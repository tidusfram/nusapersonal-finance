package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String, // Elektronik, Kendaraan, Rumah Tangga
    val purchaseDate: String,
    val purchasePrice: Long,
    val currentValue: Long,
    val notes: String = ""
)
