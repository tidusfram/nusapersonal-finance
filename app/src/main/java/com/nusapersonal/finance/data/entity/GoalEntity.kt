package com.nusapersonal.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val dueDate: String,
    val priority: String = "Sedang", // Tinggi, Sedang, Rendah
    val notes: String = ""
)
