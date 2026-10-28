package io.github.fanfeast.anonpdf.net

import java.net.URI

/** Shared browser text can include a title. Preserve signed query parameters verbatim. */
object PdfLink {
    fun find(text: String): URI? = Regex("https://[^\\s<>\"]+", RegexOption.IGNORE_CASE)
        .findAll(text).mapNotNull { match ->
            runCatching { URI(match.value) }.getOrNull()?.takeIf {
                it.scheme.equals("https", true) && !it.host.isNullOrBlank() && it.rawUserInfo == null
            }
        }.firstOrNull()
}
