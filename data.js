// Data Awal NusaPersonal Finance sesuai workbook Excel
const INITIAL_DATA = {
  accounts: [
    { id: 'ACC-01', name: 'Tunai', type: 'Cash', initialBalance: 1500000, active: true },
    { id: 'ACC-02', name: 'Rekening Bank Utama', type: 'Bank', initialBalance: 12000000, active: true },
    { id: 'ACC-03', name: 'E-Wallet', type: 'E-Wallet', initialBalance: 500000, active: true },
    { id: 'ACC-04', name: 'Tabungan', type: 'Savings', initialBalance: 5000000, active: true }
  ],

  categories: [
    { type: 'Income', name: 'Gaji', active: true },
    { type: 'Income', name: 'Usaha', active: true },
    { type: 'Income', name: 'Bonus', active: true },
    { type: 'Income', name: 'Freelance', active: true },
    { type: 'Income', name: 'Hadiah', active: true },
    { type: 'Income', name: 'Bunga/Dividen', active: true },
    { type: 'Income', name: 'Lainnya', active: true },
    { type: 'Expense', name: 'Makanan', active: true },
    { type: 'Expense', name: 'Transportasi', active: true },
    { type: 'Expense', name: 'Rumah Tangga', active: true },
    { type: 'Expense', name: 'Tagihan', active: true },
    { type: 'Expense', name: 'Kesehatan', active: true },
    { type: 'Expense', name: 'Pendidikan', active: true },
    { type: 'Expense', name: 'Hiburan', active: true },
    { type: 'Expense', name: 'Belanja', active: true },
    { type: 'Expense', name: 'Donasi', active: true },
    { type: 'Expense', name: 'Lainnya', active: true },
    { type: 'Savings', name: 'Dana Darurat', active: true },
    { type: 'Savings', name: 'Tabungan Tujuan', active: true },
    { type: 'Savings', name: 'Tabungan Umum', active: true },
    { type: 'Investment', name: 'Deposito', active: true },
    { type: 'Investment', name: 'Reksa Dana', active: true },
    { type: 'Investment', name: 'Saham', active: true },
    { type: 'Investment', name: 'Obligasi', active: true },
    { type: 'Investment', name: 'Emas', active: true },
    { type: 'Investment', name: 'Properti', active: true },
    { type: 'Investment', name: 'Kripto', active: true },
    { type: 'Investment', name: 'Lainnya', active: true },
    { type: 'Debt', name: 'KPR', active: true },
    { type: 'Debt', name: 'Kendaraan', active: true },
    { type: 'Debt', name: 'Kartu Kredit', active: true },
    { type: 'Debt', name: 'Pinjaman Pribadi', active: true },
    { type: 'Debt', name: 'Paylater', active: true },
    { type: 'Debt', name: 'Lainnya', active: true },
    { type: 'Transfer', name: 'Antar Rekening', active: true },
    { type: 'Transfer', name: 'Dompet Digital', active: true }
  ],

  transactions: [
    {
      id: 'TX-0001',
      date: '2026-11-01',
      type: 'Income',
      desc: 'Contoh gaji bulanan',
      category: 'Gaji',
      accountId: 'ACC-02',
      amount: 8000000,
      party: 'Pemberi kerja',
      transferId: '',
      status: 'Posted',
      notes: 'Gaji pokok bulanan masuk'
    },
    {
      id: 'TX-0002',
      date: '2026-11-02',
      type: 'Expense',
      desc: 'Contoh belanja bulanan',
      category: 'Rumah Tangga',
      accountId: 'ACC-02',
      amount: 1200000,
      party: 'Supermarket',
      transferId: '',
      status: 'Posted',
      notes: 'Kebutuhan pokok dapur'
    },
    {
      id: 'TX-0003',
      date: '2026-11-03',
      type: 'Expense',
      desc: 'Contoh kebutuhan makan',
      category: 'Makanan',
      accountId: 'ACC-01',
      amount: 750000,
      party: 'Warung & Resto',
      transferId: '',
      status: 'Posted',
      notes: 'Makan harian minggu pertama'
    },
    {
      id: 'TX-0004',
      date: '2026-11-05',
      type: 'Savings',
      desc: 'Contoh setoran tabungan',
      category: 'Dana Darurat',
      accountId: 'ACC-04',
      amount: 1000000,
      party: 'Diri sendiri',
      transferId: '',
      status: 'Posted',
      notes: 'Bukan Expense melainkan alokasi tabungan'
    },
    {
      id: 'TX-0005',
      date: '2026-11-06',
      type: 'Debt Payment',
      desc: 'Contoh cicilan KPR',
      category: 'KPR',
      accountId: 'ACC-02',
      amount: 1500000,
      party: 'Bank KPR',
      transferId: '',
      status: 'Posted',
      notes: 'Cicilan rutin bulanan rumah'
    }
  ],

  budgets: [
    { category: 'Makanan', amount: 1500000, period: '2026-11' },
    { category: 'Transportasi', amount: 700000, period: '2026-11' },
    { category: 'Rumah Tangga', amount: 1500000, period: '2026-11' },
    { category: 'Tagihan', amount: 800000, period: '2026-11' },
    { category: 'Kesehatan', amount: 300000, period: '2026-11' },
    { category: 'Pendidikan', amount: 300000, period: '2026-11' },
    { category: 'Hiburan', amount: 300000, period: '2026-11' },
    { category: 'Belanja', amount: 500000, period: '2026-11' },
    { category: 'Lainnya', amount: 300000, period: '2026-11' }
  ],

  goals: [
    {
      id: 'GOAL-01',
      name: 'Dana darurat 6 bulan',
      targetAmount: 30000000,
      currentAmount: 6000000,
      dueDate: '2027-12-31',
      priority: 'Tinggi',
      notes: 'Target keamanan finansial utama'
    },
    {
      id: 'GOAL-02',
      name: 'Dana pendidikan anak',
      targetAmount: 10000000,
      currentAmount: 1500000,
      dueDate: '2027-06-30',
      priority: 'Sedang',
      notes: 'Persiapan tahun ajaran baru'
    },
    {
      id: 'GOAL-03',
      name: 'Liburan akhir tahun',
      targetAmount: 5000000,
      currentAmount: 500000,
      dueDate: '2027-12-01',
      priority: 'Rendah',
      notes: 'Rencana liburan keluarga'
    }
  ],

  debts: [
    {
      id: 'DEBT-01',
      type: 'KPR',
      lender: 'Bank BTN / Mandiri',
      startDate: '2024-01-01',
      dueDate: '2039-01-01',
      monthlyInstallment: 2500000,
      initialAmount: 250000000,
      remainingAmount: 245000000,
      notes: 'Bunga floating 7.5%, tenor 15 tahun'
    },
    {
      id: 'DEBT-02',
      type: 'Kartu Kredit',
      lender: 'Bank BCA',
      startDate: '2026-10-01',
      dueDate: '2026-11-25',
      monthlyInstallment: 500000,
      initialAmount: 5000000,
      remainingAmount: 3500000,
      notes: 'Cicilan 0% 12 bulan'
    }
  ],

  assets: [
    {
      id: 'AST-01',
      name: 'Laptop Kerja High-End',
      category: 'Elektronik',
      purchaseDate: '2025-01-01',
      purchasePrice: 12000000,
      currentValue: 9000000,
      notes: 'Pemakaian produktif sehari-hari'
    },
    {
      id: 'AST-02',
      name: 'Sepeda Motor Matik',
      category: 'Kendaraan',
      purchaseDate: '2023-06-01',
      purchasePrice: 22000000,
      currentValue: 17000000,
      notes: 'Kondisi terawat, surat lengkap'
    },
    {
      id: 'AST-03',
      name: 'Perabot & Elektronik Rumah',
      category: 'Rumah Tangga',
      purchaseDate: '2024-03-01',
      purchasePrice: 8000000,
      currentValue: 6000000,
      notes: 'Kulkas, mesin cuci, kasur'
    }
  ],

  investments: [
    {
      id: 'INV-01',
      instrument: 'Reksa Dana',
      code: 'Sucorinvest Sharia Money Market',
      purchaseDate: '2025-02-01',
      units: 100,
      purchasePricePerUnit: 10000,
      currentValue: 1100000,
      riskNote: 'Rendah - Pasar Uang'
    },
    {
      id: 'INV-02',
      instrument: 'Emas',
      code: 'Emas Logam Mulia Antam',
      purchaseDate: '2025-05-01',
      units: 2,
      purchasePricePerUnit: 1200000,
      currentValue: 2600000,
      riskNote: 'Rendah/Menengah - Lindung Nilai'
    },
    {
      id: 'INV-03',
      instrument: 'Saham',
      code: 'BBCA / BBRI',
      purchaseDate: '2025-08-01',
      units: 10,
      purchasePricePerUnit: 50000,
      currentValue: 560000,
      riskNote: 'Tinggi - Bluechip Fundamental'
    }
  ],

  workflows: [
    {
      frequency: 'Harian',
      step: '1. Catat Transaksi',
      action: 'Segera catat setiap pemasukan/pengeluaran setelah terjadi',
      control: 'Pastikan tanggal, kategori, akun, nominal, dan status Posted'
    },
    {
      frequency: 'Harian',
      step: '2. Klasifikasi Tepat',
      action: 'Bedakan expense, savings, debt payment, investment, transfer',
      control: 'Transfer bukan expense/income; tabungan bukan biaya konsumsi'
    },
    {
      frequency: 'Harian',
      step: '3. Cek Saldo',
      action: 'Cocokkan saldo kas/rekening dengan mutasi aktual bank/e-wallet',
      control: 'Jangan pernah menyimpan PIN, password bank, atau OTP'
    },
    {
      frequency: 'Mingguan',
      step: '4. Review Budget',
      action: 'Periksa kategori pengeluaran yang mendekati batas anggaran bulanan',
      control: 'Lakukan penyesuaian belanja jika sudah >80%'
    },
    {
      frequency: 'Mingguan',
      step: '5. Review Tagihan & Utang',
      action: 'Periksa tanggal jatuh tempo cicilan untuk menghindari denda keterlambatan',
      control: 'Prioritaskan pelunasan cicilan berbunga tinggi'
    },
    {
      frequency: 'Bulanan',
      step: '6. Rekonsiliasi & Tutup Bulan',
      action: 'Bandingkan mutasi semua akun, kunci transaksi bulan tersebut',
      control: 'Ekspor backup data berkala dalam format JSON / Excel'
    },
    {
      frequency: 'Bulanan',
      step: '7. Analisis & Evaluasi',
      action: 'Hitung Saving Rate, Cashflow, dan update nilai estimasi Aset & Investasi',
      control: 'Periksa pertumbuhan Kekayaan Bersih (Net Worth)'
    },
    {
      frequency: 'Tahunan',
      step: '8. Evaluasi Tujuan & Rencana Baru',
      action: 'Tinjau pencapaian target tabungan, pelunasan utang, dan alokasi portofolio',
      control: 'Sesuaikan target tahun depan dengan proyeksi pemasukan'
    }
  ],

  glossary: [
    { term: 'Income', def: 'Pemasukan uang dari gaji, usaha, bonus, dividen, dan sumber lain. Jangan masukkan transfer antar rekening sendiri.' },
    { term: 'Expense', def: 'Pengeluaran kebutuhan atau konsumsi sehari-hari. Berbeda dengan pembelian aset berharga atau tabungan.' },
    { term: 'Savings', def: 'Alokasi pemindahan dana ke rekening tabungan/dana darurat. Bukan beban konsumsi.' },
    { term: 'Debt Payment', def: 'Pembayaran kewajiban cicilan utang. Arus kas keluar yang mengurangi saldo sisa utang pokok.' },
    { term: 'Investment', def: 'Pembelian instrumen investasi (saham, reksa dana, emas, properti). Nilai pasar bisa naik/turun.' },
    { term: 'Transfer', def: 'Pemindahan dana antar rekening sendiri (misal Bank ke E-Wallet). Tidak menambah/mengurangi total kekayaan.' },
    { term: 'Budget', def: 'Batas plafon rencana belanja per kategori bulanan untuk menjaga pengeluaran tetap terkendali.' },
    { term: 'Net Worth', def: 'Kekayaan bersih: Total Saldo Kas/Bank + Nilai Aset + Nilai Investasi dikurangi Total Sisa Utang.' },
    { term: 'Saving Rate', def: 'Persentase pendapatan yang berhasil ditabung atau diinvestasikan (Target ideal: >20%).' },
    { term: 'Posted', def: 'Status transaksi yang telah final dan sah dihitung dalam saldo dan laporan bulanan.' }
  ]
};
