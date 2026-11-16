# NusaPersonal Finance — Aplikasi Android (APK & PWA)

Aplikasi Manajemen Keuangan Pribadi modern untuk Android yang dibangun berdasarkan spesifikasi dan data sistem **NUSAPERSONAL FINANCE WORKBOOK**.

Aplikasi ini dirancang dengan antarmuka mobile Android (Material 3 & responsive touch-friendly), mendukung penyimpanan offline (Local Storage / IndexedDB), serta dapat dipasang langsung di HP Android sebagai aplikasi mandiri (PWA / WebAPK) maupun dikompilasi menjadi berkas file `.apk`.

---

## 📱 Modul Sistem Sesuai Dokumen

1. **Dashboard & Ringkasan Eksekutif**
   - **Kekayaan Bersih (Net Worth)**: Dihitung otomatis `(Total Saldo Kas/Bank + Total Aset Fisik + Total Nilai Pasar Investasi) - Total Sisa Utang`.
   - **Arus Kas Bulanan**: Pemasukan (Income), Pengeluaran (Expense), Arus Kas Bersih, dan Saving Rate (Rasio Tabungan %).
   - **Grafik Donut Interaktif**: Visualisasi sebaran pengeluaran per kategori.
   - **Status Akun**: Ringkasan saldo berjalan setiap rekening/dompet.

2. **Master Rekening & Dompet (Accounts)**
   - Mendukung tipe: `Cash` (Tunai), `Bank`, `E-Wallet`, dan `Savings` (Tabungan Khusus).
   - Menghitung saldo awal, mutasi masuk, mutasi keluar, dan saldo saat ini secara akurat.

3. **Database Transaksi & Form Input Cepat**
   - Klasifikasi aktivitas lengkap: `Income`, `Expense`, `Savings`, `Debt Payment`, `Investment`, dan `Transfer`.
   - Filter berdasarkan jenis transaksi, pencarian nama/toko/kategori, dan filter periode bulan.
   - Status transaksi `Posted` (transaksi final yang otomatis mengupdate saldo buku kas).
   - Floating Action Button (+) di kanan bawah untuk pencatatan cepat 1-ketukan.

4. **Kontrol Anggaran Bulanan (Budget)**
   - Plafon anggaran per kategori (Makanan, Transportasi, Rumah Tangga, Tagihan, Kesehatan, Pendidikan, Hiburan, Belanja, Lainnya).
   - Realisasi otomatis terbaca dari transaksi `Expense` yang berstatus `Posted`.
   - Indikator visual progres bar: **Aman** (<80%), **Waspada** (80-99%), dan **Overbudget!** (≥100%).

5. **Target Tabungan & Tujuan (Goals)**
   - Pemantauan dana darurat, dana pendidikan, dan liburan.
   - Progress bar persentase pencapaian, nominal sisa target, tenggat tanggal, dan tombol **+ Tambah Dana** cepat.

6. **Daftar Utang & Cicilan (Debts)**
   - Fasilitas pinjaman KPR, Kartu Kredit, Paylater, dsb.
   - Pelacakan saldo awal utang, cicilan per bulan, sisa kewajiban, dan persentase pelunasan.

7. **Daftar Aset Pribadi & Portofolio Investasi**
   - **Aset Fisik**: Barang elektronik, kendaraan, perabot rumah tangga dengan estimasi depresiasi/apresiasi nilai pasar.
   - **Portofolio Investasi**: Reksa dana, logam mulia emas, saham; otomatis menghitung modal beli vs nilai pasar serta Keuntungan/Kerugian (*Gain/Loss* dalam Rp dan %).

8. **Laporan Keuangan & Edukasi Finansial**
   - Laporan Arus Kas Bulanan (Pemasukan, Pengeluaran, Cicilan Utang, Alokasi Tabungan, Investasi Dibeli, dan Arus Kas Tersedia).
   - **Alur Kerja Finansial**: Panduan checklist harian, mingguan, bulanan, dan tahunan.
   - **Kamus Istilah**: Definisi istilah finansial resmi untuk mengedukasi pengguna.
   - **Ekspor & Impor Data**: Fitur Backup JSON dan Ekspor CSV yang dapat dibuka langsung di Microsoft Excel.

---

## 🚀 Cara Menjalankan & Menggunakan di HP Android

### Cara 1: Menjalankan Server Lokal & Memasang di HP (Paling Cepat)

1. Buka folder proyek ini di komputer Anda:
   ```
   C:\Users\ASROCK\.gemini\antigravity\scratch\nusapersonal-android
   ```
2. Klik ganda berkas **`jalankan.bat`** (atau jalankan perintah `node server.js` di terminal).
3. Terminal akan menampilkan alamat IP lokal, contoh:
   ```
   http://10.10.30.6:3000
   ```
4. Pastikan HP Android Anda terhubung ke jaringan Wi-Fi yang sama dengan komputer.
5. Buka **Google Chrome** di HP Android Anda, lalu ketik alamat tersebut (misal `http://10.10.30.6:3000`).
6. Pasang ke layar utama HP:
   - Ketuk menu titik tiga (**⋮**) di kanan atas Google Chrome.
   - Pilih **"Tambahkan ke Layar Utama"** atau **"Install Aplikasi"**.
   - Ikon **NusaFinance** akan muncul di menu aplikasi HP Anda, berjalan full screen tanpa bar URL browser layaknya aplikasi APK native, dan dapat digunakan secara offline.

---

### Cara 2: Menghasilkan Berkas `.apk` via PWABuilder (No-Code Cloud Build)

1. Deploy folder web ini ke hosting statis gratis (seperti Vercel, Netlify, atau GitHub Pages).
2. Kunjungi layanan resmi Microsoft: [https://www.pwabuilder.com](https://www.pwabuilder.com)
3. Masukkan link website aplikasi Anda, lalu klik **Start**.
4. PWABuilder akan mendeteksi `manifest.json` dan `sw.js` yang sudah disiapkan di proyek ini.
5. Klik **"Package for Android"** -> **"Generate APK"**.
6. Unduh file berkas `.apk` langsung ke HP Android Anda dan pasang (*sideload install*).

---

### Cara 3: Build Berkas `.apk` Otomatis via GitHub Actions

1. Buat repositori baru di GitHub (misal: `nusapersonal-finance-android`).
2. Unggah/Push seluruh berkas dalam folder ini ke GitHub.
3. Repositori ini sudah dilengkapi alur kerja otomatis di [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml).
4. Masuk ke tab **Actions** di GitHub, pilih workflow **Build Android APK**, dan unduh berkas APK yang dihasilkan dari bagian *Artifacts*.

---

## 🔒 Privasi & Keamanan Data
- **100% Offline-First**: Seluruh transaksi dan data pribadi Anda tersimpan secara lokal di memori peramban/HP (`localStorage`).
- Tidak ada data keuangan yang dikirim ke server pihak ketiga manapun.
- Anda dapat mencadangkan data kapan saja melalui menu **Laporan -> Unduh Backup JSON** atau **Ekspor CSV Transaksi**.
