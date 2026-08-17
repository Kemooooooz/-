package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class DailyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR).coerceIn(1, 365)
            NotificationHelper.showDailyReminderNotification(
                context = context,
                dayNumber = dayOfYear
            )
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
