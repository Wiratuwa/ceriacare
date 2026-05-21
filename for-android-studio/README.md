# CeriaCare Android Studio Project

Proyek ini adalah proyek **Android Studio** (Java & Gradle) yang membungkus web app React (CeriaCare) menggunakan **WebView** native. Metode ini memastikan aplikasi web Anda dapat dideploy secara native ke HP Android dengan 100% tampilan, animasi, dan logika yang sama persis tanpa perlu menulis ulang seluruh kode UI.

---

## 🚀 Cara Membuka di Android Studio

1. Buka **Android Studio** di komputer Anda.
2. Pilih menu **Open** atau **Import Project**.
3. Arahkan ke folder **`for-android-studio`** ini, lalu klik **OK**.
4. Android Studio akan mendeteksi Gradle secara otomatis dan mulai mengunduh dependensi (proses sinkronisasi Gradle pertama kali membutuhkan koneksi internet).
5. Pasang Emulator Android atau sambungkan HP Android asli via USB debugging.
6. Klik tombol **Run** (ikon segitiga hijau) di toolbar atas untuk menjalankan aplikasi.

---

## 📁 Struktur Penting Proyek Android

* **`app/src/main/java/com/wiratuwa/ceriacare/MainActivity.java`**: Kelas utama Java yang memprakarsai WebView, mengaktifkan fitur DOM Storage (localStorage) dan JavaScript, serta memuat file index web.
* **`app/src/main/res/layout/activity_main.xml`**: Layout XML yang berisi satu elemen `<WebView>` layar penuh.
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
2. Hapus file lama di folder assets Android Studio, lalu salin file baru hasil build dari folder `dist/` ke folder `for-android-studio/app/src/main/assets/`.
   *(Jika menggunakan PowerShell di Windows, Anda bisa menjalankan perintah ini di terminal folder root):*
   ```powershell
   Copy-Item -Path "dist/*" -Destination "for-android-studio/app/src/main/assets" -Recurse -Force
   ```
3. Klik **Run** atau **Apply Changes** di Android Studio untuk memasang versi terbaru ke HP/Emulator.
