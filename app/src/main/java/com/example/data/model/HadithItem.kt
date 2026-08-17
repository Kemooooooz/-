package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hadith_items")
data class HadithItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayNumber: Int,
    val itemOrder: Int, // 1, 2, or 3
    val collection: String, // e.g. "صحيح البخاري", "صحيح مسلم"
    val bookName: String, // e.g. "كتاب الإيمان", "كتاب العلم"
    val hadithNumber: String, // e.g. "رقم 1"
    val narrator: String, // e.g. "عن عمر بن الخطاب رضي الله عنه"
    val textArabic: String,
    val grade: String = "صحيح",
    val takhrij: String, // e.g. "أخرجه البخاري (1) ومسلم (1907)"
    val sourceUrl: String = "AhmedBaset/hadith-json",
    val sourceVersion: String = "v2.1.0",
    val verificationStatus: String = "FINAL"
)
