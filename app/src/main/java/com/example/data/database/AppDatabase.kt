package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.DayProgressDao
import com.example.data.dao.FavoriteDao
import com.example.data.dao.FiqhDao
import com.example.data.dao.HadithDao
import com.example.data.dao.QuranDao
import com.example.data.dao.SourceRegistryDao
import com.example.data.model.DayProgress
import com.example.data.model.FavoriteItem
import com.example.data.model.FiqhItem
import com.example.data.model.HadithItem
import com.example.data.model.QuranItem
import com.example.data.model.SourceRegistryItem

@Database(
    entities = [
        QuranItem::class,
        HadithItem::class,
        FiqhItem::class,
        FavoriteItem::class,
        DayProgress::class,
        SourceRegistryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao
    abstract fun hadithDao(): HadithDao
    abstract fun fiqhDao(): FiqhDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun dayProgressDao(): DayProgressDao
    abstract fun sourceRegistryDao(): SourceRegistryDao
}
