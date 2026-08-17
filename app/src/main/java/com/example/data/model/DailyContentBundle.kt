package com.example.data.model

data class DailyContentBundle(
    val dayNumber: Int,
    val isCompleted: Boolean = false,
    val quranList: List<QuranItem> = emptyList(),
    val hadithList: List<HadithItem> = emptyList(),
    val fiqhList: List<FiqhItem> = emptyList()
)

enum class PreferredMadhhab(val titleArabic: String, val displayName: String) {
    ALL("عرض جميع المذاهب بالتساوي", "الكل"),
    HANAFI("المذهب الحنفي", "الحنفي"),
    MALIKI("المذهب المالكي", "المالكي"),
    SHAFII("المذهب الشافعي", "الشافعي"),
    HANBALI("المذهب الحنبلي", "الحنبلي")
}

data class AppSettings(
    val preferredMadhhab: PreferredMadhhab = PreferredMadhhab.ALL,
    val arabicFontSizeScale: Float = 1.0f,
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val isDarkMode: Boolean? = null // null means system default
)
