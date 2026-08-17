package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "source_registry")
data class SourceRegistryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceName: String,
    val category: String, // "القرآن والتفسير", "الحديث الشريف", "الفقه والمذاهب"
    val authority: String, // e.g. "مجمع الملك فهد لطباعة المصحف الشريف", "دار التأصيل", "الموسوعة الفقهية الكويتية"
    val repository: String,
    val version: String,
    val commitHash: String,
    val license: String,
    val reviewStage: String = "FINAL", // DRAFT, SOURCE_CHECKED, TEXT_CHECKED, SCHOLARLY_REVIEW, FINAL, PUBLISHED
    val description: String
)
