package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.HadithItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    @Query("SELECT * FROM hadith_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    fun getHadithByDay(dayNumber: Int): Flow<List<HadithItem>>

    @Query("SELECT * FROM hadith_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    suspend fun getHadithByDayDirect(dayNumber: Int): List<HadithItem>

    @Query("SELECT * FROM hadith_items WHERE textArabic LIKE '%' || :query || '%' OR narrator LIKE '%' || :query || '%' OR collection LIKE '%' || :query || '%' OR bookName LIKE '%' || :query || '%'")
    fun searchHadith(query: String): Flow<List<HadithItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HadithItem>)

    @Query("SELECT COUNT(*) FROM hadith_items")
    suspend fun getCount(): Int
}
