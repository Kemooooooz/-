package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FiqhItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FiqhDao {
    @Query("SELECT * FROM fiqh_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    fun getFiqhByDay(dayNumber: Int): Flow<List<FiqhItem>>

    @Query("SELECT * FROM fiqh_items WHERE dayNumber = :dayNumber ORDER BY itemOrder ASC")
    suspend fun getFiqhByDayDirect(dayNumber: Int): List<FiqhItem>

    @Query("SELECT * FROM fiqh_items WHERE topic LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR hanafi LIKE '%' || :query || '%' OR maliki LIKE '%' || :query || '%' OR shafii LIKE '%' || :query || '%' OR hanbali LIKE '%' || :query || '%'")
    fun searchFiqh(query: String): Flow<List<FiqhItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FiqhItem>)

    @Query("SELECT COUNT(*) FROM fiqh_items")
    suspend fun getCount(): Int
}
