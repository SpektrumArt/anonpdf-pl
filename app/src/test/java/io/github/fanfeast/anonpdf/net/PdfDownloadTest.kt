package io.github.fanfeast.anonpdf.net

import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PdfDownloadTest {
    @get:Rule val temp = TemporaryFolder()
    private class Reply(val status: Int = 200, val data: String = "%PDF-1.7\n", val location: String? = null,
                        val length: Long = data.length.toLong()) : HttpURLConnection(URI("https://example.com").toURL()) {
        var closed = false
        override fun connect() {}
        override fun disconnect() { closed = true }
        override fun usingProxy() = false
        override fun getResponseCode() = status
        override fun getContentLengthLong() = length
        override fun getHeaderField(name: String?) = if (name == "Location") location else null
        override fun getInputStream() = ByteArrayInputStream(data.toByteArray())
    }
    @Test fun signedRedirectDownloadsWithoutChangingQuery() = runBlocking {
        val start = URI("https://example.com/download")
        val signed = "https://cdn.example.com/object?signature=a%2Fb&token=abc+def"
        val replies = listOf(Reply(status = 302, location = signed), Reply())
        val visited = mutableListOf<URI>()
        val result = PdfDownload.fetch(start, temp.root) { uri -> visited.add(uri); replies[visited.lastIndex] }
        assertEquals(listOf(start, URI(signed)), visited)
        assertTrue(result.readText().startsWith("%PDF-"))
        assertTrue(replies.all { it.closed })
    }
    @Test fun htmlIsRejectedAndPartialFileIsRemoved() = runBlocking {
        val reply = Reply(data = "<html>logowanie</html>")
        assertTrue(runCatching { PdfDownload.fetch(URI("https://example.com"), temp.root) { reply } }.isFailure)
        assertEquals(0, temp.root.listFiles()!!.size)
        assertTrue(reply.closed)
    }
    @Test fun oversizedResponseIsRejectedBeforeReading() = runBlocking {
        val reply = Reply(length = 51L * 1024 * 1024)
        assertTrue(runCatching { PdfDownload.fetch(URI("https://example.com"), temp.root) { reply } }.isFailure)
        assertEquals(0, temp.root.listFiles()!!.size)
    }
    @Test fun insecureRedirectIsNotOpened() = runBlocking {
        var requests = 0
        assertTrue(runCatching {
            PdfDownload.fetch(URI("https://example.com"), temp.root) {
                requests++; Reply(status = 302, location = "http://example.com/file.pdf")
            }
        }.isFailure)
        assertEquals(1, requests)
        assertEquals(0, temp.root.listFiles()!!.size)
    }
    @Test fun expiredLinkGivesReadableError() = runBlocking {
        val result = runCatching { PdfDownload.fetch(URI("https://example.com"), temp.root) { Reply(status = 403) } }
        assertTrue(result.exceptionOrNull()!!.message!!.contains("wygasło"))
    }
}
