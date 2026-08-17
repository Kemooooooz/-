package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_items ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT * FROM favorite_items WHERE itemType = :type ORDER BY savedAt DESC")
    fun getFavoritesByType(type: String): Flow<List<FavoriteItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE itemType = :type AND originalId = :originalId)")
    fun isFavorite(type: String, originalId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE itemType = :type AND originalId = :originalId)")
    suspend fun isFavoriteDirect(type: String, originalId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItem): Long

    @Query("DELETE FROM favorite_items WHERE itemType = :type AND originalId = :originalId")
    suspend fun deleteFavoriteByTypeAndOriginalId(type: String, originalId: Long)

    @Delete
    suspend fun deleteFavorite(item: FavoriteItem)
}
