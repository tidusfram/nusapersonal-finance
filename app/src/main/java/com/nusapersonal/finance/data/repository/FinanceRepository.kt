package com.nusapersonal.finance.data.repository

import com.nusapersonal.finance.data.database.AppDatabase
import com.nusapersonal.finance.data.database.DatabaseInitializer
import com.nusapersonal.finance.data.entity.*
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val database: AppDatabase) {

    private val accountDao = database.accountDao()
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val goalDao = database.goalDao()
    private val debtDao = database.debtDao()
    private val assetDao = database.assetDao()
    private val investmentDao = database.investmentDao()

    // Observables
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()
    val allAssets: Flow<List<AssetEntity>> = assetDao.getAllAssets()
    val allInvestments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()

    fun getBudgetsByPeriod(period: String): Flow<List<BudgetEntity>> = budgetDao.getBudgetsByPeriod(period)
    fun getAllBudgets(): Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    // Transaction actions
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: String) {
        transactionDao.deleteById(id)
    }

    // Goal actions
    suspend fun addFundsToGoal(goalId: String, amount: Long) {
        goalDao.addFundsToGoal(goalId, amount)
    }

    // Reset to simulation data
    suspend fun resetToDefaultSimulation() {
        accountDao.deleteAll()
        transactionDao.deleteAll()
        budgetDao.deleteAll()
        goalDao.deleteAll()
        debtDao.deleteAll()
        assetDao.deleteAll()
        investmentDao.deleteAll()

        accountDao.insertAll(DatabaseInitializer.defaultAccounts)
        transactionDao.insertAll(DatabaseInitializer.defaultTransactions)
        budgetDao.insertAll(DatabaseInitializer.defaultBudgets)
        goalDao.insertAll(DatabaseInitializer.defaultGoals)
        debtDao.insertAll(DatabaseInitializer.defaultDebts)
        assetDao.insertAll(DatabaseInitializer.defaultAssets)
        investmentDao.insertAll(DatabaseInitializer.defaultInvestments)
    }
}
