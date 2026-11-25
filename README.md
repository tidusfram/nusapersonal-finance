# NusaFinance — Native Android (Kotlin + Jetpack Compose + SQLite Room)

Aplikasi Manajemen Keuangan Pribadi dan Kekayaan Bersih (**NusaFinance**) yang dibangun 100% secara native menggunakan **Kotlin**, antarmuka modern **Jetpack Compose (Material 3)**, dan penyimpanan basis data lokal **SQLite (Android Room Database)**.

Proyek ini telah distrukturkan sesuai standar resmi **Android Studio** sehingga dapat langsung di-import, dijalankan pada emulator/perangkat fisik, dan dikompilasi menjadi berkas file `.apk`.

---

## 🛠️ Spesifikasi Teknologi & Arsitektur

- **Bahasa Pemrograman**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose (Material 3 Dark Theme)
- **Database Lokal**: SQLite via Android Room Database 2.6.1 + KSP
- **Arsitektur**: MVVM (Model-View-ViewModel) + Kotlin Coroutines & Flow
- **Target SDK**: Android 15 (API 35), Min SDK: Android 7.0 (API 24)

---

## 📱 Modul Sistem Sesuai Workbook Excel

1. **Dasbor & Kekayaan Bersih (Net Worth)**
   - Perhitungan otomatis: `(Total Kas/Bank + Total Aset Fisik + Total Nilai Pasar Investasi) - Total Sisa Utang`.
   - Ringkasan Arus Kas Bulanan: Pemasukan, Pengeluaran, Arus Bersih, dan Saving Rate %.
   - Saldo mutasi berjalan untuk seluruh rekening dan dompet.
   - Tombol cepat **Scan Nota Otomatis**.

2. **Master Rekening & Dompet (Accounts)**
   - Akun: Tunai (Cash), Rekening Bank Utama (Bank), E-Wallet, dan Tabungan (Savings).
   - Melacak saldo awal, mutasi masuk, mutasi keluar, dan saldo berjalan saat ini.

3. **Database Transaksi & Form Input**
   - Mendukung jenis aktivitas: `Income`, `Expense`, `Savings` (alokasi tabungan), `Debt Payment` (cicilan pokok/bunga), `Investment`, dan `Transfer`.
   - Filter pencarian instan dan filter chip berdasarkan jenis aktivitas keuangan.

4. **Kontrol Anggaran Bulanan (Budget)**
   - Plafon per kategori vs realisasi pengeluaran bulanan.
   - Status badge otomatis: **Aman** (<80%), **Waspada** (80-99%), dan **Overbudget!** (≥100%).

5. **Target Tabungan & Tujuan (Goals)**
   - Pelacakan progres target dana darurat, dana pendidikan, dan liburan.
   - Tombol interaktif **+ Tambah Dana** untuk setoran langsung ke target.

6. **Daftar Utang & Cicilan (Debts)**
   - Pelacakan pinjaman (KPR, Kartu Kredit), jadwal jatuh tempo, cicilan bulanan, dan persentase pelunasan.

7. **Aset Pribadi & Portofolio Investasi**
   - **Investasi**: Reksa dana, logam mulia emas, dan saham dengan kalkulasi modal vs valuasi pasar serta *Gain/Loss* (Rp & %).
   - **Aset Fisik**: Elektronik, kendaraan bermotor, dan perabot rumah tangga.

8. **Laporan & Edukasi Finansial**
   - Ringkasan arus kas bulanan & arus kas tersedia (*available cashflow*).
   - Panduan Alur Kerja Finansial (Harian, Mingguan, Bulanan, Tahunan).
   - Tombol pemulihan data simulasi awal kapan saja.

---

## 💻 Cara Membuka & Menjalankan di Android Studio

1. **Buka Android Studio**:
   - Pilih menu **File** -> **Open...**
   - Arahkan ke folder proyek ini:
     ```
     C:\Users\ASROCK\.gemini\antigravity\scratch\nusapersonal-android
     ```
2. **Sinkronisasi Gradle**:
   - Android Studio akan otomatis mengunduh dependensi dan melakukan *Gradle Sync*.
3. **Uji Coba & Jalankan**:
   - Pilih perangkat (Emulator Android atau HP fisik yang terhubung via USB Debugging).
   - Klik tombol **Run ▶️** di bagian atas Android Studio.

---

## 📦 Cara Build File `.apk` di Android Studio

1. Di menu Android Studio, klik **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)**.
2. Tunggu proses kompilasi selesai (biasanya 1–2 menit).
3. Notifikasi akan muncul di pojok kanan bawah dengan tautan **"locate"**.
4. Klik **"locate"** untuk membuka folder tempat file **`app-debug.apk`** berada.
5. Pindahkan file `.apk` tersebut ke HP Android Anda dan pasang secara langsung!

---

## ⚡ Cara Unduh APK Otomatis via GitHub Actions

Repositori ini telah dilengkapi alur kerja kompilasi otomatis di [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml):
1. Buka tab **Actions** di repositori GitHub Anda:  
   👉 **[https://github.com/tidusfram/nusapersonal-finance/actions](https://github.com/tidusfram/nusapersonal-finance/actions)**
2. Pilih alur kerja **Build Native Android APK** terbaru.
3. Unduh berkas APK yang sudah jadi dari bagian **Artifacts** di bawah halaman (*NusaFinance-Kotlin-Compose-APK*).
