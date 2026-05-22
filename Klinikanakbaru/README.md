# CeriaCare Android Studio Project (Compose Wrapper)

Proyek ini adalah proyek **Android Studio** berbasis **Kotlin & Jetpack Compose** yang membungkus web app React (CeriaCare) menggunakan **WebView** native. Metode ini memastikan aplikasi web Anda dapat dideploy secara native ke HP Android dengan 100% tampilan, animasi, dan logika yang sama persis tanpa perlu menulis ulang seluruh kode UI.

---

## 🚀 Cara Membuka di Android Studio

1. Buka **Android Studio** di komputer Anda.
2. Pilih menu **Open** atau **Import Project**.
3. Arahkan ke folder **`Klinikanakbaru`** ini, lalu klik **OK**.
4. Android Studio akan mendeteksi Gradle (Kotlin DSL) secara otomatis dan mulai mengunduh dependensi (proses sinkronisasi Gradle pertama kali membutuhkan koneksi internet).
5. Pasang Emulator Android atau sambungkan HP Android asli via USB debugging.
6. Klik tombol **Run** (ikon segitiga hijau) di toolbar atas untuk menjalankan aplikasi.

---

## 📁 Struktur Penting Proyek Android (Compose)

* **`app/src/main/java/com/example/klinikanakbaru/MainActivity.kt`**: Kelas utama Android yang memprakarsai WebView dalam Compose `AndroidView`, mengaktifkan fitur DOM Storage (localStorage) dan JavaScript, serta memuat file index web. Juga menangani navigasi tombol kembali fisik (back button) secara deklaratif menggunakan Compose `BackHandler`.
* **`app/src/main/assets/`**: Folder tempat file compile React (`dist/`) diletakkan agar bisa berjalan secara offline dan cepat di HP.
* **`app/src/main/AndroidManifest.xml`**: Berkas manifes yang meminta izin akses Internet (`android.permission.INTERNET`) serta mengaktifkan `android:usesCleartextTraffic="true"` agar emulator bisa melakukan request HTTP (non-HTTPS) ke server API lokal Anda selama development.

---

## 🌐 Menghubungkan Emulator ke Server API PHP Lokal

Jika Anda menjalankan server PHP lokal (seperti XAMPP atau Laragon) di komputer Anda, perhatikan aturan penting ini:
* **JANGAN** menggunakan alamat `http://localhost` di konfigurasi API Android. Di dalam emulator Android, kata kunci `localhost` merujuk ke emulator itu sendiri, bukan komputer host Anda.
* **SOLUSI**: Gunakan IP khusus **`http://10.0.2.2/`** untuk mengakses localhost komputer host dari dalam Emulator bawaan Android Studio.
* Anda bisa mengatur URL API ini di dalam file `.env` di root proyek sebelum melakukan build ulang React.

---

## 🔄 Cara Memperbarui Tampilan Web di Android Studio

Setiap kali Anda mengubah kode React/Tailwind di folder root proyek utama dan ingin melihat hasilnya di Android Studio:

1. Di terminal folder root proyek utama, jalankan perintah compile ulang:
   ```bash
   npm run build
   ```
2. Hapus file lama di folder assets Android Studio, lalu salin file baru hasil build dari folder `dist/` ke folder `Klinikanakbaru/app/src/main/assets/`.
   *(Jika menggunakan PowerShell di Windows, Anda bisa menjalankan perintah ini di terminal folder root):*
   ```powershell
   # Bersihkan assets lama
   Remove-Item -Path "Klinikanakbaru/app/src/main/assets/*" -Recurse -Force
   
   # Salin index.html dan folder assets dari build dist
   Copy-Item -Path "dist/index.html" -Destination "Klinikanakbaru/app/src/main/assets/" -Force
   Copy-Item -Path "dist/assets" -Destination "Klinikanakbaru/app/src/main/assets/" -Recurse -Force
   ```
3. Klik **Run** atau **Apply Changes** di Android Studio untuk memasang versi terbaru ke HP/Emulator.
