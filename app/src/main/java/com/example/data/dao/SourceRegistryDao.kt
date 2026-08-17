package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SourceRegistryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceRegistryDao {
    @Query("SELECT * FROM source_registry ORDER BY id ASC")
    fun getAllSources(): Flow<List<SourceRegistryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SourceRegistryItem>)

    @Query("SELECT COUNT(*) FROM source_registry")
    suspend fun getCount(): Int
}
