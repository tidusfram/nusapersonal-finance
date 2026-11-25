package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // Cash, Bank, E-Wallet, Savings
    val initialBalance: Long,
    val isActive: Boolean = true
)
