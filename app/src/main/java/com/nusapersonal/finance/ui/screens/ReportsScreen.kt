package com.nusapersonal.finance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusapersonal.finance.ui.components.CurrencyFormatter
import com.nusapersonal.finance.ui.theme.*
import com.nusapersonal.finance.viewmodel.FinanceUiState

@Composable
fun ReportsScreen(
    state: FinanceUiState,
    onResetSimulation: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Pulihkan Data Simulasi?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Semua transaksi baru akan diganti dengan data contoh simulasi awal dari Excel workbook.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetSimulation()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseExpense)
                ) {
                    Text("Ya, Pulihkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
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
        // 1. Laporan Arus Kas Bulanan
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "RINGKASAN LAPORAN ARUS KAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    ReportRow("Total Pemasukan (Income)", CurrencyFormatter.formatRupiah(state.monthlyIncome), EmeraldLight)
                    ReportRow("Total Pengeluaran (Expense)", CurrencyFormatter.formatRupiah(state.monthlyExpense), RoseExpense)
                    ReportRow("Pembayaran Cicilan Utang", CurrencyFormatter.formatRupiah(state.monthlyDebtPayment), AmberWarning)
                    ReportRow("Setoran Alokasi Tabungan", CurrencyFormatter.formatRupiah(state.monthlySavings), BlueSavings)
                    ReportRow("Investasi Dibeli", CurrencyFormatter.formatRupiah(state.monthlyInvestment), PurpleInvest)

                    Divider(color = BorderDark)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Arus Kas Tersedia", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(
                            CurrencyFormatter.formatRupiah(state.availableCashflow),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (state.availableCashflow >= 0) EmeraldLight else RoseExpense
                        )
                    }
                }
            }
        }

        // 2. Neraca Kekayaan Bersih (Net Worth)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "NERACA KEKAYAAN BERSIH (NET WORTH)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    ReportRow("Total Kas & Rekening Bank", CurrencyFormatter.formatRupiah(state.totalCash), TextPrimary)
                    ReportRow("Total Nilai Aset Fisik", CurrencyFormatter.formatRupiah(state.totalAssets), TextPrimary)
                    ReportRow("Total Nilai Portofolio Investasi", CurrencyFormatter.formatRupiah(state.totalInvestments), TextPrimary)
                    ReportRow("Dikurangi: Total Sisa Utang", "- " + CurrencyFormatter.formatRupiah(state.totalDebts), RoseExpense)

                    Divider(color = BorderDark)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Net Worth Bersih", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text(
                            CurrencyFormatter.formatRupiah(state.netWorth),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (state.netWorth >= 0) EmeraldLight else RoseExpense
                        )
                    }
                }
            }
        }

        // 3. Database & Simulation Reset Button
        item {
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔄 Pulihkan Seluruh Data Contoh Simulasi", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // 4. Workflows Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "ALUR KERJA FINANSIAL (WORKFLOW)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    WorkflowItem("Harian", "1. Catat Transaksi", "Masukkan pemasukan/pengeluaran segera setelah terjadi.")
                    WorkflowItem("Harian", "2. Klasifikasi Tepat", "Bedakan expense, savings, debt payment, investment, transfer.")
                    WorkflowItem("Mingguan", "3. Review Budget", "Periksa kategori yang mendekati batas anggaran 80%.")
                    WorkflowItem("Mingguan", "4. Review Tagihan", "Periksa tanggal jatuh tempo dan cicilan utang.")
                    WorkflowItem("Bulanan", "5. Rekonsiliasi & Net Worth", "Kunci transaksi bulan tersebut dan periksa pertumbuhan kekayaan.")
                }
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun WorkflowItem(freq: String, title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldLight)
            Text(freq, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = IndigoAccent)
        }
        Text(desc, fontSize = 11.sp, color = TextMuted)
    }
}
