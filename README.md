# AnonPDF

[![Kompilacja](https://github.com/FanFeast/anonpdf/actions/workflows/build.yml/badge.svg)](https://github.com/FanFeast/anonpdf/actions/workflows/build.yml)
[![Licencja: MIT](https://img.shields.io/badge/licence-MIT-blue.svg)](LICENSE)
[![Android 12+](https://img.shields.io/badge/Android-12%2B-3DDC84.svg)](#wymagania)
[![Import HTTPS](https://img.shields.io/badge/network-HTTPS_import-blue.svg)](#gwarancja-prywatności-i-jak-ją-sprawdzić)

Czytnik i edytor PDF na Androida, który przetwarza dokumenty wyłącznie na Twoim urządzeniu.

**Wariant PL 1.02: import PDF z łączy HTTPS i drukowanie. Bez reklam, kont i analityki.**

W przeglądarce wybierz **Udostępnij → AnonPDF**, potwierdź pobranie PDF i użyj **Drukuj**. Po zmianie rozmiaru w edytorze wybierz **Drukuj** na karcie wyniku. Szczegóły: [instrukcja 1.01](docs/Internet-drukowanie-1.01.md).

Poprawka udostępniania 1.02: link tekstowy jest wybierany przed miniaturą podglądu. [Szczegóły](docs/Poprawka-udostepniania-1.02.md).

Ta wersja ma uprawnienie INTERNET. Poprzednia gwarancja braku połączeń sieciowych nie obowiązuje. Pliki są edytowane lokalnie; import pobiera wskazany dokument dopiero po potwierdzeniu. Serwer źródłowy otrzymuje żądanie, parametry adresu i adres IP. Drukowanie przekazuje PDF do wybranej usługi drukowania.

AnonPDF PL jest polskim wariantem otwartego projektu [FanFeast/anonpdf](https://github.com/FanFeast/anonpdf). Dodaje import internetowy i systemowe drukowanie do lokalnego edytora PDF.

| Biblioteka | Czytanie | Twoje pliki |
|---|---|---|
| ![Ekran główny](docs/screenshots/home.png) | ![Czytnik](docs/screenshots/viewer.png) | ![Przeglądanie plików](docs/screenshots/browse.png) |

## Edytor

Tutaj wykonasz większość pracy. Wybierz strony, wprowadź dowolną liczbę zmian, obserwuj aktualizowany podgląd rzeczywistego wyniku, a na koniec zapisz plik. Zmiany nie są zapisywane do dokumentu przed wybraniem zapisu, a oryginał nigdy nie jest modyfikowany.

| Edycja | Przeciąganie zmienia kolejność | Zwijanie narzędzi |
|---|---|---|
| ![Edytor](docs/screenshots/editor.png) | ![Zmiana kolejności](docs/screenshots/editor-reorder.png) | ![Zwinięty panel](docs/screenshots/editor-collapsed.png) |

| Zakryj treść i wpisz nową | Podświetlaj i rysuj | Połącz i edytuj dalej |
|---|---|---|
| ![Dodawanie tekstu](docs/screenshots/editor-text.png) | ![Rysowanie](docs/screenshots/editor-draw.png) | ![Łączenie](docs/screenshots/editor-merge.png) |

| Przycinanie z podglądem | Dopasowanie do A4, Letter i innych formatów | Znak wodny |
|---|---|---|
| ![Przycinanie](docs/screenshots/editor-crop.png) | ![Format papieru](docs/screenshots/editor-paper.png) | ![Znak wodny](docs/screenshots/editor-watermark.png) |

Obracaj, przycinaj, skaluj, usuwaj i przywracaj strony, odwracaj ich kolejność, dodawaj tekst, zakrywaj treść, podświetlaj, rysuj oraz dodawaj znak wodny i numery stron. Każdą operację możesz zastosować do **tej strony**, **zaznaczonych stron** lub **wszystkich stron**. Całą historię zmian można cofać i ponawiać.

- **Wszystkie narzędzia są w jednym miejscu**, na czterech przesuwanych kartach: *Strony*, *Zawartość*, *Cały plik* oraz *Konwersja i ochrona*. Narzędzia działające na całym pliku otrzymują dokument z już zastosowanymi oczekującymi zmianami. Polecenie „usuń trzy strony, a potem skompresuj” daje więc dokładnie taki wynik, jakiego oczekujesz.
- **Łączenie wstawia strony we wskazanym miejscu.** Wybierz jeden lub kilka innych plików PDF. Ich strony zostaną wstawione za bieżącą stroną, na początku albo na końcu dokumentu jako zwykłe strony na pasku miniatur. Zmieniaj ich kolejność, obracaj, przycinaj, oznaczaj lub usuwaj, a potem zapisz wszystko jednym razem. Nie trzeba przechodzić do osobnego ekranu łączenia ani ponownie otwierać wyniku.
- **Panel narzędzi można wysuwać i zwijać.** Przeciągnij uchwyt w górę, aby go powiększyć, albo dotknij go, aby schować narzędzia i czytać stronę na pełnej wysokości.
- **Edytuj także zawartość stron.** Zakryj fragment i wpisz własny tekst na wierzchu, podświetl akapit lub narysuj coś odręcznie. Istniejącego tekstu PDF nie można przepisać bezpośrednio bez osadzonych czcionek dokumentu — aplikacja zmienia go przez zakrycie i wpisanie nowego. Zakrywanie rzeczywiście usuwa treść pod zaznaczonym obszarem: strona zostaje spłaszczona do obrazu, więc ukrytego tekstu nie można później kopiować, wyszukiwać ani wyodrębniać. Oznacza to jednak, że pozostały tekst na tej stronie również przestaje być możliwy do zaznaczenia.
- **Zmieniaj kolejność przez przeciąganie.** Przytrzymaj miniaturę i przeciągnij ją. W miejscu wstawienia pojawi się przerwa, a pasek przewinie się automatycznie po dotarciu do krawędzi.
- **Zaznaczaj wiele stron dotknięciami.** Wybierz zakres *Zaznaczone*. Każde dotknięcie zaznacza lub odznacza stronę.
- **Opcje pojawiają się pod podglądem**, zamiast go zasłaniać. Możesz przesuwać suwak i obserwować zmiany na stronie.
- **Podgląd pokazuje zmianę rozmiaru bez ponownego dopasowania.** Jego skala odnosi się do pierwotnego rozmiaru strony. Skalowanie do 50% zmniejszy więc jej szerokość o połowę, zamiast ponownie wypełnić ekran.

Podgląd nie jest przybliżeniem. Tworzy go ten sam kod, który zapisuje plik wynikowy. Test renderuje oba wyniki i sprawdza ich zgodność piksel po pikselu.

## Gwarancja prywatności i jak ją sprawdzić

Wariant PL 1.01 deklaruje INTERNET do importu PDF oraz opcjonalne MANAGE_EXTERNAL_STORAGE do przeglądania plików. AndroidX dodaje uprawnienie chroniące prywatny odbiornik aplikacji. Aplikacja nie zawiera analityki ani reklam. Kopie zapasowe są wyłączone.

Pobranie wymaga potwierdzenia, obsługuje HTTPS, najwyżej pięć przekierowań i pliki do 50 MB. Nie korzysta z sesji przeglądarki. Brak uprawnienia INTERNET nie jest już gwarancją systemową tego wariantu. Aktualne zasady opisuje [PRIVACY.md](PRIVACY.md).

```bash
apkanalyzer manifest permissions app-release.apk
```

### Co aplikacja zapisuje

| Dane | Miejsce | Sposób usunięcia |
|---|---|---|
| Nazwy i URI maksymalnie 20 ostatnich plików | Prywatna pamięć aplikacji, wyłączona z kopii zapasowej | Ustawienia → Wyczyść ostatnie pliki lub wyłączenie opcji „Zapamiętuj ostatnie pliki” |
| Robocze kopie edytowanego pliku | Pamięć podręczna aplikacji | Ustawienia → Usuń tymczasowe pliki robocze; są też czyszczone przy każdym uruchomieniu |
| Trzy ustawienia wyświetlania | Prywatna pamięć aplikacji | Odinstalowanie aplikacji |

To pełna lista. Nie ma identyfikatorów użytkownika, pierwszego uruchomienia ani reklamowych.

## Funkcje

**Czytanie**

- Ciągłe przewijanie pionowe, renderowanie przez systemowy `PdfRenderer` Androida.
- Powiększanie gestem szczypania i ponowne renderowanie stron z zachowaniem ostrości na każdym poziomie powiększenia.
- Przechodzenie do strony, wyszukiwanie tekstu i tryb nocny.
- Otwieranie plików PDF zabezpieczonych hasłem.
- Obsługa otwierania plików PDF z innych aplikacji.

**Wyszukiwanie plików**

- Wbudowana przeglądarka wszystkich plików PDF na urządzeniu, od najnowszych, z wyszukiwaniem według nazwy.
- Przeglądanie według folderów.
- Systemowe okno wyboru, jeśli nie chcesz przyznawać dostępu do plików.

**Edycja** — wiele zmian podczas jednej sesji, z podglądem na żywo

- Obracanie, przycinanie za pomocą przeciąganych uchwytów, usuwanie i przywracanie.
- Zmiana rozmiaru: skalowanie procentowe lub dopasowanie do A3, A4, A5, Letter, Legal i Tabloid.
- Zmiana kolejności pojedynczych stron lub odwracanie kolejności całego dokumentu.
- Wstawianie innych plików PDF w dowolnym miejscu i dalsza edycja ich stron.
- Dodawanie tekstu, usuwanie treści przez zakrywanie ze spłaszczeniem strony, podświetlanie i rysowanie odręczne.
- Tekstowy znak wodny: ukośnie, na środku, kafelkowo, u góry lub u dołu.
- Numeracja stron z szablonem formatu, pozycją, numerem początkowym i pomijaniem stron początkowych, np. okładki.
- Stosowanie zmian do pojedynczej strony, zaznaczenia lub całego dokumentu.

**Import i drukowanie**

- Odbieranie pliku PDF oraz łącza HTTPS udostępnionego z przeglądarki.
- Drukowanie otwartego lub edytowanego PDF przez system Android.
- Centrowanie i pomniejszanie do obszaru wydruku bez automatycznego powiększania małych stron.

**Operacje na całych plikach**

- Łączenie kilku plików PDF w jeden.
- Dzielenie według zakresów lub co N stron.
- Wyodrębnianie wybranych stron do nowego dokumentu.
- Kompresja w dwóch trybach: ponowne kodowanie samych obrazów z zachowaniem możliwości zaznaczania tekstu albo spłaszczenie stron do obrazów w celu uzyskania najmniejszego pliku. Interfejs wyjaśnia konsekwencje wyboru.
- PDF → JPG lub PNG w wybranej rozdzielczości.
- Obrazy → PDF z uwzględnieniem obrotu EXIF i formatem A4, Letter lub dopasowaniem do obrazu.
- Wyodrębnianie tekstu do pliku `.txt`.
- Ochrona hasłem z szyfrowaniem AES-256 lub usuwanie znanego hasła.
- Podpisywanie: narysowanie podpisu i umieszczenie go na stronie.

## Funkcje celowo pominięte

Projekt jasno określa ograniczenia zamiast udostępniać niepełne rozwiązania:

- **Konwersja Word / Excel / PowerPoint.** Poprawna obsługa wymaga pełnego silnika dokumentów biurowych. Niedokładny konwerter byłby gorszy niż jego brak.
- **OCR.** Odczyt tekstu ze skanów wymaga modelu, a więc dużego pakietu danych do pobrania lub serwera. Aplikacja wykrywa skany i informuje o braku tekstu do wyodrębnienia, zamiast bez wyjaśnienia zwracać pusty wynik.
- **Synchronizacja w chmurze, konta i linki do udostępniania.** Założeniem jest pozostawienie danych na urządzeniu.
- **Kryptograficzne podpisy cyfrowe.** Funkcja „Podpisz PDF” umieszcza na stronie obraz podpisu. Odpowiada podpisowi na papierze, a nie certyfikatowi. Aplikacja informuje o tym na ekranie.

## Wymagania

- Android 12 (API 31) lub nowszy.
- Około 15 MB po instalacji.

## Kompilacja

```bash
git clone https://github.com/SpektrumArt/anonpdf-pl.git
cd anonpdf-pl
./gradlew assembleDebug
```

Potrzebne są JDK 17 lub nowsze oraz Android SDK z platformą 37 i narzędziami build-tools 36. Wskaż Gradle lokalizację SDK w pliku `local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
```

### Testy

```bash
./gradlew testDebugUnitTest          # 55 testów JVM: zakresy stron, przetwarzanie planu edycji, limit renderowania, logika interfejsu
./gradlew connectedDebugAndroidTest  # testy na urządzeniu; wymagają urządzenia lub emulatora
./gradlew :desktop-engine:test       # silnik na komputerze: 108 testów, bez urządzenia mobilnego
./gradlew :desktop:test              # testy całej aplikacji komputerowej
./gradlew :desktop:run               # uruchomienie aplikacji komputerowej
```

Testy silnika wymagające renderowania — obejmujące wszystkie operacje i porównanie podglądu z eksportem — znajdują się w `shared-engine/src/deviceTest`. Są uruchamiane zarówno w zestawie testów Androida, jak i wersji komputerowej. CI uruchamia testy silnika komputerowego na Linuksie i Windowsie.

Najszerszy zakres weryfikacji zapewniają testy na urządzeniu. Tworzą pliki PDF, wykonują każdą operację i sprawdzają wyniki: liczbę i kolejność stron po reorganizacji, obszary przycięcia, geometrię skalowania, szyfrowanie i odszyfrowywanie, stopień kompresji, numerację zgodną z końcową kolejnością, wyrenderowane piksele znaku wodnego oraz dokładną zgodność podglądu z eksportem. Dwa testy powstały, ponieważ ich słabsze wersje przechodziły mimo błędnego kodu: skalowanie jest sprawdzane przez ponowne renderowanie i porównanie całego układu, a nie tylko wymiarów stron, zaś przycinanie jest sprawdzane na obróconej stronie. Testy weryfikują też zasady prywatności, w tym listę dozwolonych uprawnień i wyłączone kopie zapasowe.

[`AppSmokeTest`](app/src/androidTest/java/io/github/fanfeast/anonpdf/ui/AppSmokeTest.kt) steruje rzeczywistym interfejsem. Uruchamia aplikację, odwiedza każdy ekran dostępny z ekranu głównego, w tym wszystkie narzędzia, i wraca. Przekazuje też PDF przez „Otwórz za pomocą” i „Udostępnij”, sprawdzając renderowanie w czytniku i otwieranie w edytorze.

[`ManifestContractTest`](app/src/androidTest/java/io/github/fanfeast/anonpdf/ManifestContractTest.kt) sprawdza punkty wejścia używane przez inne aplikacje: uruchamianie, „Otwórz za pomocą” i „Udostępnij” dla PDF, prywatny komponent udostępniania plików, brak innych eksportowanych komponentów i wyłączone kopie zapasowe. CI buduje też zminimalizowany pakiet wydania przy każdym pull requeście i sprawdza uprawnienia, aby wykryć błędy minimalizacji lub manifestu przed oznaczeniem wydania.

### Pakiety wersji komputerowej

```bash
./gradlew :desktop:createReleaseDistributable            # zminimalizowana aplikacja i ograniczone środowisko Java
desktop/packaging/linux/build-appimage.sh 1.0.0 dist     # -> dist/AnonPDF-1.0.0-linux-x86_64.tar.gz
pwsh desktop/packaging/windows/build-zip.ps1 -Version 1.0.0 -Out dist   # w Windowsie
desktop/build/compose/binaries/main-release/app/AnonPDF/bin/AnonPDF --self-test
```

Oba pakiety są przenośne i nie wymagają instalacji. Pakiet dla Linuksa zawiera AppImage w archiwum `.tar.gz`, ponieważ tar zachowuje uprawnienie do uruchamiania, które przeglądarki usuwają. Wystarczy rozpakować archiwum i dwukrotnie kliknąć plik. AppImage korzysta ze statycznego środowiska uruchomieniowego, więc nie wymaga `libfuse2`, a narzędzia do jego budowania mają przypięte sumy kontrolne. Opcja `--self-test` sprawdza najbardziej podatne na błędy ścieżki silnika w spakowanej aplikacji. CI uruchamia ją przy każdym pull requeście na Linuksie i Windowsie, używając dokładnie tych plików, które zostały zbudowane.

### Flatpak

Katalog `flatpak/` zawiera manifest Flathub. Flathub buduje aplikacje bez dostępu do sieci, dlatego każdy wymagany plik Maven jest wymieniony w `flatpak/maven-sources.json` wraz z sumą SHA-256. Plik `offline.init.gradle.kts` ogranicza wyszukiwanie zależności przez Gradle do tego źródła. Aplikacja również nie otrzymuje dostępu do sieci w piaskownicy: manifest nie przyznaje `--share=network`. Dostęp do plików ogranicza się do folderów Dokumenty, Pobrane, Pulpit i Obrazy. Plik otwierany z menedżera plików lub przeciągnięty do okna może pochodzić z dowolnego miejsca — jest przekazywany przez portal dokumentów.

```bash
# Po każdej zmianie zależności; CI zgłasza błąd, dopóki lista nie odpowiada kompilacji.
G=$(mktemp -d)
GRADLE_USER_HOME=$G ./gradlew --no-daemon -Panonpdf.desktopOnly=true :desktop:createReleaseDistributable
flatpak/generate-sources.py $G > flatpak/maven-sources.json

# Lokalna kompilacja i instalacja — skieruj źródło git w manifeście na swoją kopię repozytorium.
flatpak run org.flatpak.Builder --user --install --force-clean builddir flatpak/io.github.fanfeast.anonpdf.yml
```

Podczas kompilacji `--self-test` działa wewnątrz piaskownicy. Dzięki temu błędny Flatpak nie przejdzie kompilacji, zamiast ujawnić problem dopiero u użytkownika.

### Kompilacje wydania

Podpisywanie jest opcjonalne. Utwórz plik `keystore.properties` w katalogu głównym projektu. Jest wykluczony z kontroli wersji przez `.gitignore`:

```properties
storeFile=/absolute/path/to/upload.jks
storePassword=…
keyAlias=…
keyPassword=…
```

Następnie wykonaj:

```bash
./gradlew bundleRelease   # .aab do Google Play
./gradlew assembleRelease # .apk do bezpośredniej instalacji
```

Bez tego pliku kompilacja wydania nadal działa, ale tworzy niepodpisany pakiet.

## Budowa projektu

Kotlin i Jetpack Compose, bez frameworka wstrzykiwania zależności oraz bez nadmiernie rozbudowanej architektury. Silnik PDF, ekrany edytora i narzędzi są współdzielone z aplikacją na Linuksa i Windowsa. Jest ona budowana i testowana przy każdej zmianie, ale nie została jeszcze wydana.

```
shared-engine/src/     Silnik współdzielony przez Androida i komputery: plik wejściowy → wynik.
├── main/.../pdf/
│   ├── PdfEditor.kt     Operacje edycji → ujednolicony plan → zastosowanie / podgląd
│   ├── PdfOps.kt        Łączenie, dzielenie, wyodrębnianie, przycinanie, ochrona, tekst
│   ├── PdfConvert.kt    Kompresja, eksport stron, obrazy → PDF
│   ├── PdfDraw.kt       Rysowanie pojedynczej strony, wspólne dla edytora i narzędzi
│   └── PageRanges.kt    Odczyt zakresów „1-3, 5, 9-12”
├── test/              Testy JVM uruchamiane na obu platformach
└── deviceTest/        Testy silnika wymagające renderowania: na urządzeniu z Androidem
                       lub jako zwykłe testy na komputerze

app/src/main/java/io/github/fanfeast/anonpdf/
├── pdf/          Część silnika przeznaczona dla Androida
│   ├── Platform.kt      Typy obrazów i rozmiarów używane przez silnik na tej platformie
│   ├── PdfRasterizer.kt Obsługa android.graphics.pdf.PdfRenderer
│   └── DocumentStore.kt Przenoszenie danych między SAF a prywatną pamięcią podręczną
├── ui/common/Platform.kt  Typy plików i okna wyboru dla współdzielonych ekranów
├── storage/      Opcjonalny dostęp do wszystkich plików i skaner PDF
├── data/         Ostatnie pliki i ustawienia (DataStore)
└── ui/           Ekrany tylko dla Androida: główny, czytnik, przeglądarka, ustawienia, informacje

shared-ui/src/main/.../ui/   Ekrany wspólne z wersją komputerową: edytor,
                             narzędzia i podpisywanie, motyw oraz wspólne komponenty

desktop-engine/   Silnik kompilowany dla Windowsa i Linuksa — opis poniżej
desktop/          Aplikacja komputerowa: natywne okno, bez przeglądarki,
                  z edytorem i wszystkimi narzędziami w panelu bocznym;
                  pliki można przeciągać z menedżera plików
```

Decyzje projektowe wymagające wyjaśnienia:

**Renderowanie korzysta z platformy.** `android.graphics.pdf.PdfRenderer` jest dostępny na każdym obsługiwanym urządzeniu, działa w piaskownicy systemowej i nie zwiększa rozmiaru APK. PdfBox-Android obsługuje *strukturę* dokumentu, a nie jego rasteryzację.

**Edycja jest listą operacji, a nie bezpośrednich modyfikacji.** Edytor przechowuje uporządkowaną listę operacji i wyprowadza z niej ujednolicony `EditPlan`. Cofnięcie zmiany usuwa ostatnią operację i przelicza plan. Podgląd i eksport korzystają z tego samego planu, dzięki czemu pozostają zgodne.

**Podgląd tworzy jednostronicowy dokument.** Zamiast stosować plan do całego pliku, mechanizm podglądu wyodrębnia widoczną stronę, stosuje odpowiednie operacje dotyczące strony i całego dokumentu, a następnie ją renderuje. Podgląd pliku 400-stronicowego jest równie szybki jak pliku czterostronicowego.

**Jeden kod silnika, dwie wersje PDFBox.** Android korzysta z PdfBox-Android, czyli portu PDFBox z tym samym API, lecz inną nazwą pakietu (`com.tom_roush.pdfbox`). Silnik jest napisany raz dla tego API. Moduł `desktop-engine` kompiluje kopię generowaną podczas budowania z przywróconą nazwą oryginalnego pakietu (`org.apache.pdfbox`). Różnice platformowe — typ obrazu i sposób rasteryzacji strony, przez systemowy renderer Androida lub PDFBox na komputerze — mieszczą się w niewielkim pliku dla każdej platformy. Te same testy edytora sprawdzają obie wersje.

Ekrany edytora są współdzielone podobnie, ale bez zmiany nazw pakietów: Compose Multiplatform korzysta z pakietów `androidx.compose` Jetpack Compose. Każda platforma ma niewielki plik `Platform.kt`, który określa referencję dokumentu — URI zawartości na Androidzie lub plik na komputerze — sposób wyboru dokumentu i poprawnego odczytu orientacji zdjęcia. Każda ma też własny panel zapisywania wyników. Na szerokim ekranie edytor wyświetla narzędzia obok strony zamiast w wysuwanym panelu pod nią. Pliki przeciągnięte do okna komputerowego trafiają do wspólnych ekranów przez `DropInbox`. Na Androidzie ten mechanizm jest nieaktywny, a każdy ekran sam interpretuje otrzymany plik.

**Operacje na stronach zachowują istniejący dokument.** Przed zmianą kolejności lub usunięciem stron silnik utrwala ich dziedziczone atrybuty, odłącza wszystkie strony od drzewa, a następnie dołącza je w nowej kolejności. Pozornie prostsze kopiowanie stron do nowego dokumentu powodowałoby utratę adnotacji i zasobów dziedziczonych z drzewa stron.

Biblioteka BouncyCastle, dostarczana z PdfBox, została wykluczona z kompilacji. PdfBox używa jej tylko do szyfrowania opartego na certyfikatach, którego aplikacja nie udostępnia. Ochrona hasłem korzysta z systemowego JCE. Usunięcie biblioteki zmniejszyło ilość nieużywanego kodu o około 4 MB, głównie algorytmów postkwantowych. Testy szyfrowania potwierdzają działanie pozostałych mechanizmów.

## Licencja

MIT. Zobacz [LICENSE](LICENSE).

Zewnętrzne komponenty i ich licencje są wymienione w aplikacji w sekcji **O aplikacji → Licencje otwartego oprogramowania**.
