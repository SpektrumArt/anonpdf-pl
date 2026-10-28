package io.github.fanfeast.anonpdf.net

import java.io.File
import java.net.URI

/** A text share may carry a preview image in ClipData or EXTRA_STREAM. */
object SharedPdfInput {
    fun preferredLink(mimeType: String?, hasExplicitStream: Boolean, text: String?, clipTexts: List<String>): URI? {
        if (mimeType.equals("application/pdf", true) && hasExplicitStream) return null
        return anyLink(text, clipTexts)
    }

    fun anyLink(text: String?, clipTexts: List<String>): URI? =
        sequenceOf(text.orEmpty()).plus(clipTexts.asSequence()).mapNotNull(PdfLink::find).firstOrNull()

    fun hasPdfHeader(file: File): Boolean = file.inputStream().use { input ->
        val bytes = ByteArray(1024)
        val count = input.read(bytes)
        count > 0 && String(bytes, 0, count, Charsets.ISO_8859_1).contains("%PDF-")
    }
}
