@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fanfeast.anonpdf.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.fanfeast.anonpdf.BuildConfig
import io.github.fanfeast.anonpdf.ui.components.AnonTopBar
import io.github.fanfeast.anonpdf.ui.components.InfoNote
import io.github.fanfeast.anonpdf.ui.components.SectionLabel

private const val SOURCE_URL = "https://github.com/SpektrumArt/anonpdf-pl"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(topBar = { AnonTopBar(title = "O aplikacji", onBack = onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text("AnonPDF", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Wersja ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Czytnik i edytor PDF, który przetwarza dokumenty wyłącznie na urządzeniu. " +
                    "",
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))
            SectionLabel("Internet i prywatność")
            Bullet("Ta wersja ma uprawnienie INTERNET, aby pobierać PDF z łączy HTTPS udostępnionych z przeglądarki. Każde pobranie wymaga potwierdzenia. Serwer źródłowy otrzyma żądanie, adres IP i parametry łącza.")
            Bullet("Edycja dokumentów odbywa się na urządzeniu. Aplikacja nie ma reklam, analityki ani konta i nie wysyła plików na serwer edycji. Brak dostępu do internetu nie jest już gwarancją systemową tej wersji.")
            Bullet("Drukowanie przekazuje wybrany dokument do systemowej usługi drukowania. Może ona przesłać go do wskazanej drukarki.")
            SectionLabel("Dostęp do plików")
            Bullet("Dostęp do wszystkich plików jest opcjonalny i służy do przeglądania PDF. Możesz zamiast niego używać systemowego okna wyboru.")
            Spacer(Modifier.height(12.dp))
            SectionLabel("Co zapisuje")
            Bullet(
                "Listę nazw ostatnio otwieranych plików, w prywatnej pamięci aplikacji, " +
                    "bez kopii zapasowej w chmurze. Możesz wyłączyć zapisywanie lub wyczyścić listę " +
                    "w ustawieniach.",
            )
            Bullet(
                "Robocze kopie edytowanego pliku w pamięci podręcznej aplikacji. " +
                    "Są tymczasowe i Android może je w każdej chwili usunąć.",
            )
            Bullet("Ustawienia wyświetlania. To cała lista.")

            Spacer(Modifier.height(24.dp))
            InfoNote(
                "Możesz to sprawdzić. Kod źródłowy jest publiczny, " +
                    "a lista uprawnień jest widoczna na stronie aplikacji w Google Play " +
                    "oraz w informacjach o aplikacji w Androidzie.",
            )

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, SOURCE_URL.toUri()),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Zobacz kod źródłowy") }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            SectionLabel("Licencje otwartego oprogramowania")
            Licence(
                "AnonPDF",
                "MIT Licence. Copyright (c) 2026 the AnonPDF contributors.",
            )
            Licence(
                "PdfBox-Android",
                "Apache License 2.0. Copyright the Apache Software Foundation and " +
                    "Tom Roush. Used for reading and writing PDF structure.",
            )
            Licence(
                "AndroidX and Jetpack Compose",
                "Apache License 2.0. Copyright The Android Open Source Project.",
            )
            Licence(
                "Kotlin and kotlinx.coroutines",
                "Apache License 2.0. Copyright JetBrains s.r.o.",
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Ochrona hasłem korzysta z AES-256 i systemowych bibliotek kryptograficznych " +
                    "Androida. Aplikacja nie zawiera zewnętrznej biblioteki kryptograficznej.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Bullet(text: String) {
    Text(
        "•  $text",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun Licence(name: String, detail: String) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(
            name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            detail,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
