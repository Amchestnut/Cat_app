package com.example.cat_app.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// colorScheme za light i dark temuuu
private val LightColors = lightColorScheme(
    primary               = CatBrown,
    onPrimary             = CatWhite,
    primaryContainer      = CatBorder,
    onPrimaryContainer    = CatWhite,
    secondary             = ScoreYellow,
    onSecondary           = CatWhite,
    secondaryContainer    = BadgeGreen,
    onSecondaryContainer  = CatWhite,
    tertiary              = Gold,
    onTertiary            = Color.Black,
    tertiaryContainer     = Bronze,
    onTertiaryContainer   = Color.Black,

    background            = CatBeige,
    onBackground          = Color.Black,
    surface               = CatWhite,
    onSurface             = Color.Black,
    surfaceVariant        = LightGray300,
    onSurfaceVariant      = Color.Black,
    outline               = LightGray300,

    error                 = ErrorRed600,
    onError               = Color.White
)

private val DarkColors = darkColorScheme(
    primary               = CatBorder,
    onPrimary             = Color.Black,
    primaryContainer      = CatBrown,
    onPrimaryContainer    = CatWhite,
    secondary             = BadgeGreen,
    onSecondary           = Color.Black,
    secondaryContainer    = ScoreYellow,
    onSecondaryContainer  = Color.Black,
    tertiary              = Bronze,
    onTertiary            = Color.Black,
    tertiaryContainer     = Gold,
    onTertiaryContainer   = Color.Black,

    background            = Color(0xFF121212),
    onBackground          = CatWhite,
    surface               = Color(0xFF1E1E1E),
    onSurface             = CatWhite,
    surfaceVariant        = LightGray300.copy(alpha = 0.3f),
    onSurfaceVariant      = CatWhite,
    outline               = LightGray300,

    error                 = ErrorRed600,
    onError               = Color.Black
)

// Gledamo koja je tema i namestamo i podesava system bars
@Composable
fun CatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // Android 12+ boje
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else      -> LightColors
    }

    // SideEffect za status i navigation bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar prati primary, nav bar prati background
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)

            // svetli ikonicki sadržaj u barovima kada je tamna tema
            controller.isAppearanceLightStatusBars      = !darkTheme
            controller.isAppearanceLightNavigationBars  = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
