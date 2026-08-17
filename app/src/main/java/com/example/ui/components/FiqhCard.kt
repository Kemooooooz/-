package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FiqhItem
import com.example.data.model.PreferredMadhhab
import com.example.ui.theme.ConsensusGreen
import com.example.ui.theme.KhilafAmber
import com.example.ui.theme.MadhhabHanafiColor
import com.example.ui.theme.MadhhabHanbaliColor
import com.example.ui.theme.MadhhabMalikiColor
import com.example.ui.theme.MadhhabShafiiColor
import com.example.ui.theme.QuranFontFamily

private enum class MadhhabViewMode {
    TABS,
    ALL
}

@Composable
fun FiqhCard(
    item: FiqhItem,
    isFavorite: Boolean,
    fontScale: Float,
    preferredMadhhab: PreferredMadhhab,
    onToggleFavorite: () -> Unit,
    onShowSources: (FiqhItem) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isExpanded by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(MadhhabViewMode.TABS) }

    val initialTabIndex = when (preferredMadhhab) {
        PreferredMadhhab.HANAFI -> 0
        PreferredMadhhab.MALIKI -> 1
        PreferredMadhhab.SHAFII -> 2
        PreferredMadhhab.HANBALI -> 3
        PreferredMadhhab.ALL -> 0
    }
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }

    val consensusColor = if (item.isConsensus) ConsensusGreen else KhilafAmber
    val madhhabList = remember(item) {
        listOf(
            MadhhabData("المذهب الحنفي", item.hanafi, MadhhabHanafiColor, PreferredMadhhab.HANAFI),
            MadhhabData("المذهب المالكي", item.maliki, MadhhabMalikiColor, PreferredMadhhab.MALIKI),
            MadhhabData("المذهب الشافعي", item.shafii, MadhhabShafiiColor, PreferredMadhhab.SHAFII),
            MadhhabData("المذهب الحنبلي", item.hanbali, MadhhabHanbaliColor, PreferredMadhhab.HANBALI)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fiqh_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Header: Category badge & Consensus Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.itemOrder}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "باب ${item.category}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = consensusColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, consensusColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (item.isConsensus) Icons.Default.CheckCircle else Icons.Default.Gavel,
                            contentDescription = null,
                            tint = consensusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.isConsensus) "متفق عليه" else "مسألة خلافية",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = consensusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Structured Issue Title
            Text(
                text = item.topic,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (18 * fontScale).sp,
                    lineHeight = (28 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Clear Ruling Box (الخلاصة والفتوى المعتمدة)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الخلاصة والفتوى المعتمدة",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Text(
                            text = item.consensusStatus,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = consensusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (15 * fontScale).sp,
                            lineHeight = (25 * fontScale).sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Accordion Toggle for The Four Madhhabs
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "تفصيل أقوال المذاهب الأربعة والأدلة",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isExpanded) "انقر للطي" else "انقر للاطلاع على آراء الأئمة الأربعة والدليل",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "طي" else "توسيع",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 5. Expandable Content: Interactive Tabs + Evidence
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    // View Mode Switch (Tabs vs Vertical Comparison)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اختر المذهب الفقهي:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewMode == MadhhabViewMode.TABS) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { viewMode = MadhhabViewMode.TABS }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "تبويبات",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (viewMode == MadhhabViewMode.TABS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewMode == MadhhabViewMode.ALL) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { viewMode = MadhhabViewMode.ALL }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "مقارنة الكل",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (viewMode == MadhhabViewMode.ALL) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (viewMode == MadhhabViewMode.TABS) {
                        // Interactive Tabs
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = madhhabList[selectedTabIndex].color,
                                    height = 3.dp
                                )
                            }
                        ) {
                            madhhabList.forEachIndexed { index, madhhab ->
                                val isSelected = selectedTabIndex == index
                                val isUserPref = preferredMadhhab == madhhab.preferredEnum
                                Tab(
                                    selected = isSelected,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = madhhab.title.replace("المذهب ", ""),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) madhhab.color else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (isUserPref) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "★",
                                                    fontSize = 11.sp,
                                                    color = madhhab.color
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Selected Tab Ruling Card
                        AnimatedContent(
                            targetState = selectedTabIndex,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "madhhab_tab_content"
                        ) { targetIndex ->
                            val currentMadhhab = madhhabList[targetIndex]
                            val isUserPref = preferredMadhhab == currentMadhhab.preferredEnum

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = currentMadhhab.color.copy(alpha = 0.08f),
                                border = BorderStroke(1.5.dp, currentMadhhab.color.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = currentMadhhab.color
                                        ) {
                                            Text(
                                                text = currentMadhhab.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        if (isUserPref) {
                                            Text(
                                                text = "★ مذهبك المفضل في الإعدادات",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = currentMadhhab.color
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = currentMadhhab.ruling,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = (15 * fontScale).sp,
                                            lineHeight = (25 * fontScale).sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        // All Madhhabs Stacked View
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            madhhabList.forEach { madhhab ->
                                MadhhabDetailCard(
                                    schoolName = madhhab.title,
                                    ruling = madhhab.ruling,
                                    badgeColor = madhhab.color,
                                    isHighlighted = preferredMadhhab == madhhab.preferredEnum,
                                    fontScale = fontScale
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Evidence & Reference Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "📜",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الدليل والتعليل الشرعي:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.evidence,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = QuranFontFamily,
                                    fontSize = (14 * fontScale).sp,
                                    lineHeight = (22 * fontScale).sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "📚 المراجع الفقهية:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.sources,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 7. Footer Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onShowSources(item) },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("fiqh_sources_button_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "المراجع",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "المراجع المعتمدة",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val shareContent = buildString {
                                append("زاد اليوم • الفقه والمذاهب الأربعة\n\n")
                                append("المسألة: ${item.topic}\n")
                                append("الحالة: ${item.consensusStatus}\n")
                                append("الخلاصة: ${item.summary}\n\n")
                                append("أقوال المذاهب:\n")
                                append("• الحنفي: ${item.hanafi}\n")
                                append("• المالكي: ${item.maliki}\n")
                                append("• الشافعي: ${item.shafii}\n")
                                append("• الحنبلي: ${item.hanbali}\n\n")
                                append("الدليل: ${item.evidence}\n")
                                append("المراجع: ${item.sources}\n\n")
                                append("تطبيق زاد اليوم")
                            }
                            onShare(shareContent)
                        },
                        modifier = Modifier.testTag("fiqh_share_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة المسألة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("${item.topic}\n\n${item.summary}\n\nالمراجع: ${item.sources}"))
                            Toast.makeText(context, "تم نسخ خلاصة المسألة الفقهية", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ المسألة",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("fiqh_favorite_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isFavorite) "محفوظ في المفضلة" else "حفظ في المفضلة",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class MadhhabData(
    val title: String,
    val ruling: String,
    val color: Color,
    val preferredEnum: PreferredMadhhab
)

@Composable
private fun MadhhabDetailCard(
    schoolName: String,
    ruling: String,
    badgeColor: Color,
    isHighlighted: Boolean,
    fontScale: Float
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isHighlighted) badgeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (isHighlighted) BorderStroke(1.5.dp, badgeColor) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = schoolName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                if (isHighlighted) {
                    Text(
                        text = "★ مذهبك المفضل",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ruling,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = (13.5 * fontScale).sp,
                    lineHeight = (21 * fontScale).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
