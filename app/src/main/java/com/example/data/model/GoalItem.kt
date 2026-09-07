package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals_90")
data class GoalItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val targetValue: Int,
    val currentValue: Int,
    val unit: String,
    val deadlineDays: Int = 90,
    val isCompleted: Boolean = false
) {
    val progressFraction: Float
        get() = if (targetValue <= 0) 0f else (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)
}
