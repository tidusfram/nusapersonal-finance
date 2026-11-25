package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey
    val id: String,
    val type: String, // KPR, Kartu Kredit, dll
    val lender: String,
    val startDate: String,
    val dueDate: String,
    val monthlyInstallment: Long,
    val initialAmount: Long,
    val remainingAmount: Long,
    val notes: String = ""
)
