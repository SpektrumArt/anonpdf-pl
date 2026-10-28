package io.github.fanfeast.anonpdf.printing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import io.github.fanfeast.anonpdf.pdf.PdfOps
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

object PdfPrinting {
    /** Own snapshot survives leaving the viewer; removed when the system finishes printing. */
    suspend fun start(context: Context, input: File, name: String, password: String? = null) {
        val snapshot = withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "print").apply { mkdirs() }
            val copy = File.createTempFile("print-", ".pdf", dir)
            try {
                PdfOps.load(input, password).use { doc ->
                    require(doc.currentAccessPermission.canPrint()) { "Ten PDF nie zezwala na drukowanie." }
                    doc.isAllSecurityToBeRemoved = true
                    doc.save(copy)
                }
                copy
            } catch (t: Throwable) { copy.delete(); throw t }
        }
        try {
            val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            manager.print(name, Adapter(context, snapshot, name), null)
        } catch (t: Throwable) { snapshot.delete(); throw t }
    }

    private class Adapter(private val context: Context, private val source: File, private val name: String) : PrintDocumentAdapter() {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private var attributes: PrintAttributes? = null
        private var pageCount = 0

        override fun onLayout(old: PrintAttributes?, new: PrintAttributes, cancel: CancellationSignal,
                              callback: LayoutResultCallback, extras: Bundle?) {
            attributes = new
            scope.launch {
                try {
                    pageCount = withContext(Dispatchers.IO) {
                        ParcelFileDescriptor.open(source, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                            PdfRenderer(fd).use { it.pageCount }
                        }
                    }
                    if (cancel.isCanceled) callback.onLayoutCancelled()
                    else callback.onLayoutFinished(PrintDocumentInfo.Builder(name)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(pageCount).build(), old != new)
                } catch (t: CancellationException) { callback.onLayoutCancelled() }
                catch (t: Exception) { callback.onLayoutFailed("Nie udało się przygotować wydruku.") }
            }
        }

        override fun onWrite(ranges: Array<out PageRange>, destination: ParcelFileDescriptor,
                             cancel: CancellationSignal, callback: WriteResultCallback) {
            val settings = attributes ?: run { callback.onWriteFailed("Brak ustawień wydruku."); return }
            scope.launch {
                try {
                    val selected = (0 until pageCount).filter { index -> ranges.any { index in it.start..it.end } }
                    require(selected.isNotEmpty())
                    withContext(Dispatchers.IO) {
                        val output = PrintedPdfDocument(context, settings)
                        try {
                            ParcelFileDescriptor.open(source, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                                PdfRenderer(fd).use { renderer ->
                                    selected.forEach { index ->
                                        ensureActive()
                                        if (cancel.isCanceled) throw CancellationException()
                                        renderer.openPage(index).use { page ->
                                            // 300 dpi, capped to keep a single bitmap below ~80 MB.
                                            val pixelsPerPoint = min(300f / 72f, 4500f / maxOf(page.width, page.height))
                                            val bitmap = Bitmap.createBitmap((page.width * pixelsPerPoint).roundToInt().coerceAtLeast(1),
                                                (page.height * pixelsPerPoint).roundToInt().coerceAtLeast(1), Bitmap.Config.ARGB_8888)
                                            try {
                                                bitmap.eraseColor(Color.WHITE)
                                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                                                val sheet = output.startPage(index)
                                                val area = sheet.info.contentRect
                                                // Never enlarge: an 80% PDF stays at 80% on the chosen sheet.
                                                val scale = printScale(page.width, page.height, area.width(), area.height())
                                                val w = page.width * scale; val h = page.height * scale
                                                val x = area.left + (area.width() - w) / 2f
                                                val y = area.top + (area.height() - h) / 2f
                                                sheet.canvas.drawBitmap(bitmap, null, RectF(x, y, x + w, y + h), null)
                                                output.finishPage(sheet)
                                            } finally { bitmap.recycle() }
                                        }
                                    }
                                }
                            }
                            if (cancel.isCanceled) throw CancellationException()
                            FileOutputStream(destination.fileDescriptor).use { output.writeTo(it) }
                        } finally { output.close() }
                    }
                    if (cancel.isCanceled) callback.onWriteCancelled()
                    else callback.onWriteFinished(selected.map { PageRange(it, it) }.toTypedArray())
                } catch (t: CancellationException) { callback.onWriteCancelled() }
                catch (t: Exception) { callback.onWriteFailed("Nie udało się utworzyć wydruku.") }
            }
        }

        override fun onFinish() {
            val job = scope.coroutineContext[Job]!!
            job.invokeOnCompletion { source.delete() }
            scope.cancel()
        }
    }
}
