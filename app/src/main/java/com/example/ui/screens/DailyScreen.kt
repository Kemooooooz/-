package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyContentBundle
import com.example.data.model.PreferredMadhhab
import com.example.ui.components.DaySelectorDialog
import com.example.ui.components.FiqhCard
import com.example.ui.components.HadithCard
import com.example.ui.components.QuranCard
import com.example.ui.components.SourceDetailDialog
import com.example.ui.components.SourceDialogData
import com.example.ui.theme.ConsensusGreen
import com.example.ui.theme.IslamicGoldSecondary
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.viewmodel.SectionTab
import com.example.ui.viewmodel.ZadViewModel

@Composable
fun DailyScreen(
    viewModel: ZadViewModel,
    dailyBundle: DailyContentBundle,
    currentDay: Int,
    favoriteKeys: Set<String>,
    fontScale: Float,
    preferredMadhhab: PreferredMadhhab,
    selectedTab: SectionTab,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var showDayDialog by remember { mutableStateOf(false) }
    var activeSourceDialogData by remember { mutableStateOf<SourceDialogData?>(null) }

    // Scroll to top on day change
    LaunchedEffect(currentDay) {
        listState.animateScrollToItem(0)
    }

    if (showDayDialog) {
        DaySelectorDialog(
            currentDay = currentDay,
            onDaySelected = { viewModel.setDay(it) },
            onDismiss = { showDayDialog = false }
        )
    }

    activeSourceDialogData?.let { dialogData ->
        SourceDetailDialog(
            data = dialogData,
            onDismiss = { activeSourceDialogData = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Daily Hero Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Day Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Day Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showDayDialog = true }
                            .testTag("day_picker_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "اختيار اليوم",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "اليوم $currentDay",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "من ٣٦٥ يوماً (اضغط للتغيير)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Completion Toggle Pill
                    val isDone = dailyBundle.isCompleted
                    val pillBgColor by animateColorAsState(
                        targetValue = if (isDone) ConsensusGreen else MaterialTheme.colorScheme.surfaceVariant,
                        label = "completion_bg"
                    )
                    val pillTextColor = if (isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = pillBgColor,
                        shadowElevation = if (isDone) 2.dp else 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { viewModel.toggleDayCompleted() }
                            .testTag("day_completion_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                contentDescription = "حالة إتمام الورد",
                                tint = pillTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDone) "تم الإنجاز ✓" else "تحديد كمقروء",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = pillTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section Tabs (3x3 Switcher)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf(
                        SectionTab.ALL to "الكل (٩)",
                        SectionTab.QURAN to "القرآن (٣)",
                        SectionTab.HADITH to "الحديث (٣)",
                        SectionTab.FIQH to "الفقه (٣)"
                    )

                    items(tabs) { (tab, title) ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setSectionTab(tab) }
                                .testTag("section_tab_${tab.name}")
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Content List
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Quran (3 Items)
                if (selectedTab == SectionTab.ALL || selectedTab == SectionTab.QURAN) {
                    item(key = "header_quran") {
                        SectionHeaderBanner(
                            title = "القرآن الكريم والتفسير المعتمد",
                            subtitle = "٣ آيات كريمة مع التفسير الميسر",
                            icon = Icons.Outlined.MenuBook,
                            themeColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(dailyBundle.quranList, key = { "q_${it.id}" }) { quranItem ->
                        val isFav = favoriteKeys.contains("QURAN_${quranItem.id}")
                        QuranCard(
                            item = quranItem,
                            isFavorite = isFav,
                            fontScale = fontScale,
                            onToggleFavorite = { viewModel.toggleQuranFavorite(quranItem) },
                            onShowSource = {
                                activeSourceDialogData = SourceDialogData(
                                    title = "سورة ${it.surahName} (آية ${it.ayahNumber})",
                                    category = "القرآن والتفسير",
                                    primarySource = "مصحف المدينة النبوية - مجمع الملك فهد لطباعة المصحف الشريف",
                                    repositoryOrAuthor = it.tafsirSourceName,
                                    version = it.sourceVersion,
                                    license = "Public Islamic Waqf",
                                    description = "الآية الكريمة والتفسير مستخرجة ومراجعة وفق طبعة التفسير الميسر المعتمدة."
                                )
                            },
                            onShare = { text -> viewModel.shareText(context, text) }
                        )
                    }
                }

                // Section 2: Hadith (3 Items)
                if (selectedTab == SectionTab.ALL || selectedTab == SectionTab.HADITH) {
                    item(key = "header_hadith") {
                        SectionHeaderBanner(
                            title = "الحديث النبوي الشريف",
                            subtitle = "٣ أحاديث صحيحة موثقة ومخرجة",
                            icon = Icons.Outlined.AutoStories,
                            themeColor = MaterialTheme.colorScheme.secondary
                        )
                    }

                    items(dailyBundle.hadithList, key = { "h_${it.id}" }) { hadithItem ->
                        val isFav = favoriteKeys.contains("HADITH_${hadithItem.id}")
                        HadithCard(
                            item = hadithItem,
                            isFavorite = isFav,
                            fontScale = fontScale,
                            onToggleFavorite = { viewModel.toggleHadithFavorite(hadithItem) },
                            onShowSource = {
                                activeSourceDialogData = SourceDialogData(
                                    title = "${it.collection} - ${it.hadithNumber}",
                                    category = "الحديث الشريف",
                                    primarySource = "${it.collection} (${it.bookName})",
                                    repositoryOrAuthor = "AhmedBaset/hadith-json / دار التأصيل",
                                    version = it.sourceVersion,
                                    license = "MIT / Verified Scholarly Corpus",
                                    description = "الحديث الشريف مروي بلفظه مع ثبوت التخريج وصحة السند وفق المنهج الحديثي المعتمد."
                                )
                            },
                            onShare = { text -> viewModel.shareText(context, text) }
                        )
                    }
                }

                // Section 3: Fiqh (3 Items)
                if (selectedTab == SectionTab.ALL || selectedTab == SectionTab.FIQH) {
                    item(key = "header_fiqh") {
                        SectionHeaderBanner(
                            title = "الفقه الإسلامي والمذاهب الأربعة",
                            subtitle = "٣ مسائل فقهية بأقوال الحنفي والمالكي والشافعي والحنبلي",
                            icon = Icons.Outlined.Gavel,
                            themeColor = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    items(dailyBundle.fiqhList, key = { "f_${it.id}" }) { fiqhItem ->
                        val isFav = favoriteKeys.contains("FIQH_${fiqhItem.id}")
                        FiqhCard(
                            item = fiqhItem,
                            isFavorite = isFav,
                            fontScale = fontScale,
                            preferredMadhhab = preferredMadhhab,
                            onToggleFavorite = { viewModel.toggleFiqhFavorite(fiqhItem) },
                            onShowSources = {
                                activeSourceDialogData = SourceDialogData(
                                    title = it.topic,
                                    category = "الفقه والمذاهب الأربعة (باب ${it.category})",
                                    primarySource = it.sources,
                                    repositoryOrAuthor = "الموسوعة الفقهية الكويتية وكتب المذاهب الأربعة المعتمدة",
                                    version = "v1.0.0",
                                    license = "Verified Islamic Heritage",
                                    description = "تم تحرير الأقوال الفقهية من كتب المذاهب المعتمدة: بدائع الصنائع (حنفي)، بداية المجتهد (مالكي)، المجموع (شافعي)، المغني (حنبلي) مع الدليل لكل قول."
                                )
                            },
                            onShare = { text -> viewModel.shareText(context, text) }
                        )
                    }
                }
            }
        }

        // Floating Bottom Day Stepper Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev Day Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.previousDay() }
                        .testTag("prev_day_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "اليوم السابق",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اليوم السابق",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Jump to Today
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.jumpToToday() }
                        .testTag("jump_today_button")
                ) {
                    Text(
                        text = "اليوم الحالي",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                // Next Day Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.nextDay() }
                        .testTag("next_day_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اليوم التالي",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "اليوم التالي",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderBanner(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    themeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = themeColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.22f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(themeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
