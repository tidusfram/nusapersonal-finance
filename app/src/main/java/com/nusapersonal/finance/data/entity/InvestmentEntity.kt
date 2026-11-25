package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey
    val id: String,
    val instrument: String, // Reksa Dana, Emas, Saham
    val code: String,
    val purchaseDate: String,
    val units: Double,
    val purchasePricePerUnit: Long,
    val currentValue: Long,
    val riskNote: String = ""
)
