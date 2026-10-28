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

    /** File shares and text links are separate: remote addresses never enter DocumentStore. */
    private fun receive(intent: Intent?) {
        val candidate = when (intent?.action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
                ?: intent.clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.uri
            else -> null
        }
        if (candidate != null && candidate.scheme in listOf("content", "file")) {
            cancelDownload()
            pendingUri = candidate
            return
        }
        if (intent?.action != Intent.ACTION_SEND && intent?.action != Intent.ACTION_VIEW) return
        val text = candidate?.toString() ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString().orEmpty()
        val link = PdfLink.find(text)
        cancelDownload()
        if (link != null) pendingLink = link.toString()
        else downloadError = "Udostępnij plik PDF lub bezpośrednie łącze HTTPS do dokumentu."
    }
}
