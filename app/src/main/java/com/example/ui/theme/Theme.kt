package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = IslamicGreenPrimaryDark,
    onPrimary = Color(0xFF003822),
    primaryContainer = IslamicGreenContainerDark,
    onPrimaryContainer = Color(0xFFD8F3DC),
    secondary = IslamicGoldSecondaryDark,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = IslamicGoldContainerDark,
    onSecondaryContainer = Color(0xFFFFE08B),
    tertiary = IslamicClayTertiaryDark,
    onTertiary = Color(0xFF4A2800),
    background = DarkIslamicBackground,
    onBackground = Color(0xFFE2E3DF),
    surface = DarkIslamicSurface,
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = DarkIslamicSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0C9C1),
    outline = DarkIslamicOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IslamicGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = IslamicGreenContainer,
    onPrimaryContainer = Color(0xFF082016),
    secondary = IslamicGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = Color(0xFF4A3400),
    tertiary = IslamicClayTertiary,
    onTertiary = Color.White,
    background = ParchmentBackground,
    onBackground = Color(0xFF1B1C1A),
    surface = ParchmentSurface,
    onSurface = Color(0xFF1B1C1A),
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = Color(0xFF434844),
    outline = ParchmentOutline
  )

@Composable
fun ZadAlYoumTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
