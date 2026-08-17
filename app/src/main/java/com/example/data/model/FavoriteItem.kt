package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemType: String, // "QURAN", "HADITH", "FIQH"
    val originalId: Long,
    val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val mainText: String,
    val extraDetails: String,
    val sourceInfo: String,
    val savedAt: Long = System.currentTimeMillis()
)
