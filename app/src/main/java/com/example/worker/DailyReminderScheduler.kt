package com.example.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyReminderScheduler {

    const val WORK_NAME = "zad_daily_reminder_periodic_work"
    const val IMMEDIATE_TEST_WORK_NAME = "zad_immediate_test_reminder"

    fun scheduleDailyReminder(
        context: Context,
        hour: Int,
        minute: Int
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time has already passed today, schedule for tomorrow
        if (target.before(now) || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            24,
            TimeUnit.HOURS,
            15,
            TimeUnit.MINUTES // Flex interval for battery efficiency
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag("zad_daily_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun triggerImmediateTestReminder(context: Context) {
        val testRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .addTag("zad_test_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_TEST_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            testRequest
        )
    }
}
