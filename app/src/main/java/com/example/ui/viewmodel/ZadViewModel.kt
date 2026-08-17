package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppSettings
import com.example.data.model.DailyContentBundle
import com.example.data.model.FavoriteItem
import com.example.data.model.FiqhItem
import com.example.data.model.HadithItem
import com.example.data.model.PreferredMadhhab
import com.example.data.model.QuranItem
import com.example.data.model.SourceRegistryItem
import com.example.data.repository.ZadRepository
import com.example.worker.DailyReminderScheduler
import com.example.worker.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class SectionTab(val title: String) {
    ALL("الكل (٣×٣)"),
    QURAN("القرآن والتفسير (٣)"),
    HADITH("الحديث الشريف (٣)"),
    FIQH("الفقه والمذاهب (٣)")
}

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val quranResults: List<QuranItem> = emptyList(),
    val hadithResults: List<HadithItem> = emptyList(),
    val fiqhResults: List<FiqhItem> = emptyList()
) {
    val totalCount: Int get() = quranResults.size + hadithResults.size + fiqhResults.size
}

class ZadViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ZadRepository.getInstance(application)

    private val prefs = application.getSharedPreferences("zad_prefs", Context.MODE_PRIVATE)

    private val defaultDay: Int = run {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (dayOfYear in 1..365) dayOfYear else 1
    }

    private val _currentDay = MutableStateFlow(
        prefs.getInt("saved_current_day", defaultDay)
    )
    val currentDay: StateFlow<Int> = _currentDay.asStateFlow()

    private val _selectedSectionTab = MutableStateFlow(SectionTab.ALL)
    val selectedSectionTab: StateFlow<SectionTab> = _selectedSectionTab.asStateFlow()

    private val _appSettings = MutableStateFlow(
        AppSettings(
            preferredMadhhab = PreferredMadhhab.valueOf(
                prefs.getString("preferred_madhhab", PreferredMadhhab.ALL.name) ?: PreferredMadhhab.ALL.name
            ),
            arabicFontSizeScale = prefs.getFloat("font_scale", 1.0f),
            dailyReminderEnabled = prefs.getBoolean("reminder_enabled", true),
            reminderHour = prefs.getInt("reminder_hour", 8),
            reminderMinute = prefs.getInt("reminder_minute", 0)
        )
    )
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyBundle: StateFlow<DailyContentBundle> = _currentDay
        .flatMapLatest { day -> repository.getDailyBundle(day) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyContentBundle(dayNumber = _currentDay.value)
        )

    val completedDaysCount: StateFlow<Int> = repository.getCompletedDaysCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val favorites: StateFlow<List<FavoriteItem>> = repository.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteKeysSet: StateFlow<Set<String>> = favorites.map { list ->
        list.map { "${it.itemType}_${it.originalId}" }.toSet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val sources: StateFlow<List<SourceRegistryItem>> = repository.getAllSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _selectedFavoriteFilter = MutableStateFlow("ALL")
    val selectedFavoriteFilter: StateFlow<String> = _selectedFavoriteFilter.asStateFlow()

    init {
        NotificationHelper.createNotificationChannel(application)
        viewModelScope.launch {
            repository.ensureDatabasePopulated()
            if (_appSettings.value.dailyReminderEnabled) {
                DailyReminderScheduler.scheduleDailyReminder(
                    application,
                    _appSettings.value.reminderHour,
                    _appSettings.value.reminderMinute
                )
            }
        }
    }

    fun setDay(day: Int) {
        val clamped = day.coerceIn(1, 365)
        _currentDay.value = clamped
        prefs.edit().putInt("saved_current_day", clamped).apply()
    }

    fun nextDay() {
        val next = if (_currentDay.value >= 365) 1 else _currentDay.value + 1
        setDay(next)
    }

    fun previousDay() {
        val prev = if (_currentDay.value <= 1) 365 else _currentDay.value - 1
        setDay(prev)
    }

    fun jumpToToday() {
        setDay(defaultDay)
    }

    fun setSectionTab(tab: SectionTab) {
        _selectedSectionTab.value = tab
    }

    fun toggleDayCompleted() {
        val bundle = dailyBundle.value
        val newStatus = !bundle.isCompleted
        viewModelScope.launch {
            repository.setDayCompleted(bundle.dayNumber, newStatus)
        }
    }

    fun toggleQuranFavorite(item: QuranItem) {
        viewModelScope.launch {
            repository.toggleFavorite(
                type = "QURAN",
                originalId = item.id,
                dayNumber = item.dayNumber,
                title = "سورة ${item.surahName} - آية ${item.ayahNumber}",
                subtitle = "اليوم ${item.dayNumber} من ٣٦٥",
                mainText = "﴿${item.textArabic}﴾",
                extraDetails = item.tafsirText,
                sourceInfo = "${item.tafsirSourceName} (${item.sourceVersion})"
            )
        }
    }

    fun toggleHadithFavorite(item: HadithItem) {
        viewModelScope.launch {
            repository.toggleFavorite(
                type = "HADITH",
                originalId = item.id,
                dayNumber = item.dayNumber,
                title = "${item.collection} - ${item.hadithNumber}",
                subtitle = item.narrator,
                mainText = item.textArabic,
                extraDetails = "${item.grade} • ${item.takhrij}",
                sourceInfo = "${item.collection} (${item.sourceVersion})"
            )
        }
    }

    fun toggleFiqhFavorite(item: FiqhItem) {
        viewModelScope.launch {
            val madhhabText = buildString {
                append("الحنفي: ${item.hanafi}\n")
                append("المالكي: ${item.maliki}\n")
                append("الشافعي: ${item.shafii}\n")
                append("الحنبلي: ${item.hanbali}")
            }
            repository.toggleFavorite(
                type = "FIQH",
                originalId = item.id,
                dayNumber = item.dayNumber,
                title = item.topic,
                subtitle = "قسم ${item.category} • ${item.consensusStatus}",
                mainText = item.summary,
                extraDetails = "$madhhabText\n\nالدليل: ${item.evidence}",
                sourceInfo = item.sources
            )
        }
    }

    fun removeFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            repository.removeFavorite(item)
        }
    }

    fun setFavoriteFilter(filter: String) {
        _selectedFavoriteFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchState.value = _searchState.value.copy(query = query, isSearching = query.isNotBlank())
        if (query.isBlank()) {
            _searchState.value = SearchUiState()
            return
        }
        viewModelScope.launch {
            val quranFlow = repository.searchQuran(query)
            val hadithFlow = repository.searchHadith(query)
            val fiqhFlow = repository.searchFiqh(query)

            kotlinx.coroutines.flow.combine(quranFlow, hadithFlow, fiqhFlow) { q, h, f ->
                SearchUiState(
                    query = query,
                    isSearching = false,
                    quranResults = q,
                    hadithResults = h,
                    fiqhResults = f
                )
            }.collect { state ->
                _searchState.value = state
            }
        }
    }

    fun setPreferredMadhhab(madhhab: PreferredMadhhab) {
        _appSettings.value = _appSettings.value.copy(preferredMadhhab = madhhab)
        prefs.edit().putString("preferred_madhhab", madhhab.name).apply()
    }

    fun setFontSizeScale(scale: Float) {
        _appSettings.value = _appSettings.value.copy(arabicFontSizeScale = scale)
        prefs.edit().putFloat("font_scale", scale).apply()
    }

    fun setReminderEnabled(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(dailyReminderEnabled = enabled)
        prefs.edit().putBoolean("reminder_enabled", enabled).apply()
        val app = getApplication<Application>()
        if (enabled) {
            DailyReminderScheduler.scheduleDailyReminder(
                app,
                _appSettings.value.reminderHour,
                _appSettings.value.reminderMinute
            )
        } else {
            DailyReminderScheduler.cancelDailyReminder(app)
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        _appSettings.value = _appSettings.value.copy(reminderHour = hour, reminderMinute = minute)
        prefs.edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply()
        val app = getApplication<Application>()
        if (_appSettings.value.dailyReminderEnabled) {
            DailyReminderScheduler.scheduleDailyReminder(app, hour, minute)
        }
    }

    fun triggerTestNotification() {
        val app = getApplication<Application>()
        DailyReminderScheduler.triggerImmediateTestReminder(app)
    }

    fun shareText(context: Context, text: String, title: String = "مشاركة من زاد اليوم") {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
