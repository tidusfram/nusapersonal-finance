package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val date: String, // YYYY-MM-DD
    val type: String, // Income, Expense, Savings, Debt Payment, Investment, Transfer
    val description: String,
    val category: String,
    val accountId: String,
    val amount: Long,
    val party: String = "",
    val transferId: String = "",
    val status: String = "Posted", // Posted, Draft
    val notes: String = "",
    val receiptImage: String = "" // base64 or path
)
