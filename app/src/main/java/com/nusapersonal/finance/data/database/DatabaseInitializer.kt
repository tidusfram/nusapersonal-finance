package com.nusapersonal.finance.data.database

import com.nusapersonal.finance.data.entity.*

object DatabaseInitializer {

    val defaultAccounts = listOf(
        AccountEntity("ACC-01", "Tunai", "Cash", 1_500_000L),
        AccountEntity("ACC-02", "Rekening Bank Utama", "Bank", 12_000_000L),
        AccountEntity("ACC-03", "E-Wallet", "E-Wallet", 500_000L),
        AccountEntity("ACC-04", "Tabungan", "Savings", 5_000_000L)
    )

    val defaultTransactions = listOf(
        TransactionEntity(
            id = "TX-0001",
            date = "2026-11-01",
            type = "Income",
            description = "Contoh gaji bulanan",
            category = "Gaji",
            accountId = "ACC-02",
            amount = 8_000_000L,
            party = "Pemberi kerja",
            status = "Posted",
            notes = "Gaji pokok bulanan masuk"
        ),
        TransactionEntity(
            id = "TX-0002",
            date = "2026-11-02",
            type = "Expense",
            description = "Contoh belanja bulanan",
            category = "Rumah Tangga",
            accountId = "ACC-02",
            amount = 1_200_000L,
            party = "Supermarket",
            status = "Posted",
            notes = "Kebutuhan pokok dapur"
        ),
        TransactionEntity(
            id = "TX-0003",
            date = "2026-11-03",
            type = "Expense",
            description = "Contoh kebutuhan makan",
            category = "Makanan",
            accountId = "ACC-01",
            amount = 750_000L,
            party = "Warung & Resto",
            status = "Posted",
            notes = "Makan harian minggu pertama"
        ),
        TransactionEntity(
            id = "TX-0004",
            date = "2026-11-05",
            type = "Savings",
            description = "Contoh setoran tabungan",
            category = "Dana Darurat",
            accountId = "ACC-04",
            amount = 1_000_000L,
            party = "Diri sendiri",
            status = "Posted",
            notes = "Alokasi tabungan, bukan expense"
        ),
        TransactionEntity(
            id = "TX-0005",
            date = "2026-11-06",
            type = "Debt Payment",
            description = "Contoh cicilan KPR",
            category = "KPR",
            accountId = "ACC-02",
            amount = 1_500_000L,
            party = "Bank KPR",
            status = "Posted",
            notes = "Cicilan rutin bulanan rumah"
        )
    )

    val defaultBudgets = listOf(
        BudgetEntity(category = "Makanan", amount = 1_500_000L, period = "2026-11"),
        BudgetEntity(category = "Transportasi", amount = 700_000L, period = "2026-11"),
        BudgetEntity(category = "Rumah Tangga", amount = 1_500_000L, period = "2026-11"),
        BudgetEntity(category = "Tagihan", amount = 800_000L, period = "2026-11"),
        BudgetEntity(category = "Kesehatan", amount = 300_000L, period = "2026-11"),
        BudgetEntity(category = "Pendidikan", amount = 300_000L, period = "2026-11"),
        BudgetEntity(category = "Hiburan", amount = 300_000L, period = "2026-11"),
        BudgetEntity(category = "Belanja", amount = 500_000L, period = "2026-11"),
        BudgetEntity(category = "Lainnya", amount = 300_000L, period = "2026-11")
    )

    val defaultGoals = listOf(
        GoalEntity(
            id = "GOAL-01",
            name = "Dana darurat 6 bulan",
            targetAmount = 30_000_000L,
            currentAmount = 6_000_000L,
            dueDate = "2027-12-31",
            priority = "Tinggi",
            notes = "Target keamanan finansial utama"
        ),
        GoalEntity(
            id = "GOAL-02",
            name = "Dana pendidikan anak",
            targetAmount = 10_000_000L,
            currentAmount = 1_500_000L,
            dueDate = "2027-06-30",
            priority = "Sedang",
            notes = "Persiapan tahun ajaran baru"
        ),
        GoalEntity(
            id = "GOAL-03",
            name = "Liburan akhir tahun",
            targetAmount = 5_000_000L,
            currentAmount = 500_000L,
            dueDate = "2027-12-01",
            priority = "Rendah",
            notes = "Rencana liburan keluarga"
        )
    )

    val defaultDebts = listOf(
        DebtEntity(
            id = "DEBT-01",
            type = "KPR",
            lender = "Bank BTN / Mandiri",
            startDate = "2024-01-01",
            dueDate = "2039-01-01",
            monthlyInstallment = 2_500_000L,
            initialAmount = 250_000_000L,
            remainingAmount = 245_000_000L,
            notes = "Bunga floating 7.5%, tenor 15 tahun"
        ),
        DebtEntity(
            id = "DEBT-02",
            type = "Kartu Kredit",
            lender = "Bank BCA",
            startDate = "2026-10-01",
            dueDate = "2026-11-25",
            monthlyInstallment = 500_000L,
            initialAmount = 5_000_000L,
            remainingAmount = 3_500_000L,
            notes = "Cicilan 0% 12 bulan"
        )
    )

    val defaultAssets = listOf(
        AssetEntity("AST-01", "Laptop Kerja High-End", "Elektronik", "2025-01-01", 12_000_000L, 9_000_000L, "Pemakaian produktif"),
        AssetEntity("AST-02", "Sepeda Motor Matik", "Kendaraan", "2023-06-01", 22_000_000L, 17_000_000L, "Kondisi terawat"),
        AssetEntity("AST-03", "Perabot & Elektronik Rumah", "Rumah Tangga", "2024-03-01", 8_000_000L, 6_000_000L, "Kulkas, mesin cuci, kasur")
    )

    val defaultInvestments = listOf(
        InvestmentEntity("INV-01", "Reksa Dana", "Sucorinvest Pasar Uang", "2025-02-01", 100.0, 10_000L, 1_100_000L, "Rendah - Pasar Uang"),
        InvestmentEntity("INV-02", "Emas", "Logam Mulia Antam 2 gr", "2025-05-01", 2.0, 1_200_000L, 2_600_000L, "Rendah - Lindung Nilai"),
        InvestmentEntity("INV-03", "Saham", "BBCA / BBRI", "2025-08-01", 10.0, 50_000L, 560_000L, "Tinggi - Bluechip Fundamental")
    )
}
