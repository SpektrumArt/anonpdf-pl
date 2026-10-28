package io.github.fanfeast.anonpdf.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.UUID

/** Explicit GET only: no cookies, credentials, document uploads or background synchronization. */
object PdfDownload {
    private const val MAX_BYTES = 50L * 1024 * 1024

    suspend fun fetch(
        link: URI,
        cache: File,
        open: (URI) -> HttpURLConnection = { it.toURL().openConnection() as HttpURLConnection },
    ): File = withContext(Dispatchers.IO) {
        cache.mkdirs()
        val output = File(cache, "internet-${UUID.randomUUID()}.pdf")
        var completed = false
        try {
            var current = link
            repeat(6) { hop ->
                currentCoroutineContext().ensureActive()
                require(current.scheme.equals("https", true) && current.rawUserInfo == null &&
                    !current.host.isNullOrBlank()) { "Obsługiwane są tylko bezpośrednie łącza HTTPS." }
                val connection = open(current)
                try {
                    connection.instanceFollowRedirects = false
                    connection.connectTimeout = 15_000
                    connection.readTimeout = 20_000
                    connection.useCaches = false
                    connection.setRequestProperty("Accept", "application/pdf, application/octet-stream;q=0.9")
                    val status = connection.responseCode
                    if (status in listOf(301, 302, 303, 307, 308)) {
                        require(hop < 5) { "Zbyt wiele przekierowań." }
                        val location = connection.getHeaderField("Location")
                            ?: error("Brak adresu przekierowania.")
                        current = current.resolve(location)
                    } else {
                        require(status == 200) {
                            if (status == 401 || status == 403) "Łącze wygasło lub wymaga logowania. Pobierz PDF w przeglądarce i udostępnij plik."
                            else "Nie udało się pobrać PDF (HTTP $status)."
                        }
                        require(connection.contentLengthLong <= MAX_BYTES) { "Plik przekracza limit 50 MB." }
                        connection.inputStream.use { input ->
                            output.outputStream().use { out ->
                                val buffer = ByteArray(32 * 1024)
                                var total = 0L
                                while (true) {
                                    currentCoroutineContext().ensureActive()
                                    val read = input.read(buffer)
                                    if (read < 0) break
                                    total += read
                                    require(total <= MAX_BYTES) { "Plik przekracza limit 50 MB." }
                                    out.write(buffer, 0, read)
                                }
                            }
                        }
                        val header = output.inputStream().use { input ->
                            val buffer = ByteArray(1024)
                            val read = input.read(buffer)
                            if (read > 0) buffer.copyOf(read) else byteArrayOf()
                        }
                        require(header.toString(Charsets.ISO_8859_1).contains("%PDF-")) {
                            "Łącze nie zwróciło pliku PDF. Pobierz dokument w przeglądarce i udostępnij plik."
                        }
                        currentCoroutineContext().ensureActive()
                        completed = true
                        return@withContext output
                    }
                } finally { connection.disconnect() }
            }
            error("Zbyt wiele przekierowań.")
        } finally { if (!completed) output.delete() }
    }
}
