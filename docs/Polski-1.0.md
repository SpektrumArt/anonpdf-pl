# AnonPDF po polsku — 1.0

Polska wersja interfejsu Android w repozytorium SpektrumArt/anonpdf-pl.

## Zakres

- Ekran główny, przeglądanie plików, czytnik, ustawienia i informacje o aplikacji.
- Edytor stron, skalowanie procentowe, przycinanie, numeracja i znaki wodne.
- Łączenie, dzielenie, konwersja, kompresja, podpis i ochrona hasłem.
- Opisy dostępności, wyniki operacji i komunikaty walidacji.
- Istniejące testy interfejsu i oczekiwania dotyczące przetłumaczonych komunikatów.

To polski wariant aplikacji: język interfejsu jest ustawiony w kodzie, bez przełącznika języka. Wspólne ekrany i komunikaty silnika są również używane przez wersję desktopową; jej oddzielne ekrany nie są objęte pełnym tłumaczeniem.

Nazwy klas, klucze ustawień, formaty plików, znaczniki {n} i {total}, techniczne znaczniki ekstrakcji tekstu i oryginalne informacje licencyjne pozostają zachowane. Tłumaczenie nie dodaje obsługi polskich znaków do czcionek używanych przy wpisywaniu treści PDF.

## Weryfikacja

Istniejący workflow Build uruchamia testy jednostkowe, lint, kompilację APK i testy na emulatorach po utworzeniu pull requestu (jeżeli Actions jest włączone dla forka). Wynik należy sprawdzić w zakładce Checks. Ręcznie należy sprawdzić długość etykiet na telefonie, skalowanie 80%, zapis PDF i ponowne otwarcie wyniku.

Kod źródłowy oryginału: https://github.com/FanFeast/anonpdf
