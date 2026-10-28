package io.github.fanfeast.anonpdf.net

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SharedPdfInputTest {
    @get:Rule val temp = TemporaryFolder()
    private val url = "https://example.com/label?X-Amz-Signature=a%2Fb&token=abc+def"

    @Test fun textUrlWinsEvenWhenBrowserAlsoSuppliesAThumbnailStream() {
        assertEquals(url, SharedPdfInput.preferredLink("text/plain", true, url, emptyList())?.toString())
    }
    @Test fun textUrlWinsOverClipDataPreviewWithoutAnExplicitStream() {
        assertEquals(url, SharedPdfInput.preferredLink("text/plain", false, "Etykieta\\n$url", emptyList())?.toString())
    }
    @Test fun actualPdfStreamIsPreferredOverAccompanyingWebPageAddress() {
        assertNull(SharedPdfInput.preferredLink("application/pdf", true, url, emptyList()))
        assertEquals(url, SharedPdfInput.anyLink(url, emptyList())?.toString())
    }
    @Test fun clipDataTextCanProvideTheLinkWhenExtraTextIsAbsent() {
        assertEquals(url, SharedPdfInput.preferredLink("text/plain", false, null, listOf("Etykieta", url))?.toString())
    }
    @Test fun thumbnailAndHtmlAreRejectedButPdfWithoutExtensionIsAccepted() {
        val input = temp.newFile("1793210416")
        input.writeBytes(byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte()))
        assertFalse(SharedPdfInput.hasPdfHeader(input))
        input.writeText("<html>Podgląd</html>")
        assertFalse(SharedPdfInput.hasPdfHeader(input))
        input.writeText("%PDF-1.7\\n1 0 obj")
        assertTrue(SharedPdfInput.hasPdfHeader(input))
    }
}
