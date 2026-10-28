package io.github.fanfeast.anonpdf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import androidx.core.net.toUri
import io.github.fanfeast.anonpdf.net.PdfLink
import io.github.fanfeast.anonpdf.net.PdfDownload
import io.github.fanfeast.anonpdf.net.SharedPdfInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import io.github.fanfeast.anonpdf.pdf.DocumentStore
import io.github.fanfeast.anonpdf.ui.AnonPdfApp
import io.github.fanfeast.anonpdf.ui.theme.AnonPdfTheme

class MainActivity : ComponentActivity() {

    private var pendingUri by mutableStateOf<Uri?>(null)
    private var pendingLink by mutableStateOf<String?>(null)
    private var downloadError by mutableStateOf<String?>(null)
    private var downloading by mutableStateOf(false)
    private var downloadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // Start each launch with an empty scratch area rather than accumulating
            // working copies of documents across sessions.
            DocumentStore(this).clearWorkspace()
        }

        receive(intent)

        setContent {
            AnonPdfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AnonPdfApp(initialUri = pendingUri)
                    pendingLink?.let { link ->
                        AlertDialog(
                            onDismissRequest = { cancelDownload() },
                            title = { Text(if (downloading) "Pobieranie PDF…" else "Otworzyć PDF z internetu?") },
                            text = { Text(if (downloading) "Poczekaj na pobranie pliku. Możesz anulować."
                                else "Pobierz dokument z ${java.net.URI(link).host}. Serwer otrzyma żądanie pobrania i adres IP. Plik będzie edytowany na telefonie.") },
                            confirmButton = {
                                if (!downloading) TextButton(onClick = { download(link) }) { Text("Pobierz PDF") }
                            },
                            dismissButton = { TextButton(onClick = { cancelDownload() }) { Text("Anuluj") } },
                        )
                    }
                    downloadError?.let { message ->
                        AlertDialog(onDismissRequest = { downloadError = null },
                            title = { Text("Nie udało się otworzyć PDF") },
                            text = { Text(message) },
                            confirmButton = { TextButton(onClick = { downloadError = null }) { Text("OK") } })
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        receive(intent)
    }

    private fun cancelDownload() {
        downloadJob?.cancel()
        downloading = false
        pendingLink = null
    }

    private fun download(link: String) {
        downloading = true
        downloadJob = lifecycleScope.launch {
            try {
                val file = PdfDownload.fetch(java.net.URI(link), java.io.File(cacheDir, "work"))
                pendingUri = file.toUri()
                pendingLink = null
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                pendingLink = null
                downloadError = when (e) {
                    is java.net.SocketTimeoutException -> "Przekroczono czas pobierania. Spróbuj ponownie."
                    is IllegalArgumentException, is IllegalStateException -> e.message
                    else -> "Nie udało się pobrać dokumentu. Sprawdź połączenie lub pobierz PDF w przeglądarce i udostępnij plik."
                }
            } finally { downloading = false }
        }
    }

    /** Prefer a browser's shared URL over its preview image. Explicit PDF streams still win. */
    private fun receive(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND && intent?.action != Intent.ACTION_VIEW) return
        val stream = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
        val clip = intent.clipData
        val texts = (0 until (clip?.itemCount ?: 0)).mapNotNull { clip?.getItemAt(it)?.text?.toString() }
        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        val candidate = if (intent.action == Intent.ACTION_VIEW) intent.data
            else stream ?: clip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.uri
        val sharedLink = if (intent.action == Intent.ACTION_SEND)
            SharedPdfInput.preferredLink(intent.type, stream != null, text, texts) else null
        val fallback = SharedPdfInput.anyLink(text, texts) ?: candidate?.toString()?.let(PdfLink::find)
        cancelDownload()
        downloadError = null
        if (sharedLink != null) {
            pendingLink = sharedLink.toString()
        } else if (candidate != null && candidate.scheme in listOf("content", "file")) {
            openSharedFile(candidate, fallback)
        } else if (fallback != null) {
            pendingLink = fallback.toString()
        } else {
            downloadError = "Udostępnij plik PDF lub bezpośrednie łącze HTTPS do dokumentu."
        }
    }

    /** Copy while the browser's temporary URI grant is valid; never send thumbnails to PdfRenderer. */
    private fun openSharedFile(uri: Uri, fallback: java.net.URI?) {
        downloadJob = lifecycleScope.launch {
            var copy: java.io.File? = null
            try {
                val store = DocumentStore(this@MainActivity)
                copy = store.materialize(uri, store.displayName(uri))
                val valid = withContext(Dispatchers.IO) { SharedPdfInput.hasPdfHeader(copy!!) }
                if (valid) {
                    pendingUri = copy!!.toUri()
                    copy = null // Owned by the workspace until its next cleanup.
                } else if (fallback != null) {
                    pendingLink = fallback.toString()
                } else {
                    downloadError = "Przeglądarka przekazała podgląd lub dane, które nie są plikiem PDF. Pobierz PDF przyciskiem ze strzałką w przeglądarce, a następnie otwórz pobrany plik w AnonPDF."
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                if (fallback != null) pendingLink = fallback.toString()
                else downloadError = "Nie udało się odczytać udostępnionego pliku. Pobierz PDF w przeglądarce i otwórz go ponownie w AnonPDF."
            } finally { copy?.delete() }
        }
    }
}
