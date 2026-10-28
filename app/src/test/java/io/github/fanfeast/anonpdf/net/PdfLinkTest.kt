package io.github.fanfeast.anonpdf.net

import org.junit.Assert.*
import org.junit.Test

class PdfLinkTest {
    @Test fun signedDownloadWithoutExtensionIsPreserved() {
        val url = "https://example.com/download/123?X-Amz-Signature=a%2Fb%3D&token=abc+xyz"
        assertEquals(url, PdfLink.find("Etykieta przesyłki\n$url")?.toString())
    }
    @Test fun unsupportedInputsAreNotFetched() {
        listOf("http://example.com/a.pdf", "file:///secret.pdf", "javascript:alert(1)",
            "https://user:password@example.com/a.pdf", "zwykły tekst").forEach { assertNull(PdfLink.find(it)) }
    }
    @Test fun urlInAngleBracketsIsRecognized() {
        assertEquals("https://example.com/a.pdf", PdfLink.find("<https://example.com/a.pdf>")?.toString())
    }
}
