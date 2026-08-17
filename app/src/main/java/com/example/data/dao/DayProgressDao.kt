package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DayProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface DayProgressDao {
    @Query("SELECT * FROM day_progress")
    fun getAllProgress(): Flow<List<DayProgress>>

    @Query("SELECT * FROM day_progress WHERE dayNumber = :dayNumber LIMIT 1")
    fun getProgressByDay(dayNumber: Int): Flow<DayProgress?>

    @Query("SELECT COUNT(*) FROM day_progress WHERE isCompleted = 1")
    fun getCompletedDaysCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: DayProgress)
}
