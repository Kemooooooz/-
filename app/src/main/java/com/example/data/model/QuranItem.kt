package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_items")
data class QuranItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayNumber: Int,
    val itemOrder: Int, // 1, 2, or 3
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val textArabic: String,
    val tafsirText: String,
    val tafsirSourceName: String = "التفسير الميسر",
    val sourceUrl: String = "https://quranenc.com",
    val sourceVersion: String = "v1.4.2",
    val verificationStatus: String = "FINAL"
)
