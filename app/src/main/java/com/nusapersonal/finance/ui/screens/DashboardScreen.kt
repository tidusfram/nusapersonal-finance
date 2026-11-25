package com.nusapersonal.finance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusapersonal.finance.ui.components.CurrencyFormatter
import com.nusapersonal.finance.ui.theme.*
import com.nusapersonal.finance.viewmodel.FinanceUiState

@Composable
fun DashboardScreen(
    state: FinanceUiState,
    onOpenScan: () -> Unit,
    onNavigateTransactions: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // 1. Scan Nota Quick Action Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF059669), Color(0xFF0D9488), Color(0xFF10B981))
                        )
                    )
                    .clickable { onOpenScan() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan Nota",
                                tint = Color.White
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Scan Nota Otomatis",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        "OCR AI",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                "Foto struk, nominal & toko langsung tercatat",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Text("➔", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Net Worth Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF064E3B))
                        )
                    )
                    .border(1.dp, BorderDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Kekayaan Bersih (Net Worth)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                "Status Terkini",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        CurrencyFormatter.formatRupiah(state.netWorth),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = if (state.netWorth >= 0) EmeraldLight else RoseExpense
                    )
                    Text(
                        "Total Kas + Aset Fisik + Investasi - Total Sisa Utang",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = BackgroundDark.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Saldo Kas / Bank", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    CurrencyFormatter.formatRupiah(state.totalCash),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = BackgroundDark.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Kewajiban Utang", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    CurrencyFormatter.formatRupiah(state.totalDebts),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseExpense
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Arus Kas Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ARUS KAS PERIODE INI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Saving Rate: ${String.format(java.util.Locale.US, "%.1f", state.savingRate)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Income
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = BackgroundDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Pemasukan", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    CurrencyFormatter.formatRupiah(state.monthlyIncome),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }
                        // Expense
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = BackgroundDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Pengeluaran", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    CurrencyFormatter.formatRupiah(state.monthlyExpense),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseExpense
                                )
                            }
                        }
                        // Net Cashflow
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = BackgroundDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Arus Bersih", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    CurrencyFormatter.formatRupiah(state.netCashflow),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.netCashflow >= 0) TextPrimary else RoseExpense
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Accounts List
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MASTER REKENING & DOMPET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Lihat Mutasi →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary,
                        modifier = Modifier.clickable { onNavigateTransactions() }
                    )
                }

                state.accounts.forEach { acc ->
                    val balance = state.accountBalances[acc.id] ?: acc.initialBalance
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    color = when (acc.type) {
                                        "Cash" -> EmeraldPrimary.copy(alpha = 0.2f)
                                        "Bank" -> BlueSavings.copy(alpha = 0.2f)
                                        "E-Wallet" -> IndigoAccent.copy(alpha = 0.2f)
                                        else -> AmberWarning.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        acc.type,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Column {
                                    Text(acc.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(acc.id, fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Text(
                                CurrencyFormatter.formatRupiah(balance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (balance >= 0) TextPrimary else RoseExpense
                            )
                        }
                    }
                }
            }
        }

        // 5. Recent Transactions Preview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TRANSAKSI TERAKHIR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Semua Transaksi →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary,
                        modifier = Modifier.clickable { onNavigateTransactions() }
                    )
                }

                state.transactions.take(4).forEach { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        tx.type,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (tx.type) {
                                            "Income" -> EmeraldLight
                                            "Expense" -> RoseExpense
                                            "Savings" -> BlueSavings
                                            "Debt Payment" -> AmberWarning
                                            else -> PurpleInvest
                                        }
                                    )
                                    Text("•", fontSize = 10.sp, color = TextMuted)
                                    Text(tx.category, fontSize = 11.sp, color = TextSecondary)
                                }
                                Text(tx.description, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                Text("${tx.date} • ${tx.party}", fontSize = 10.sp, color = TextMuted)
                            }
                            Text(
                                (if (tx.type == "Income") "+ " else "- ") + CurrencyFormatter.formatRupiah(tx.amount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == "Income") EmeraldLight else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
