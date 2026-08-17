package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fiqh_items")
data class FiqhItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayNumber: Int,
    val itemOrder: Int, // 1, 2, or 3
    val category: String, // e.g. "الطهارة", "الصلاة", "الصيام", "الزكاة"
    val topic: String, // Topic question or statement
    val consensusStatus: String, // "المسألة متفق عليها بالإجماع" or "في المسألة خلاف معتبر بين المذاهب"
    val isConsensus: Boolean,
    val summary: String, // Concise ruling summary
    val hanafi: String, // Hanafi position & reasoning
    val maliki: String, // Maliki position & reasoning
    val shafii: String, // Shafi'i position & reasoning
    val hanbali: String, // Hanbali position & reasoning
    val evidence: String, // Textual evidence
    val sources: String, // Classical references: المغني، المجموع، بدائع الصنائع، بداية المجتهد
    val verificationStatus: String = "FINAL"
)
