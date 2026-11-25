package com.nusapersonal.finance.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nusapersonal.finance.data.dao.*
import com.nusapersonal.finance.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        DebtEntity::class,
        AssetEntity::class,
        InvestmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun debtDao(): DebtDao
    abstract fun assetDao(): AssetDao
    abstract fun investmentDao(): InvestmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nusapersonal_finance.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate default data from Excel on first run
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                database.accountDao().insertAll(DatabaseInitializer.defaultAccounts)
                                database.transactionDao().insertAll(DatabaseInitializer.defaultTransactions)
                                database.budgetDao().insertAll(DatabaseInitializer.defaultBudgets)
                                database.goalDao().insertAll(DatabaseInitializer.defaultGoals)
                                database.debtDao().insertAll(DatabaseInitializer.defaultDebts)
                                database.assetDao().insertAll(DatabaseInitializer.defaultAssets)
                                database.investmentDao().insertAll(DatabaseInitializer.defaultInvestments)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
