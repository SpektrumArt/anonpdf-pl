package io.github.fanfeast.anonpdf.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The one-shot tools.
 *
 * Anything that changes individual pages — rotate, crop, scale, delete, reorder,
 * watermark, page numbers — lives in the editor instead, where the result can be
 * previewed and several changes combined before saving. What is left here are the
 * jobs that act on whole files, or that produce something other than one PDF.
 */
enum class ToolId {
    MERGE,
    SPLIT,
    EXTRACT,
    COMPRESS,
    PDF_TO_IMAGES,
    IMAGES_TO_PDF,
    EXTRACT_TEXT,
    PROTECT,
    UNLOCK,
    SIGN,
}

/** What the tool needs the user to hand it. */
enum class ToolInput { SINGLE_PDF, MULTIPLE_PDF, IMAGES }

/** What the tool produces, which decides how we offer to save it. */
enum class ToolOutput { PDF, TEXT, MANY_FILES }

enum class ToolGroup(val label: String) {
    FILES("Całe pliki"),
    CONVERT("Konwersja i kompresja"),
    MARKUP("Adnotacje"),
    SECURITY("Zabezpieczenia"),
}

data class ToolSpec(
    val id: ToolId,
    val title: String,
    val summary: String,
    val icon: ImageVector,
    val group: ToolGroup,
    val input: ToolInput,
    val output: ToolOutput,
    /** True when the tool has a bespoke screen instead of the generic run flow. */
    val hasCustomScreen: Boolean = false,
    /** Shown as a caveat before running, when there is an honest trade-off. */
    val caveat: String? = null,
)

object ToolCatalog {

    val all: List<ToolSpec> = listOf(
        ToolSpec(
            id = ToolId.MERGE,
            title = "Połącz pliki PDF",
            summary = "Połącz kilka plików w jeden w wybranej kolejności",
            icon = Icons.AutoMirrored.Filled.MergeType,
            group = ToolGroup.FILES,
            input = ToolInput.MULTIPLE_PDF,
            output = ToolOutput.PDF,
        ),
        ToolSpec(
            id = ToolId.SPLIT,
            title = "Podziel PDF",
            summary = "Podziel dokument na pliki według zakresów stron",
            icon = Icons.Filled.ContentCut,
            group = ToolGroup.FILES,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.MANY_FILES,
        ),
        ToolSpec(
            id = ToolId.EXTRACT,
            title = "Wyodrębnij strony",
            summary = "Zapisz wybrane strony w nowym pliku PDF",
            icon = Icons.Filled.FilterNone,
            group = ToolGroup.FILES,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.PDF,
        ),
        ToolSpec(
            id = ToolId.COMPRESS,
            title = "Kompresuj PDF",
            summary = "Zmniejsz plik przez ponowne zakodowanie obrazów",
            icon = Icons.Filled.Compress,
            group = ToolGroup.CONVERT,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.PDF,
        ),
        ToolSpec(
            id = ToolId.PDF_TO_IMAGES,
            title = "PDF na obrazy",
            summary = "Zapisz każdą stronę jako JPG lub PNG",
            icon = Icons.Filled.Image,
            group = ToolGroup.CONVERT,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.MANY_FILES,
        ),
        ToolSpec(
            id = ToolId.IMAGES_TO_PDF,
            title = "Obrazy na PDF",
            summary = "Utwórz PDF ze zdjęć lub skanów",
            icon = Icons.Filled.PictureAsPdf,
            group = ToolGroup.CONVERT,
            input = ToolInput.IMAGES,
            output = ToolOutput.PDF,
        ),
        ToolSpec(
            id = ToolId.EXTRACT_TEXT,
            title = "Wyodrębnij tekst",
            summary = "Zapisz tekst w pliku .txt",
            icon = Icons.AutoMirrored.Filled.Article,
            group = ToolGroup.CONVERT,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.TEXT,
            caveat = "Wykrywa tylko właściwy tekst. Skany są obrazami, więc " +
                "wynik będzie pusty. Ich odczyt wymaga OCR, którego AnonPDF nie obsługuje.",
        ),
        ToolSpec(
            id = ToolId.SIGN,
            title = "Podpisz PDF",
            summary = "Narysuj podpis i umieść go na stronie",
            icon = Icons.Filled.Draw,
            group = ToolGroup.MARKUP,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.PDF,
            hasCustomScreen = true,
            caveat = "Umieszcza obraz podpisu na stronie. " +
                "Nie jest to kryptograficzny podpis cyfrowy.",
        ),
        ToolSpec(
            id = ToolId.PROTECT,
            title = "Zabezpiecz PDF",
            summary = "Ustaw hasło z szyfrowaniem AES-256",
            icon = Icons.Filled.Lock,
            group = ToolGroup.SECURITY,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.PDF,
        ),
        ToolSpec(
            id = ToolId.UNLOCK,
            title = "Usuń hasło",
            summary = "Usuń zabezpieczenie PDF, którego hasło znasz",
            icon = Icons.Filled.LockOpen,
            group = ToolGroup.SECURITY,
            input = ToolInput.SINGLE_PDF,
            output = ToolOutput.PDF,
        ),
    )

    private val byId = all.associateBy { it.id }

    fun spec(id: ToolId): ToolSpec = byId.getValue(id)

    val grouped: List<Pair<ToolGroup, List<ToolSpec>>> =
        ToolGroup.entries.map { group -> group to all.filter { it.group == group } }
            .filter { it.second.isNotEmpty() }
}
