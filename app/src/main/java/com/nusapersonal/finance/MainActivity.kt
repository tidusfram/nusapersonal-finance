package com.nusapersonal.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusapersonal.finance.ui.components.*
import com.nusapersonal.finance.ui.screens.*
import com.nusapersonal.finance.ui.theme.*
import com.nusapersonal.finance.viewmodel.FinanceViewModel
import com.nusapersonal.finance.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels {
        FinanceViewModelFactory((application as NusaFinanceApp).repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NusaFinanceTheme {
                val uiState by viewModel.uiState.collectAsState()
                var currentRoute by remember { mutableStateOf(Screen.Dashboard.route) }

                var showAddDialog by remember { mutableStateOf(false) }
                var showScanDialog by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(EmeraldPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("N", fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp)
                                    }
                                    Column {
                                        Text("NusaFinance", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                                        Text("Personal Finance System", fontSize = 10.sp, color = TextMuted)
                                    }
                                }
                            },
                            actions = {
                                // Period Selector Pill
                                Surface(
                                    color = SurfaceDark,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Text(
                                        "Nov 2026",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = BackgroundDark,
                                titleContentColor = TextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { currentRoute = it }
                        )
                    },
                    floatingActionButton = {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Quick Scan Floating Button
                            ExtendedFloatingActionButton(
                                onClick = { showScanDialog = true },
                                containerColor = SurfaceCard,
                                contentColor = EmeraldLight,
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(6.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Nota")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Nota", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // Manual Add Floating Button
                            FloatingActionButton(
                                onClick = { showAddDialog = true },
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White,
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Catat Manual")
                            }
                        }
                    },
                    containerColor = BackgroundDark
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        when (currentRoute) {
                            Screen.Dashboard.route -> DashboardScreen(
                                state = uiState,
                                onOpenScan = { showScanDialog = true },
                                onNavigateTransactions = { currentRoute = Screen.Transactions.route }
                            )
                            Screen.Transactions.route -> TransactionsScreen(
                                transactions = uiState.transactions,
                                onDeleteTransaction = { viewModel.deleteTransaction(it) }
                            )
                            Screen.Budget.route -> BudgetScreen(
                                budgets = uiState.budgets,
                                goals = uiState.goals,
                                expenseByCategory = uiState.expenseByCategory,
                                onAddFundsToGoal = { id, amt -> viewModel.addFundsToGoal(id, amt) }
                            )
                            Screen.AssetsDebts.route -> AssetsDebtsScreen(
                                debts = uiState.debts,
                                investments = uiState.investments,
                                assets = uiState.assets
                            )
                            Screen.Reports.route -> ReportsScreen(
                                state = uiState,
                                onResetSimulation = { viewModel.resetToDefaultSimulation() }
                            )
                        }
                    }

                    // Dialogs
                    if (showAddDialog) {
                        AddTransactionDialog(
                            accounts = uiState.accounts,
                            onDismiss = { showAddDialog = false },
                            onSave = { type, cat, accId, date, amt, desc, party, notes ->
                                viewModel.addTransaction(type, cat, accId, date, amt, desc, party, notes)
                            }
                        )
                    }

                    if (showScanDialog) {
                        ScanReceiptDialog(
                            accounts = uiState.accounts,
                            onDismiss = { showScanDialog = false },
                            onSaveScanned = { party, amt, date, cat, accId, desc, notes, img ->
                                viewModel.addTransaction("Expense", cat, accId, date, amt, desc, party, notes, img)
                            }
                        )
                    }
                }
            }
        }
    }
}
