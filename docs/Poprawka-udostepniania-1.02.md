# Poprawka udostępniania — 1.02

Przeglądarki mogą udostępniać jednocześnie łącze w EXTRA_TEXT oraz miniaturę w ClipData lub EXTRA_STREAM. Wersja 1.01 mogła wybrać miniaturę i próbować otworzyć ją jako PDF bez pytania o pobranie.

Wersja 1.02 najpierw odczytuje łącze z tekstowego udostępnienia. Jawny strumień application/pdf pozostaje preferowany, ale jego lokalna kopia jest sprawdzana przed otwarciem. Jeśli strumień nie jest PDF, aplikacja proponuje pobranie towarzyszącego łącza lub podaje polską instrukcję pobrania oryginału w przeglądarce.

Podpisane parametry adresu pozostają bez zmian. Każde pobranie nadal wymaga potwierdzenia.

Weryfikacja: testy obejmują tekstowy link z miniaturą, ClipData bez jawnego strumienia, rzeczywisty strumień PDF z dodatkowym linkiem, tekst w ClipData oraz odrzucanie JPEG i HTML przy akceptacji nagłówka PDF bez rozszerzenia nazwy. Testy nie zastępują próby na telefonie ani analizy konkretnego pliku zgłoszonego przez użytkownika.
