package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_progress")
data class DayProgress(
    @PrimaryKey
    val dayNumber: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)
