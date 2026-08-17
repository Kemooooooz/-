package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.database.AppDatabase
import com.example.data.database.ZadDataSeeder
import com.example.data.model.DailyContentBundle
import com.example.data.model.DayProgress
import com.example.data.model.FavoriteItem
import com.example.data.model.FiqhItem
import com.example.data.model.HadithItem
import com.example.data.model.QuranItem
import com.example.data.model.SourceRegistryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class ZadRepository(private val database: AppDatabase) {

    private val quranDao = database.quranDao()
    private val hadithDao = database.hadithDao()
    private val fiqhDao = database.fiqhDao()
    private val favoriteDao = database.favoriteDao()
    private val dayProgressDao = database.dayProgressDao()
    private val sourceRegistryDao = database.sourceRegistryDao()

    suspend fun ensureDatabasePopulated() = withContext(Dispatchers.IO) {
        val count = quranDao.getCount()
        if (count == 0) {
            val quranList = ZadDataSeeder.generateAll365DaysQuran()
            val hadithList = ZadDataSeeder.generateAll365DaysHadith()
            val fiqhList = ZadDataSeeder.generateAll365DaysFiqh()
            val sources = ZadDataSeeder.getInitialSources()

            quranDao.insertAll(quranList)
            hadithDao.insertAll(hadithList)
            fiqhDao.insertAll(fiqhList)
            sourceRegistryDao.insertAll(sources)
        }
    }

    fun getDailyBundle(dayNumber: Int): Flow<DailyContentBundle> {
        return combine(
            quranDao.getQuranByDay(dayNumber),
            hadithDao.getHadithByDay(dayNumber),
            fiqhDao.getFiqhByDay(dayNumber),
            dayProgressDao.getProgressByDay(dayNumber)
        ) { quran, hadith, fiqh, progress ->
            DailyContentBundle(
                dayNumber = dayNumber,
                isCompleted = progress?.isCompleted ?: false,
                quranList = quran,
                hadithList = hadith,
                fiqhList = fiqh
            )
        }
    }

    fun getCompletedDaysCount(): Flow<Int> = dayProgressDao.getCompletedDaysCount()

    fun getAllProgress(): Flow<List<DayProgress>> = dayProgressDao.getAllProgress()

    suspend fun setDayCompleted(dayNumber: Int, completed: Boolean) = withContext(Dispatchers.IO) {
        dayProgressDao.saveProgress(
            DayProgress(
                dayNumber = dayNumber,
                isCompleted = completed,
                completedAt = if (completed) System.currentTimeMillis() else 0L
            )
        )
    }

    fun getAllFavorites(): Flow<List<FavoriteItem>> = favoriteDao.getAllFavorites()

    fun getFavoritesByType(type: String): Flow<List<FavoriteItem>> =
        if (type == "ALL") favoriteDao.getAllFavorites() else favoriteDao.getFavoritesByType(type)

    fun isFavorite(type: String, originalId: Long): Flow<Boolean> =
        favoriteDao.isFavorite(type, originalId)

    suspend fun toggleFavorite(
        type: String,
        originalId: Long,
        dayNumber: Int,
        title: String,
        subtitle: String,
        mainText: String,
        extraDetails: String,
        sourceInfo: String
    ): Boolean = withContext(Dispatchers.IO) {
        val exists = favoriteDao.isFavoriteDirect(type, originalId)
        if (exists) {
            favoriteDao.deleteFavoriteByTypeAndOriginalId(type, originalId)
            false
        } else {
            favoriteDao.insertFavorite(
                FavoriteItem(
                    itemType = type,
                    originalId = originalId,
                    dayNumber = dayNumber,
                    title = title,
                    subtitle = subtitle,
                    mainText = mainText,
                    extraDetails = extraDetails,
                    sourceInfo = sourceInfo
                )
            )
            true
        }
    }

    suspend fun removeFavorite(favoriteItem: FavoriteItem) = withContext(Dispatchers.IO) {
        favoriteDao.deleteFavorite(favoriteItem)
    }

    fun searchQuran(query: String): Flow<List<QuranItem>> = quranDao.searchQuran(query)
    fun searchHadith(query: String): Flow<List<HadithItem>> = hadithDao.searchHadith(query)
    fun searchFiqh(query: String): Flow<List<FiqhItem>> = fiqhDao.searchFiqh(query)

    fun getAllSources(): Flow<List<SourceRegistryItem>> = sourceRegistryDao.getAllSources()

    companion object {
        @Volatile
        private var INSTANCE: ZadRepository? = null

        fun getInstance(context: Context): ZadRepository {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zad_al_youm.db"
                ).build()
                val repo = ZadRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
