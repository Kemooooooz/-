package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.QuranItem
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    @Query("SELECT * FROM quran_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    fun getQuranByDay(dayNumber: Int): Flow<List<QuranItem>>

    @Query("SELECT * FROM quran_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    suspend fun getQuranByDayDirect(dayNumber: Int): List<QuranItem>

    @Query("SELECT * FROM quran_items WHERE textArabic LIKE '%' || :query || '%' OR tafsirText LIKE '%' || :query || '%' OR surahName LIKE '%' || :query || '%'")
    fun searchQuran(query: String): Flow<List<QuranItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuranItem>)

    @Query("SELECT COUNT(*) FROM quran_items")
    suspend fun getCount(): Int
}
