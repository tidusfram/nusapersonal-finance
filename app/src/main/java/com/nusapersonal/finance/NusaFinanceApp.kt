package com.nusapersonal.finance

import android.app.Application
import com.nusapersonal.finance.data.database.AppDatabase
import com.nusapersonal.finance.data.repository.FinanceRepository

class NusaFinanceApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { FinanceRepository(database) }

    override fun onCreate() {
        super.onCreate()
    }
}
