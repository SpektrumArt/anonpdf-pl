@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fanfeast.anonpdf.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.fanfeast.anonpdf.data.AppPreferences
import io.github.fanfeast.anonpdf.pdf.DocumentStore
import io.github.fanfeast.anonpdf.ui.components.AnonTopBar
import io.github.fanfeast.anonpdf.ui.components.InfoNote
import io.github.fanfeast.anonpdf.ui.components.SectionLabel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }
    val store = remember { DocumentStore(context) }
    val snackbarHost = remember { SnackbarHostState() }

    val rememberRecents by preferences.rememberRecents.collectAsState(initial = true)
    val invertColors by preferences.invertPdfColors.collectAsState(initial = false)
    val keepScreenOn by preferences.keepScreenOn.collectAsState(initial = false)

    Scaffold(
        topBar = { AnonTopBar(title = "Ustawienia", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHost) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            SectionLabel("Czytanie")
            SwitchRow(
                title = "Odwróć kolory stron",
                subtitle = "Ciemne tło do czytania w nocy. Zmienia tylko sposób wyświetlania " +
                    "stron, bez modyfikacji pliku.",
                checked = invertColors,
                onChange = { scope.launch { preferences.setInvertPdfColors(it) } },
            )
            SwitchRow(
                title = "Nie wygaszaj ekranu podczas czytania",
                subtitle = "Zapobiega wygaszeniu ekranu w czytniku.",
                checked = keepScreenOn,
                onChange = { scope.launch { preferences.setKeepScreenOn(it) } },
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            SectionLabel("Prywatność")
            SwitchRow(
                title = "Zapamiętuj ostatnie pliki",
                subtitle = "Zapisuje na urządzeniu nazwy maksymalnie 20 plików, aby można było " +
                    "szybko je otworzyć. Wyłączenie natychmiast usuwa listę.",
                checked = rememberRecents,
                onChange = { scope.launch { preferences.setRememberRecents(it) } },
            )

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    scope.launch {
                        preferences.clearRecents()
                        snackbarHost.showSnackbar("Wyczyszczono listę ostatnich plików.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Wyczyść ostatnie pliki") }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    scope.launch {
                        store.clearWorkspace()
                        snackbarHost.showSnackbar("Usunięto pliki tymczasowe.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Usuń tymczasowe pliki robocze") }

            Spacer(Modifier.height(20.dp))
            InfoNote(
                "To wszystkie ustawienia. " +
                    "AnonPDF nie wymaga konta, nie synchronizuje danych i nie wyświetla reklam. Ma " +
                    "dostęp do internetu wyłącznie do importu PDF na Twoje żądanie.",
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
