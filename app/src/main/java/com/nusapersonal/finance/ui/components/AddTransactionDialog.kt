package com.nusapersonal.finance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nusapersonal.finance.data.entity.AccountEntity
import com.nusapersonal.finance.ui.theme.EmeraldPrimary
import com.nusapersonal.finance.ui.theme.SurfaceDark
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onSave: (type: String, category: String, accountId: String, date: String, amount: Long, desc: String, party: String, notes: String) -> Unit
) {
    val transactionTypes = listOf("Expense", "Income", "Savings", "Debt Payment", "Investment", "Transfer")

    val categoriesMap = mapOf(
        "Expense" to listOf("Makanan", "Transportasi", "Rumah Tangga", "Tagihan", "Kesehatan", "Pendidikan", "Hiburan", "Belanja", "Lainnya"),
        "Income" to listOf("Gaji", "Usaha", "Bonus", "Freelance", "Hadiah", "Bunga/Dividen", "Lainnya"),
        "Savings" to listOf("Dana Darurat", "Tabungan Tujuan", "Tabungan Umum"),
        "Debt Payment" to listOf("KPR", "Kendaraan", "Kartu Kredit", "Pinjaman Pribadi", "Paylater"),
        "Investment" to listOf("Deposito", "Reksa Dana", "Saham", "Obligasi", "Emas", "Properti", "Kripto"),
        "Transfer" to listOf("Antar Rekening", "Dompet Digital")
    )

    var selectedType by remember { mutableStateOf("Expense") }
    var selectedCategory by remember { mutableStateOf("Makanan") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "ACC-01") }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var date by remember { mutableStateOf(todayStr) }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var party by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var typeExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }
    var accExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Catat Aktivitas Finansial", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Type Selector
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Aktivitas") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        transactionTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    selectedCategory = categoriesMap[type]?.firstOrNull() ?: "Lainnya"
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Selector
                val currentCats = categoriesMap[selectedType] ?: listOf("Lainnya")
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        currentCats.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // Account Selector
                ExposedDropdownMenuBox(
                    expanded = accExpanded,
                    onExpandedChange = { accExpanded = it }
                ) {
                    val currentAcc = accounts.find { it.id == selectedAccountId }
                    OutlinedTextField(
                        value = currentAcc?.let { "${it.name} (${it.type})" } ?: selectedAccountId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rekening / Dompet") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = accExpanded,
                        onDismissRequest = { accExpanded = false }
                    ) {
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.name} (${acc.type})") },
                                onClick = {
                                    selectedAccountId = acc.id
                                    accExpanded = false
                                }
                            )
                        }
                    }
                }

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (Opsional)") },
                    placeholder = { Text("Contoh: Makan siang nasi padang") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Party
                OutlinedTextField(
                    value = party,
                    onValueChange = { party = it },
                    label = { Text("Pihak / Toko (Opsional)") },
                    placeholder = { Text("Contoh: Warung Barokah") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    if (amt > 0) {
                        onSave(
                            selectedType,
                            selectedCategory,
                            selectedAccountId,
                            date,
                            amt,
                            description,
                            party,
                            notes
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
