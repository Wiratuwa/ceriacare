# Panduan Struktur Git & Dependensi Project (CeriaCare)

Dokumen ini menjelaskan struktur folder yang diabaikan oleh Git (seperti `node_modules` dan `dist`) serta langkah-langkah untuk menyiapkan dan menjalankan project ini setelah melakukan clone atau checkout branch dari GitHub.

---

## 1. Mengapa Folder `node_modules` & `dist` Tidak Ada di GitHub?

Di dalam file konfigurasi `.gitignore` di root folder, kami mengabaikan folder-folder tertentu demi menjaga performa repositori dan keselarasan kode antar developer:

### `node_modules/`
* **Mengapa diabaikan?** Folder ini berisi semua library/dependencies JavaScript pihak ketiga yang diinstal oleh Node.js. Ukuran folder ini sangat besar (ratusan megabyte) dan dapat berubah setiap saat tergantung versi sistem operasi developer.
* **Solusinya**: Cukup simpan file konfigurasi `package.json` dan `package-lock.json` di Git. Ketika Anda mengunduh project di komputer baru, jalankan perintah ini di terminal untuk menginstal ulang semua library secara otomatis:
  ```bash
  npm install
  ```

### `dist/`
* **Mengapa diabaikan?** Folder ini berisi kode JavaScript/HTML/CSS yang sudah dikompilasi (production build) dan siap di-deploy. Karena kodenya digenerate secara otomatis dari file sumber di dalam folder `src/`, kita tidak perlu menyimpannya di Git.
* **Solusinya**: Untuk membuat folder `dist` baru dari source code saat ini, jalankan perintah berikut di terminal:
  ```bash
  npm run build
  ```

---

## 2. Struktur Khusus untuk Android Studio

Bagi pengembangan aplikasi mobile Android pada folder `for-android-studio/`:
* Kami sudah mem-build source code React Anda secara lokal dan menyalin hasilnya ke dalam folder **`for-android-studio/app/src/main/assets/`**.
* Folder assets ini **sudah di-push ke GitHub** secara utuh.
* **Keuntungannya**: Anda dapat langsung mengimpor folder `for-android-studio` ke Android Studio dan menjalankannya di emulator/perangkat fisik secara langsung tanpa perlu melakukan build frontend React terlebih dahulu.

Jika Anda mengubah kode React di folder `src/` dan ingin memperbaruinya di aplikasi Android Studio, ikuti langkah berikut:
1. Jalankan perintah kompilasi React:
   ```bash
   npm run build
   ```
2. Salin isi folder `dist/` ke dalam folder `for-android-studio/app/src/main/assets/` (hapus isi aset lama terlebih dahulu).

---

## 3. Langkah Awal Memulai Project (Local Setup)

Jika Anda baru saja melakukan clone repositori atau berpindah branch:

1. **Buka terminal** di direktori utama project (`Klinik Anak (Cursor)`).
2. **Instal dependensi frontend**:
   ```bash
   npm install
   ```
3. **Jalankan server lokal (development mode)**:
   ```bash
   npm run dev
   ```
4. **Buka Android Studio** dan pilih `File -> Open`, kemudian pilih folder `for-android-studio/` jika ingin menguji versi mobile.
