package com.nusapersonal.finance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusapersonal.finance.data.entity.BudgetEntity
import com.nusapersonal.finance.data.entity.GoalEntity
import com.nusapersonal.finance.ui.components.CurrencyFormatter
import com.nusapersonal.finance.ui.theme.*

@Composable
fun BudgetScreen(
    budgets: List<BudgetEntity>,
    goals: List<GoalEntity>,
    expenseByCategory: Map<String, Long>,
    onAddFundsToGoal: (goalId: String, amount: Long) -> Unit
) {
    var topUpGoalId by remember { mutableStateOf<String?>(null) }
    var topUpAmountText by remember { mutableStateOf("500000") }

    // Dialog for adding funds to goal
    if (topUpGoalId != null) {
        val targetGoal = goals.find { it.id == topUpGoalId }
        AlertDialog(
            onDismissRequest = { topUpGoalId = null },
            containerColor = SurfaceDark,
            title = {
                Text("Tambah Setoran Dana", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target: ${targetGoal?.name}", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = topUpAmountText,
                        onValueChange = { topUpAmountText = it.filter { c -> c.isDigit() } },
                        label = { Text("Nominal Tambahan (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = topUpAmountText.toLongOrNull() ?: 0L
                        if (amt > 0) {
                            onAddFundsToGoal(topUpGoalId!!, amt)
                            topUpGoalId = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Tambah", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { topUpGoalId = null }) {
                    Text("Batal")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // 1. Overall Budget Summary
        val totalBudget = budgets.sumOf { it.amount }
        val totalRealized = budgets.sumOf { expenseByCategory[it.category] ?: 0L }
        val overallPct = if (totalBudget > 0) (totalRealized.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f) else 0f

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
                            "TOTAL ANGGARAN PERIODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "${(overallPct * 100).toInt()}% Terpakai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (overallPct >= 1f) RoseExpense else EmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        CurrencyFormatter.formatRupiah(totalRealized),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        "dari plafon anggaran ${CurrencyFormatter.formatRupiah(totalBudget)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { overallPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (overallPct >= 1f) RoseExpense else if (overallPct >= 0.8f) AmberWarning else EmeraldPrimary,
                        trackColor = BackgroundDark,
                    )
                }
            }
        }

        // 2. Category Budgets Header
        item {
            Text(
                "PLAFON & REALISASI PER KATEGORI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(budgets) { b ->
            val real = expenseByCategory[b.category] ?: 0L
            val pct = if (b.amount > 0) (real.toFloat() / b.amount.toFloat()) else 0f
            val remaining = b.amount - real

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(b.category, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Surface(
                                color = when {
                                    pct >= 1f -> RoseExpense.copy(alpha = 0.2f)
                                    pct >= 0.8f -> AmberWarning.copy(alpha = 0.2f)
                                    else -> EmeraldPrimary.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    when {
                                        pct >= 1f -> "Overbudget!"
                                        pct >= 0.8f -> "Waspada (>80%)"
                                        else -> "Aman"
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        pct >= 1f -> RoseExpense
                                        pct >= 0.8f -> AmberWarning
                                        else -> EmeraldLight
                                    }
                                )
                            }
                        }
                        Text("${(pct * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                    }

                    LinearProgressIndicator(
                        progress = { pct.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = when {
                            pct >= 1f -> RoseExpense
                            pct >= 0.8f -> AmberWarning
                            else -> EmeraldPrimary
                        },
                        trackColor = BackgroundDark,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Realisasi: ${CurrencyFormatter.formatRupiah(real)}", fontSize = 11.sp, color = TextSecondary)
                        Text("Plafon: ${CurrencyFormatter.formatRupiah(b.amount)}", fontSize = 11.sp, color = TextMuted)
                        Text(
                            "Sisa: ${CurrencyFormatter.formatRupiah(remaining)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remaining >= 0) EmeraldLight else RoseExpense
                        )
                    }
                }
            }
        }

        // 3. Goals / Target Tabungan
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "TARGET TABUNGAN & TUJUAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(goals) { g ->
            val pct = if (g.targetAmount > 0) (g.currentAmount.toFloat() / g.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
            val remaining = (g.targetAmount - g.currentAmount).coerceAtLeast(0L)

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
                                    color = when (g.priority) {
                                        "Tinggi" -> RoseExpense.copy(alpha = 0.2f)
                                        "Sedang" -> AmberWarning.copy(alpha = 0.2f)
                                        else -> EmeraldPrimary.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Prioritas ${g.priority}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Text("📅 ${g.dueDate}", fontSize = 10.sp, color = TextMuted)
                            }
                            Text(g.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            if (g.notes.isNotBlank()) {
                                Text(g.notes, fontSize = 11.sp, color = TextMuted)
                            }
                        }
                        Text("${(pct * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EmeraldLight)
                    }

                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = EmeraldPrimary,
                        trackColor = BackgroundDark,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Terkumpul: ${CurrencyFormatter.formatRupiah(g.currentAmount)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldLight)
                            Text("Sisa: ${CurrencyFormatter.formatRupiah(remaining)} dari ${CurrencyFormatter.formatRupiah(g.targetAmount)}", fontSize = 10.sp, color = TextMuted)
                        }
                        Button(
                            onClick = { topUpGoalId = g.id },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+ Tambah Dana", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
