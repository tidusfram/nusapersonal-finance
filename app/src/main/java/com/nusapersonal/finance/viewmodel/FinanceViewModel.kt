package com.nusapersonal.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nusapersonal.finance.data.entity.*
import com.nusapersonal.finance.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FinanceUiState(
    val activePeriod: String = "2026-11",
    val accounts: List<AccountEntity> = emptyList(),
    val accountBalances: Map<String, Long> = emptyMap(),
    val transactions: List<TransactionEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val goals: List<GoalEntity> = emptyList(),
    val debts: List<DebtEntity> = emptyList(),
    val assets: List<AssetEntity> = emptyList(),
    val investments: List<InvestmentEntity> = emptyList(),
    val monthlyIncome: Long = 0,
    val monthlyExpense: Long = 0,
    val monthlyDebtPayment: Long = 0,
    val monthlySavings: Long = 0,
    val monthlyInvestment: Long = 0,
    val netCashflow: Long = 0,
    val availableCashflow: Long = 0,
    val savingRate: Double = 0.0,
    val totalCash: Long = 0,
    val totalAssets: Long = 0,
    val totalInvestments: Long = 0,
    val totalDebts: Long = 0,
    val netWorth: Long = 0,
    val expenseByCategory: Map<String, Long> = emptyMap()
)

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _activePeriod = MutableStateFlow("2026-11")
    val activePeriod: StateFlow<String> = _activePeriod.asStateFlow()

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private data class CoreData(
        val period: String,
        val accounts: List<AccountEntity>,
        val transactions: List<TransactionEntity>,
        val budgets: List<BudgetEntity>
    )

    private data class AdditionalData(
        val goals: List<GoalEntity>,
        val debts: List<DebtEntity>,
        val assets: List<AssetEntity>,
        val investments: List<InvestmentEntity>
    )

    private fun observeData() {
        val coreFlow = combine(
            _activePeriod,
            repository.allAccounts,
            repository.allTransactions,
            repository.getAllBudgets()
        ) { period, accounts, transactions, budgets ->
            CoreData(period, accounts, transactions, budgets)
        }

        val additionalFlow = combine(
            repository.allGoals,
            repository.allDebts,
            repository.allAssets,
            repository.allInvestments
        ) { goals, debts, assets, investments ->
            AdditionalData(goals, debts, assets, investments)
        }

        combine(coreFlow, additionalFlow) { core, additional ->
            val period = core.period
            val accounts = core.accounts
            val transactions = core.transactions
            val budgets = core.budgets
            val goals = additional.goals
            val debts = additional.debts
            val assets = additional.assets
            val investments = additional.investments

            // 1. Calculate Account Balances
            val balances = accounts.associate { it.id to it.initialBalance }.toMutableMap()
            transactions.filter { it.status == "Posted" }.forEach { tx ->
                val amt = tx.amount
                val current = balances[tx.accountId] ?: 0L
                when (tx.type) {
                    "Income" -> balances[tx.accountId] = current + amt
                    "Expense", "Debt Payment", "Investment" -> balances[tx.accountId] = current - amt
                    "Savings" -> balances[tx.accountId] = current + amt
                    "Transfer" -> balances[tx.accountId] = current - amt
                }
            }

            // 2. Calculate Period Summary
            var income = 0L
            var expense = 0L
            var debtPayment = 0L
            var savings = 0L
            var investment = 0L
            val expByCat = mutableMapOf<String, Long>()

            transactions.filter { it.status == "Posted" && it.date.startsWith(period) }.forEach { tx ->
                when (tx.type) {
                    "Income" -> income += tx.amount
                    "Expense" -> {
                        expense += tx.amount
                        expByCat[tx.category] = (expByCat[tx.category] ?: 0L) + tx.amount
                    }
                    "Debt Payment" -> debtPayment += tx.amount
                    "Savings" -> savings += tx.amount
                    "Investment" -> investment += tx.amount
                }
            }

            val netCash = income - expense
            val available = income - (expense + debtPayment + savings + investment)
            val sRate = if (income > 0) ((savings + investment).toDouble() / income.toDouble()) * 100.0 else 0.0

            // 3. Calculate Net Worth
            val cashTotal = balances.values.sum()
            val assetsTotal = assets.sumOf { it.currentValue }
            val investmentsTotal = investments.sumOf { it.currentValue }
            val debtsTotal = debts.sumOf { it.remainingAmount }
            val totalNetWorth = (cashTotal + assetsTotal + investmentsTotal) - debtsTotal

            FinanceUiState(
                activePeriod = period,
                accounts = accounts,
                accountBalances = balances,
                transactions = transactions,
                budgets = budgets.filter { it.period == period }.ifEmpty { budgets },
                goals = goals,
                debts = debts,
                assets = assets,
                investments = investments,
                monthlyIncome = income,
                monthlyExpense = expense,
                monthlyDebtPayment = debtPayment,
                monthlySavings = savings,
                monthlyInvestment = investment,
                netCashflow = netCash,
                availableCashflow = available,
                savingRate = sRate,
                totalCash = cashTotal,
                totalAssets = assetsTotal,
                totalInvestments = investmentsTotal,
                totalDebts = debtsTotal,
                netWorth = totalNetWorth,
                expenseByCategory = expByCat
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun setPeriod(period: String) {
        _activePeriod.value = period
    }

    fun addTransaction(
        type: String,
        category: String,
        accountId: String,
        date: String,
        amount: Long,
        desc: String,
        party: String,
        notes: String,
        receiptImage: String = ""
    ) {
        viewModelScope.launch {
            val newId = "TX-" + System.currentTimeMillis().toString().takeLast(5)
            val tx = TransactionEntity(
                id = newId,
                date = date,
                type = type,
                description = desc.ifBlank { category },
                category = category,
                accountId = accountId,
                amount = amount,
                party = party,
                status = "Posted",
                notes = notes,
                receiptImage = receiptImage
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun addFundsToGoal(goalId: String, amount: Long) {
        viewModelScope.launch {
            repository.addFundsToGoal(goalId, amount)
        }
    }

    fun resetToDefaultSimulation() {
        viewModelScope.launch {
            repository.resetToDefaultSimulation()
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
