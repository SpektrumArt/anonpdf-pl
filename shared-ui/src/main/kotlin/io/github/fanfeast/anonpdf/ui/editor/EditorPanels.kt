package io.github.fanfeast.anonpdf.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.fanfeast.anonpdf.pdf.CropInsets
import io.github.fanfeast.anonpdf.pdf.MarkRect
import io.github.fanfeast.anonpdf.pdf.NumberPosition
import io.github.fanfeast.anonpdf.pdf.PageOrientation
import io.github.fanfeast.anonpdf.pdf.PaperSize
import io.github.fanfeast.anonpdf.pdf.ResizeTarget
import io.github.fanfeast.anonpdf.pdf.PageNumberOptions
import io.github.fanfeast.anonpdf.pdf.TextMark
import io.github.fanfeast.anonpdf.pdf.WatermarkLayout
import io.github.fanfeast.anonpdf.pdf.WatermarkOptions
import io.github.fanfeast.anonpdf.ui.components.ChoiceChips
import io.github.fanfeast.anonpdf.ui.components.LabeledSlider
import kotlin.math.roundToInt

/**
 * Shared shell for the tool panels.
 *
 * These live in the control area rather than in a dialog, deliberately: every
 * control here changes the page, and a modal would cover the very preview the
 * user needs to watch while dragging a slider.
 */
@Composable
private fun PanelFrame(
    title: String,
    subtitle: String?,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    applyLabel: String = "Zastosuj",
    applyEnabled: Boolean = true,
    onRemove: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (subtitle != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        // Takes what is left and scrolls, so the Cancel/Apply row below stays put
        // however far the drawer is pulled down.
        Column(
            Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
        ) { content() }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onRemove != null) {
                OutlinedButton(onClick = onRemove) { Text("Usuń") }
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = onCancel) { Text("Anuluj") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onApply, enabled = applyEnabled) { Text(applyLabel) }
        }
    }
}

@Composable
fun CropPanel(
    insets: CropInsets,
    targetLabel: String,
    onChange: (CropInsets) -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Przytnij",
        subtitle = targetLabel,
        applyLabel = "Zastosuj przycięcie",
        onCancel = onCancel,
        onApply = onApply,
    ) {
        Text(
            "Przeciągnij ramkę na stronie powyżej. Krawędzie są mierzone zgodnie z " +
                "widokiem strony, również po jej obróceniu.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "L ${pct(insets.left)}   T ${pct(insets.top)}   " +
                "R ${pct(insets.right)}   B ${pct(insets.bottom)}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onChange(CropInsets(0.06f, 0.06f, 0.06f, 0.06f)) }) {
                Text("6% z każdej strony")
            }
            OutlinedButton(onClick = { onChange(CropInsets()) }) { Text("Wyczyść") }
        }
    }
}

/** Whether the page is being put on a named sheet or just scaled by a number. */
enum class SizeMode(val label: String) {
    PAPER("Format papieru"),
    PERCENT("Procentowo"),
}

@Composable
fun ResizePanel(
    mode: SizeMode,
    factor: Float,
    target: ResizeTarget,
    targetLabel: String,
    resultingSize: String,
    onModeChange: (SizeMode) -> Unit,
    onFactorChange: (Float) -> Unit,
    onTargetChange: (ResizeTarget) -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Zmień rozmiar",
        subtitle = targetLabel,
        onCancel = onCancel,
        onApply = onApply,
    ) {
        ChoiceChips(
            label = "",
            options = SizeMode.entries,
            selected = mode,
            onSelect = onModeChange,
            optionLabel = { it.label },
        )
        Spacer(Modifier.height(10.dp))

        when (mode) {
            SizeMode.PAPER -> {
                Text(
                    "Umieszcza stronę na arkuszu o wybranym rozmiarze. Podgląd powyżej " +
                        "pokazuje wynik.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(10.dp))
                ChoiceChips(
                    label = "Arkusz",
                    options = PaperSize.entries,
                    selected = target.paper,
                    onSelect = { onTargetChange(target.copy(paper = it)) },
                    optionLabel = { it.label },
                )
                Spacer(Modifier.height(10.dp))
                ChoiceChips(
                    label = "Orientacja",
                    options = PageOrientation.entries,
                    selected = target.orientation,
                    onSelect = { onTargetChange(target.copy(orientation = it)) },
                    optionLabel = { it.label },
                )
                Spacer(Modifier.height(10.dp))
                ChoiceChips(
                    label = "Gdy proporcje się różnią",
                    options = listOf(false, true),
                    selected = target.fill,
                    onSelect = { onTargetChange(target.copy(fill = it)) },
                    optionLabel = { if (it) "Wypełnij, przytnij wystające fragmenty" else "Dopasuj, pozostaw marginesy" },
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    resultingSize,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    target.paper.physical,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            SizeMode.PERCENT -> {
                Text(
                    "Zmienia rozmiar strony, zachowując jej proporcje. Podgląd " +
                        "powyżej zmienia się podczas przesuwania suwaka.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                LabeledSlider(
                    label = "Rozmiar",
                    value = factor,
                    onValueChange = onFactorChange,
                    valueRange = 0.25f..2f,
                    valueLabel = "${(factor * 100).roundToInt()}%",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.5f, 0.75f, 1f, 1.5f).forEach { preset ->
                        OutlinedButton(onClick = { onFactorChange(preset) }) {
                            Text("${(preset * 100).roundToInt()}%")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    resultingSize,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun WatermarkPanel(
    options: WatermarkOptions,
    hasExisting: Boolean,
    onChange: (WatermarkOptions) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Znak wodny",
        subtitle = "każda strona",
        onCancel = onCancel,
        onApply = onApply,
        applyEnabled = options.text.isNotBlank(),
        onRemove = if (hasExisting) onRemove else null,
    ) {
        OutlinedTextField(
            value = options.text,
            onValueChange = { onChange(options.copy(text = it)) },
            label = { Text("Tekst") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        ChoiceChips(
            label = "Położenie",
            options = WatermarkLayout.entries,
            selected = options.layout,
            onSelect = { onChange(options.copy(layout = it)) },
            optionLabel = {
                when (it) {
                    WatermarkLayout.DIAGONAL -> "Ukośnie"
                    WatermarkLayout.CENTER -> "Na środku"
                    WatermarkLayout.TILED -> "Kafelkowo"
                    WatermarkLayout.TOP -> "U góry"
                    WatermarkLayout.BOTTOM -> "U dołu"
                }
            },
        )
        Spacer(Modifier.height(12.dp))
        LabeledSlider(
            label = "Krycie",
            value = options.opacity,
            onValueChange = { onChange(options.copy(opacity = it)) },
            valueRange = 0.05f..1f,
            valueLabel = "${(options.opacity * 100).roundToInt()}%",
        )
        LabeledSlider(
            label = "Rozmiar tekstu",
            value = options.fontSize,
            onValueChange = { onChange(options.copy(fontSize = it)) },
            valueRange = 12f..140f,
            valueLabel = "${options.fontSize.roundToInt()} pt",
        )
    }
}

@Composable
fun PageNumbersPanel(
    pageCount: Int,
    options: PageNumberOptions,
    hasExisting: Boolean,
    onChange: (PageNumberOptions) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Numery stron",
        subtitle = "każda strona",
        onCancel = onCancel,
        onApply = onApply,
        applyEnabled = options.format.contains("{n}"),
        onRemove = if (hasExisting) onRemove else null,
    ) {
        OutlinedTextField(
            value = options.format,
            onValueChange = { onChange(options.copy(format = it)) },
            label = { Text("Format") },
            supportingText = { Text("{n} oznacza numer, {total} — łączną liczbę") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("{n}", "{n} / {total}", "Strona {n}").forEach { preset ->
                OutlinedButton(onClick = { onChange(options.copy(format = preset)) }) {
                    Text(preset, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        ChoiceChips(
            label = "Pozycja",
            options = NumberPosition.entries,
            selected = options.position,
            onSelect = { onChange(options.copy(position = it)) },
            optionLabel = {
                when (it) {
                    NumberPosition.TOP_LEFT -> "U góry po lewej"
                    NumberPosition.TOP_CENTER -> "U góry na środku"
                    NumberPosition.TOP_RIGHT -> "U góry po prawej"
                    NumberPosition.BOTTOM_LEFT -> "U dołu po lewej"
                    NumberPosition.BOTTOM_CENTER -> "U dołu na środku"
                    NumberPosition.BOTTOM_RIGHT -> "U dołu po prawej"
                }
            },
        )
        Spacer(Modifier.height(12.dp))
        LabeledSlider(
            label = "Zacznij numerację od",
            value = options.startNumber.toFloat(),
            onValueChange = { onChange(options.copy(startNumber = it.roundToInt())) },
            valueRange = 0f..50f,
            valueLabel = options.startNumber.toString(),
        )
        LabeledSlider(
            label = "Pomiń początkowe strony",
            value = options.firstPageIndex.toFloat(),
            onValueChange = { onChange(options.copy(firstPageIndex = it.roundToInt())) },
            valueRange = 0f..(pageCount - 1).coerceAtLeast(1).toFloat(),
            valueLabel = options.firstPageIndex.toString(),
        )
        LabeledSlider(
            label = "Rozmiar tekstu",
            value = options.fontSize,
            onValueChange = { onChange(options.copy(fontSize = it)) },
            valueRange = 7f..24f,
            valueLabel = "${options.fontSize.roundToInt()} pt",
        )
    }
}

@Composable
fun TextPanel(
    mark: TextMark,
    placed: Boolean,
    existingCount: Int,
    onChange: (TextMark) -> Unit,
    onClearPage: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Dodaj tekst",
        subtitle = "strona ${mark.sourceIndex + 1}",
        applyEnabled = placed && mark.text.isNotBlank(),
        onCancel = onCancel,
        onApply = onApply,
        onRemove = if (existingCount > 0) onClearPage else null,
    ) {
        Text(
            if (placed) {
                "Przeciągnij znacznik na stronie, aby przesunąć tekst."
            } else {
                "Dotknij strony powyżej, aby wskazać początek tekstu."
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = mark.text,
            onValueChange = { onChange(mark.copy(text = it)) },
            label = { Text("Tekst") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        LabeledSlider(
            label = "Rozmiar",
            value = mark.fontSize,
            onValueChange = { onChange(mark.copy(fontSize = it)) },
            valueRange = 6f..48f,
            valueLabel = "${mark.fontSize.roundToInt()} pt",
        )
        ChoiceChips(
            label = "Kolor",
            options = InkColours,
            selected = InkColours.firstOrNull { it.second == mark.color } ?: InkColours.first(),
            onSelect = { onChange(mark.copy(color = it.second)) },
            optionLabel = { it.first },
        )
        Spacer(Modifier.height(8.dp))
        ChoiceChips(
            label = "Krój pisma",
            options = listOf(false, true),
            selected = mark.bold,
            onSelect = { onChange(mark.copy(bold = it)) },
            optionLabel = { if (it) "Pogrubiony" else "Zwykły" },
        )
        if (existingCount > 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Liczba elementów dodanych do tej strony: $existingCount. " +
                    "Przycisk „Usuń” usuwa je wszystkie.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun FillPanel(
    highlight: Boolean,
    rect: MarkRect?,
    colour: Int,
    pageNumber: Int,
    existingCount: Int,
    onColourChange: (Int) -> Unit,
    onReset: () -> Unit,
    onClearPage: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = if (highlight) "Podświetl" else "Zakryj",
        subtitle = "strona $pageNumber",
        applyEnabled = rect != null && !rect.tidied().isDegenerate,
        onCancel = onCancel,
        onApply = onApply,
        onRemove = if (existingCount > 0) onClearPage else null,
    ) {
        Text(
            if (highlight) {
                "Przeciągnij po tekście, który chcesz podświetlić. Przeciągnij wewnątrz ramki, aby " +
                    "ją przesunąć."
            } else {
                "Narysuj ramkę nad treścią do usunięcia. Zakryta treść jest " +
                    "trwale usuwana: strona zostaje zapisana jako obraz, więc tekstu " +
                    "nie można już zaznaczać ani wyszukiwać. Aby zmienić tekst, zakryj " +
                    "go i dodaj własny na wierzchu."
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))
        val palette = if (highlight) HighlightColours else WhiteoutColours
        ChoiceChips(
            label = "Kolor",
            options = palette,
            selected = palette.firstOrNull { it.second == colour } ?: palette.first(),
            onSelect = { onColourChange(it.second) },
            optionLabel = { it.first },
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onReset) { Text("Zacznij ponownie") }
        }
    }
}

@Composable
fun InkPanel(
    strokeCount: Int,
    colour: Int,
    widthRatio: Float,
    pageNumber: Int,
    existingCount: Int,
    onColourChange: (Int) -> Unit,
    onWidthChange: (Float) -> Unit,
    onUndoStroke: () -> Unit,
    onClearStrokes: () -> Unit,
    onClearPage: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    PanelFrame(
        title = "Rysuj",
        subtitle = "strona $pageNumber",
        applyEnabled = strokeCount > 0,
        onCancel = onCancel,
        onApply = onApply,
        onRemove = if (existingCount > 0) onClearPage else null,
    ) {
        Text(
            "Powiększ przyciskiem + lub wybierz „Przesuń stronę”, aby używać gestów. Wznów edycję, aby rysować.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        ChoiceChips(
            label = "Kolor",
            options = InkColours,
            selected = InkColours.firstOrNull { it.second == colour } ?: InkColours.first(),
            onSelect = { onColourChange(it.second) },
            optionLabel = { it.first },
        )
        Spacer(Modifier.height(8.dp))
        LabeledSlider(
            label = "Grubość",
            value = widthRatio,
            onValueChange = onWidthChange,
            valueRange = 0.001f..0.02f,
            valueLabel = "${(widthRatio * 1000).roundToInt()}",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onUndoStroke, enabled = strokeCount > 0) {
                Text("Cofnij kreskę")
            }
            OutlinedButton(onClick = onClearStrokes, enabled = strokeCount > 0) {
                Text("Wyczyść")
            }
        }
    }
}

/** Small named palettes, so the pickers stay short enough to be useful. */
val InkColours: List<Pair<String, Int>> = listOf(
    "Czarny" to 0xFF1A1A1A.toInt(),
    "Niebieski" to 0xFF1B5FC1.toInt(),
    "Czerwony" to 0xFFC62828.toInt(),
    "Zielony" to 0xFF2E7D32.toInt(),
    "Biały" to 0xFFFFFFFF.toInt(),
)

val HighlightColours: List<Pair<String, Int>> = listOf(
    "Żółty" to 0xFFFFEB3B.toInt(),
    "Zielony" to 0xFF8BC34A.toInt(),
    "Niebieski" to 0xFF64B5F6.toInt(),
    "Różowy" to 0xFFF48FB1.toInt(),
)

val WhiteoutColours: List<Pair<String, Int>> = listOf(
    "Biały" to 0xFFFFFFFF.toInt(),
    "Czarny" to 0xFF000000.toInt(),
    "Szary" to 0xFF9E9E9E.toInt(),
)

/** The running list of edits, so the user can see and unwind what they have done. */
@Composable
fun PendingEditsDialog(
    descriptions: List<String>,
    onDismiss: () -> Unit,
    onUndoAll: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Oczekujące zmiany (${descriptions.size})") },
        text = {
            if (descriptions.isEmpty()) {
                Text("Brak zmian.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                    descriptions.forEachIndexed { index, description ->
                        Text(
                            "${index + 1}.  $description",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Zmiany nie zostały jeszcze zapisane. Oryginalny plik pozostaje bez zmian " +
                            "do chwili zapisu.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Zamknij") } },
        dismissButton = {
            if (descriptions.isNotEmpty()) {
                TextButton(onClick = onUndoAll) { Text("Cofnij wszystko") }
            }
        },
    )
}

private fun pct(value: Float) = "${(value * 100).roundToInt()}%"

/** Where merged-in pages land, relative to the document as it now stands. */
enum class InsertPosition(val label: String) {
    AFTER("Za tą stroną"),
    START("Na początku"),
    END("Na końcu"),
}

/**
 * Confirms a merge. The pages do not become special: once inserted they are
 * ordinary pages in the plan, open to every other tool, which is the whole
 * point of merging here instead of in a one-shot tool.
 */
@Composable
fun InsertPanel(
    docs: List<Pair<String, Int>>,
    where: InsertPosition,
    onWhereChange: (InsertPosition) -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    val total = docs.sumOf { it.second }
    PanelFrame(
        title = "Połącz",
        subtitle = "Liczba stron do wstawienia: $total",
        applyLabel = "Wstaw",
        applyEnabled = docs.isNotEmpty(),
        onCancel = onCancel,
        onApply = onApply,
    ) {
        docs.forEach { (name, count) ->
            Text(
                "$name — liczba stron: $count",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(10.dp))
        ChoiceChips(
            label = "Miejsce wstawienia",
            options = InsertPosition.entries,
            selected = where,
            onSelect = onWhereChange,
            optionLabel = { it.label },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Po wstawieniu możesz obracać strony, zmieniać ich kolejność, przycinać, oznaczać lub " +
                "usuwać tak samo jak pozostałe, przed zapisaniem.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
