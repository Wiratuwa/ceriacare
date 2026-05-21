# Panduan Integrasi Backend & Database (PHP & MySQL)

Dokumen ini berisi panduan lengkap, langkah demi langkah, dan peringatan keamanan penting untuk menyambungkan frontend React (Vite) aplikasi **CeriaCare** dengan backend **PHP** dan database **MySQL**.

---

## ⚠️ PERINGATAN PENTING (Security & Technical Warnings)

Sebelum mulai menulis baris kode backend pertama Anda, harap pahami beberapa isu krusial berikut yang sering ditemui saat menghubungkan React dengan PHP:

### 1. Masalah CORS (Cross-Origin Resource Sharing)
* **Mengapa terjadi?** Frontend React Anda berjalan di port development (misalnya `http://localhost:5173` atau `http://localhost:3000`), sedangkan backend PHP berjalan di server lokal lain (misal XAMPP/Laragon di `http://localhost`). Secara default, browser memblokir request lintas port ini demi keamanan.
* **Solusi di PHP:** Anda harus menambahkan header CORS di setiap file entry PHP (atau di file router/middleware) sebelum mengembalikan respon apa pun.
  ```php
  header("Access-Control-Allow-Origin: *"); // Ubah '*' ke domain React jika production
  header("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With");
  header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
  
  // Tangani preflight request OPTIONS dari browser
  if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
      exit(0);
  }
  ```

### 2. Cara Membaca Data POST JSON di PHP
* **Masalah:** Fungsi `fetch` di React mengirim data sebagai raw JSON (`application/json`). Variabel bawaan PHP seperti `$_POST` **akan kosong** karena mereka hanya bisa membaca format `application/x-www-form-urlencoded` atau `multipart/form-data`.
* **Solusi di PHP:** Anda harus membaca stream input mentah (`php://input`) dan mendekodekannya menjadi array asosiatif:
  ```php
  $input = json_decode(file_get_contents('php://input'), true);
  // Sekarang data dikirim bisa dibaca dari $input['parentId'], $input['name'], dll.
  ```

### 3. Ancaman SQL Injection
* **Masalah:** Menggunakan input pengguna secara langsung di query SQL seperti `SELECT * FROM parents WHERE email = '$email'` membuat database Anda rentan diretas atau dihapus secara ilegal.
* **Solusi di PHP:** Gunakan **Prepared Statements** menggunakan **PDO (PHP Data Objects)** atau **Prepared Statement MySQLi**. Jangan gunakan concatenating strings.
  ```php
  $stmt = $pdo->prepare("SELECT * FROM parents WHERE email = :email");
  $stmt->execute(['email' => $email]);
  $user = $stmt->fetch();
  ```

### 4. Keamanan Password
* Jangan pernah menyimpan password dalam bentuk teks biasa (plain text).
* Gunakan fungsi hashing bawaan PHP: `password_hash($password, PASSWORD_BCRYPT)` untuk menyimpan password.
* Gunakan `password_verify($password, $hashedPassword)` untuk memverifikasi password saat proses login.

### 5. Sinkronisasi State (Sync vs Async UI)
* React render data secara instan dari RAM lokal. Namun, server database PHP membutuhkan waktu pemrosesan (network latency).
* **Solusi:** Saat menghubungkan, pastikan Anda menambahkan animasi/status "loading" di frontend pada tombol/halaman (misal state `isLoading`) untuk mencegah pengguna mengklik berulang kali (double submit) saat request database sedang berjalan.

---

## 🗄️ STRUKTUR DATABASE (MySQL Schema DDL)

Buat database baru bernama `ceriacare_db` di phpMyAdmin atau DBMS pilihan Anda, lalu jalankan query SQL berikut untuk membuat tabel-tabel yang diperlukan:

```sql
CREATE DATABASE IF NOT EXISTS ceriacare_db;
USE ceriacare_db;

-- 1. TABEL PARENTS (Orang Tua / Wali)
CREATE TABLE parents (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    member_type VARCHAR(50) DEFAULT 'Basic Member'
);

-- 2. TABEL CHILDREN (Anak-anak / Pasien)
CREATE TABLE children (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parent_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    age VARCHAR(50) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- 3. TABEL DOCTORS (Dokter)
CREATE TABLE doctors (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL
);

-- 4. TABEL SERVICES (Layanan Klinik)
CREATE TABLE services (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL
);

-- 5. TABEL BOOKINGS (Reservasi Kunjungan)
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parent_id VARCHAR(50) NOT NULL,
    parent_name VARCHAR(100) NOT NULL,
    child_name VARCHAR(100) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    time VARCHAR(10) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- 6. TABEL QUEUE (Antrian Klinik Live)
CREATE TABLE queue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    no VARCHAR(10) NOT NULL,
    child_name VARCHAR(100) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    time VARCHAR(10) NOT NULL,
    status ENUM('Waiting', 'Examining', 'Completed') DEFAULT 'Waiting',
    patient_id INT NOT NULL,
    parent_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- 7. TABEL MEDICAL RECORDS (Rekam Medis Pasien)
CREATE TABLE medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    child_name VARCHAR(100) NOT NULL,
    parent_id VARCHAR(50) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    date VARCHAR(50) NOT NULL,
    notes TEXT NOT NULL,
    height VARCHAR(20),
    weight VARCHAR(20),
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- 8. TABEL PRESCRIPTIONS (Resep Obat Dokter)
CREATE TABLE prescriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    FOREIGN KEY (record_id) REFERENCES medical_records(id) ON DELETE CASCADE
);

-- 9. TABEL INVOICES (Keuangan & Invoice)
CREATE TABLE invoices (
    id VARCHAR(50) PRIMARY KEY,
    parent_id VARCHAR(50) NOT NULL,
    child_name VARCHAR(100) NOT NULL,
    item VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    status ENUM('PENDING', 'PENDING_CONFIRMATION', 'PAID') DEFAULT 'PENDING',
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- --- SEED DATA AWAL (Optional) ---
INSERT INTO parents (id, name, email, password, member_type) VALUES
('parent-1', 'Bunda Sarah', 'sarah@ceria.com', '$2y$10$tExlXn6.3Q1pP5FjN96.beB.YVf48vYw2Z9.1LgEawXW400V7dF2u', 'Premium Member'),
('parent-2', 'Bpk. Ridwan', 'ridwan@ceria.com', '$2y$10$tExlXn6.3Q1pP5FjN96.beB.YVf48vYw2Z9.1LgEawXW400V7dF2u', 'Basic Member');

INSERT INTO children (parent_id, name, age, gender) VALUES
('parent-1', 'Arka Pratama', '5 tahun', 'Laki-laki'),
('parent-1', 'Ziva Putri', '2 tahun', 'Perempuan'),
('parent-2', 'Siska Amelia', '3 tahun', 'Perempuan');

INSERT INTO doctors (id, name, specialty) VALUES
('doc-1', 'dr. Sarah Wijaya, Sp.A', 'Poli Tumbuh Kembang'),
('doc-2', 'dr. Budi Santoso, Sp.A', 'Poli Anak Umum'),
('doc-3', 'dr. Rian Pratama, Sp.A', 'Spesialis Gizi Anak');

INSERT INTO services (id, name, price) VALUES
('srv-1', 'Konsultasi Umum', 150000),
('srv-2', 'Tumbuh Kembang', 250000),
('srv-3', 'Vaksinasi DPT', 450000);
```

---

## 📂 STRUKTUR DIREKTORI BACKEND (Rekomendasi)

Untuk memudahkan koneksi dengan api di `src/services/api.ts`, buat struktur folder API PHP Anda di direktori server lokal (misal `C:\xampp\htdocs\ceriacare-api\`) seperti berikut:

```text
ceriacare-api/
├── config/
│   └── database.php       # Berisi koneksi PDO ke MySQL
├── auth/
│   ├── login.php          # Route Login
│   └── register.php       # Route Registrasi Parent
├── parents/
│   └── list.php
├── children/
│   ├── list.php           # Ambil daftar anak ortu tertentu (?parentId=X)
│   └── create.php         # Tambah anak baru (POST)
├── doctors/
│   ├── list.php
│   ├── create.php
│   └── delete.php
├── bookings/
│   ├── list.php           # Filter by parentId (Parent) or list all (Admin)
│   ├── create.php         # Request Booking baru
│   └── update_status.php  # Konfirmasi / Tolak Booking oleh Admin
├── queue/
│   ├── list.php           # Ambil data antrian hari ini
│   └── update_status.php  # Update status antrian (Waiting, Examining, Completed)
├── medical_records/
│   ├── list.php           # Ambil data rekam medis
│   └── create.php         # Simpan hasil periksa + resep obat oleh dokter
└── invoices/
    ├── list.php
    ├── pay.php            # Ubah status invoice ke PENDING_CONFIRMATION
    └── confirm.php        # Konfirmasi pembayaran oleh Admin (Ubah status ke PAID)
```

---

## 💻 CONTOH KODE BACKEND PHP

Berikut adalah contoh implementasi file PHP utama Anda agar dapat menerima request dari frontend React secara aman.

### 1. Konfigurasi Database (`config/database.php`)
```php
<?php
// Izinkan akses CORS
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

$host = 'localhost';
$db   = 'ceriacare_db';
$user = 'root'; // Sesuaikan dengan user MySQL lokal Anda
$pass = '';     // Sesuaikan dengan password MySQL lokal Anda
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
     $pdo = new PDO($dsn, $user, $pass, $options);
} catch (\PDOException $e) {
     http_response_code(500);
     echo json_encode(["message" => "Database connection failed: " . $e->getMessage()]);
     exit();
}
?>
```

### 2. Autentikasi Login (`auth/login.php`)
```php
<?php
require_once '../config/database.php';

// Hanya izinkan request POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["message" => "Method Not Allowed"]);
    exit();
}

// Ambil input JSON mentah
$input = json_decode(file_get_contents('php://input'), true);

$role = $input['role'] ?? '';
$username = $input['username'] ?? ''; // Dapat berupa email untuk parent
$password = $input['password'] ?? ''; 

if (empty($role) || empty($username)) {
    http_response_code(400);
    echo json_encode(["message" => "Bad Request: Data tidak lengkap"]);
    exit();
}

try {
    if ($role === 'PARENT') {
        // Cari parent berdasarkan email
        $stmt = $pdo->prepare("SELECT * FROM parents WHERE email = :email");
        $stmt->execute(['email' => $username]);
        $user = $stmt->fetch();
        
        if ($user) {
            // Catatan: Jika password_verify belum diaktifkan di database, 
            // pastikan password dicocokkan dengan password_hash()
            // Contoh implementasi production: 
            // if (password_verify($password, $user['password'])) { ... }
            
            echo json_encode([
                "token" => "dummy-jwt-token-for-parent-" . $user['id'],
                "user" => [
                    "id" => $user['id'],
                    "name" => $user['name'],
                    "email" => $user['email'],
                    "memberType" => $user['member_type']
                ]
            ]);
            exit();
        }
    } else if ($role === 'DOCTOR') {
        // Cari dokter berdasarkan ID (Mock login)
        $stmt = $pdo->prepare("SELECT * FROM doctors WHERE id = :id");
        $stmt->execute(['id' => $username]);
        $user = $stmt->fetch();
        if ($user) {
            echo json_encode([
                "token" => "dummy-jwt-token-for-doctor-" . $user['id'],
                "user" => [
                    "id" => $user['id'],
                    "name" => $user['name'],
                    "specialty" => $user['specialty']
                ]
            ]);
            exit();
        }
    } else if ($role === 'ADMIN') {
        // Cek login admin (Mock admin ID login)
        if ($username === 'admin-1' || $username === 'admin-2') {
            echo json_encode([
                "token" => "dummy-jwt-token-for-admin-" . $username,
                "user" => [
                    "id" => $username,
                    "name" => ($username === 'admin-1') ? "Admin CeriaCare" : "Admin Wiratuwa"
                ]
            ]);
            exit();
        }
    }
    
    // User tidak ditemukan atau password salah
    http_response_code(401);
    echo json_encode(["message" => "Username/Email atau Password salah."]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["message" => "Internal Server Error: " . $e->getMessage()]);
}
?>
```

### 3. Pembuatan Booking Baru (`bookings/create.php`)
```php
<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["message" => "Method Not Allowed"]);
    exit();
}

$input = json_decode(file_get_contents('php://input'), true);

$parentId = $input['parentId'] ?? '';
$parentName = $input['parentName'] ?? '';
$childName = $input['childName'] ?? '';
$serviceName = $input['serviceName'] ?? '';
$doctorName = $input['doctorName'] ?? '';
$date = $input['date'] ?? '';
$time = $input['time'] ?? '';

if (empty($parentId) || empty($childName) || empty($serviceName) || empty($doctorName) || empty($date) || empty($time)) {
    http_response_code(400);
    echo json_encode(["message" => "Data pendaftaran tidak lengkap"]);
    exit();
}

try {
    $sql = "INSERT INTO bookings (parent_id, parent_name, child_name, service_name, doctor_name, date, time, status) 
            VALUES (:parent_id, :parent_name, :child_name, :service_name, :doctor_name, :date, :time, 'PENDING')";
            
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'parent_id' => $parentId,
        'parent_name' => $parentName,
        'child_name' => $childName,
        'service_name' => $serviceName,
        'doctor_name' => $doctorName,
        'date' => $date,
        'time' => $time
    ]);
    
    $bookingId = $pdo->lastInsertId();
    
    echo json_encode([
        "id" => (string)$bookingId,
        "parentId" => $parentId,
        "parentName" => $parentName,
        "childName" => $childName,
        "serviceName" => $serviceName,
        "doctorName" => $doctorName,
        "date" => $date,
        "time" => $time,
        "status" => "PENDING"
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["message" => "Gagal membuat booking: " . $e->getMessage()]);
}
?>
```

---

## 🛠️ PANDUAN REFACTORING FRONTEND (React)

Untuk menyambungkan frontend React saat ini di [src/App.tsx](file:///c:/Stuff/Code/Klinik%20Anak%20(Cursor)/src/App.tsx) ke API client [src/services/api.ts](file:///c:/Stuff/Code/Klinik%20Anak%20(Cursor)/src/services/api.ts), ikuti langkah-langkah berikut:

### Langkah 1: Hubungkan Environment Variables ke Vite
1. Pastikan Anda menyalin file `.env.example` menjadi `.env` di folder root project.
2. Edit file `.env` dan ganti alamat `VITE_API_BASE_URL` sesuai lokasi file API PHP Anda di server lokal (misalnya `VITE_API_BASE_URL="http://localhost/ceriacare-api"`).

### Langkah 2: Impor API Client ke App.tsx
Tambahkan baris berikut di bagian atas file [App.tsx](file:///c:/Stuff/Code/Klinik%20Anak%20(Cursor)/src/App.tsx):
```typescript
import { api } from './services/api';
```

### Langkah 3: Mengganti Hooks State Lokal dengan Panggilan API
Ubah alur pemrosesan data synchronous di `App.tsx` menjadi async. Sebagai contoh, untuk **Registrasi Akun Baru**:

**Kode Asli (App.tsx):**
```typescript
const handleRegisterParent = (name: string, email: string) => {
  const newId = `parent-${parents.length + 1}`;
  const newParent: Parent = {
    id: newId,
    name,
    email,
    memberType: 'Basic Member'
  };
  setParents([...parents, newParent]);
  setChildrenDb({
    ...childrenDb,
    [newId]: []
  });
  setCurrentParentId(newId);
  setRole('PARENT');
  setActiveTab('home');
  setIsRegistering(false);
};
```

**Ubah Menjadi Panggilan Async (Gaya API):**
```typescript
const handleRegisterParent = async (name: string, email: string) => {
  try {
    setIsLoading(true); // Tambahkan state loading jika diperlukan
    // Panggil service api.auth.register yang terhubung ke PHP
    const newParent = await api.auth.register(name, email);
    
    // Update local state dengan respons database asli
    setParents(prev => [...prev, newParent]);
    setChildrenDb(prev => ({
      ...prev,
      [newParent.id]: []
    }));
    setCurrentParentId(newParent.id);
    setRole('PARENT');
    setActiveTab('home');
    setIsRegistering(false);
  } catch (error: any) {
    alert("Registrasi gagal: " . error.message);
  } finally {
    setIsLoading(false);
  }
};
```

### Langkah 4: Load Data Awal dengan `useEffect`
Gunakan `useEffect` untuk memuat data dari database MySQL ketika aplikasi pertama kali dirender atau ketika role aktif berubah:
```typescript
useEffect(() => {
  const loadInitialData = async () => {
    try {
      // 1. Ambil Dokter Aktif
      const doctorsList = await api.doctors.getAll();
      setDoctors(doctorsList);
      
      // 2. Ambil Layanan Aktif
      const servicesList = await api.services.getAll();
      setServices(servicesList);
      
      // 3. Ambil Antrian Live
      const liveQueue = await api.queue.getLiveQueue();
      setQueue(liveQueue);
    } catch (e) {
      console.error("Gagal meload master data dari database.");
    }
  };
  
  loadInitialData();
}, []);
```

---

Dengan mengikuti aturan ini, aplikasi React CeriaCare Anda akan terhubung dengan database MySQL secara real-time melalui REST API PHP dengan aman dan terstruktur!
