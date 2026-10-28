# Prywatność — AnonPDF PL 1.01

Ta wersja dodaje import plików PDF z internetu i drukowanie. Wcześniejsza gwarancja braku uprawnienia INTERNET nie obowiązuje w tej wersji.

- INTERNET: pobieranie dokumentu z udostępnionego łącza HTTPS dopiero po potwierdzeniu w aplikacji. Żądanie GET i parametry adresu trafiają do serwera źródłowego i serwerów przekierowań. Serwer poznaje adres IP urządzenia. Aplikacja nie przekazuje ciasteczek ani sesji przeglądarki.
- MANAGE_EXTERNAL_STORAGE: opcjonalne przeglądanie plików PDF; można zamiast niego korzystać z systemowego okna wyboru.
- Uprawnienie AndroidX DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION chroni prywatny odbiornik aplikacji.
- Brak analityki, reklam, konta i synchronizacji. Kod importera nie wysyła dokumentów; edycja działa lokalnie. Uprawnienie INTERNET oznacza jednak, że system nie blokuje wszystkich połączeń aplikacji.
- Drukowanie jest jawnym przekazaniem dokumentu do wybranej systemowej usługi drukowania i drukarki.
- Kopie robocze i pobrane PDF pozostają w prywatnej pamięci podręcznej. Obszar roboczy jest czyszczony przy nowym uruchomieniu. Migawka do drukowania jest usuwana po zakończeniu zadania; przerwanie procesu może pozostawić ją w pamięci podręcznej do usunięcia danych aplikacji lub przez system.
- Historia do 20 ostatnich plików jest opcjonalna i może być wyczyszczona w ustawieniach. Kopie zapasowe są wyłączone.

Importer akceptuje wyłącznie HTTPS, ogranicza przekierowania do pięciu i rozmiar do 50 MB oraz weryfikuje nagłówek PDF. Strony wymagające logowania, HTML zamiast PDF lub wygasłe linki wymagają pobrania pliku przez przeglądarkę i udostępnienia samego PDF.

Historyczna polityka wariantu offline: [PRIVACY-1.0.md](PRIVACY-1.0.md). Nie opisuje wersji 1.01.
