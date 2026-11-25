package com.nusapersonal.finance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusapersonal.finance.data.entity.AssetEntity
import com.nusapersonal.finance.data.entity.DebtEntity
import com.nusapersonal.finance.data.entity.InvestmentEntity
import com.nusapersonal.finance.ui.components.CurrencyFormatter
import com.nusapersonal.finance.ui.theme.*

@Composable
fun AssetsDebtsScreen(
    debts: List<DebtEntity>,
    investments: List<InvestmentEntity>,
    assets: List<AssetEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // 1. Debts & Installments Header
        item {
            Text(
                "DAFTAR UTANG & CICILAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(debts) { d ->
            val paid = (d.initialAmount - d.remainingAmount).coerceAtLeast(0L)
            val pct = if (d.initialAmount > 0) (paid.toFloat() / d.initialAmount.toFloat()).coerceIn(0f, 1f) else 0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = RoseExpense.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        d.type,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoseExpense
                                    )
                                }
                                Text(d.lender, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Sisa: ${CurrencyFormatter.formatRupiah(d.remainingAmount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = RoseExpense
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cicilan/Bulan", fontSize = 10.sp, color = TextMuted)
                            Text(
                                CurrencyFormatter.formatRupiah(d.monthlyInstallment),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                        }
                    }

                    // Progress Pelunasan
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = AmberWarning,
                        trackColor = BackgroundDark,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Terbayar: ${CurrencyFormatter.formatRupiah(paid)} (${(pct * 100).toInt()}%)", fontSize = 10.sp, color = TextMuted)
                        Text("Jatuh Tempo: ${d.dueDate}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // 2. Investments Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "PORTOFOLIO INVESTASI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(investments) { inv ->
            val cost = (inv.units * inv.purchasePricePerUnit).toLong()
            val gain = inv.currentValue - cost
            val gainPct = if (cost > 0) ((gain.toDouble() / cost.toDouble()) * 100.0) else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = PurpleInvest.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        inv.instrument,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleInvest
                                    )
                                }
                                Text(inv.code, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Text(
                                "${CurrencyFormatter.formatDouble(inv.units)} unit @ ${CurrencyFormatter.formatRupiah(inv.purchasePricePerUnit)} (Modal: ${CurrencyFormatter.formatRupiah(cost)})",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                CurrencyFormatter.formatRupiah(inv.currentValue),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                (if (gain >= 0) "+" else "") + CurrencyFormatter.formatRupiah(gain) + " (${String.format(java.util.Locale.US, "%.1f", gainPct)}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (gain >= 0) EmeraldLight else RoseExpense
                            )
                        }
                    }

                    if (inv.riskNote.isNotBlank()) {
                        Text("Risiko: ${inv.riskNote}", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }

        // 3. Physical Assets Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "DAFTAR ASET PRIBADI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(assets) { a ->
            val diff = a.currentValue - a.purchasePrice
            val diffPct = if (a.purchasePrice > 0) ((diff.toDouble() / a.purchasePrice.toDouble()) * 100.0) else 0.0

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
                            Surface(
                                color = BlueSavings.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    a.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueSavings
                                )
                            }
                            Text(a.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text("Beli: ${CurrencyFormatter.formatRupiah(a.purchasePrice)} • ${a.purchaseDate}", fontSize = 10.sp, color = TextMuted)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            CurrencyFormatter.formatRupiah(a.currentValue),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            (if (diff >= 0) "+" else "") + CurrencyFormatter.formatRupiah(diff) + " (${String.format(java.util.Locale.US, "%.1f", diffPct)}%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (diff >= 0) EmeraldLight else RoseExpense
                        )
                    }
                }
            }
        }
    }
}
