package duckring.hambab.com.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 함밥 ColorScheme — Material3 onSurface/surface 등을 hb.* 로 override.
// v1 다크 모드 미지원: dark 진입해도 light scheme 강제.

private val HambabLightColors = lightColorScheme(
    primary = HbAmber,
    onPrimary = HbCreamCard,
    primaryContainer = HbAmber.copy(alpha = 0.15f),
    onPrimaryContainer = HbAmberDeep,

    secondary = HbBrown,
    onSecondary = HbCreamCard,
    secondaryContainer = HbCreamSoft,
    onSecondaryContainer = HbBrown,

    tertiary = HbAmberDeep,
    onTertiary = HbCreamCard,

    background = HbCream,
    onBackground = HbFg,

    surface = HbCreamCard,
    onSurface = HbFg,
    surfaceVariant = HbCreamSoft,
    onSurfaceVariant = HbFgSoft,

    outline = HbBorderStrong,
    outlineVariant = HbBorder,

    error = HbDanger,
    onError = HbCreamCard,
    errorContainer = HbDanger.copy(alpha = 0.10f),
    onErrorContainer = HbDanger,
)

@Composable
fun HambabTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // v1: 라이트 only — 시스템 다크여도 cream 톤 유지
    val colors = HambabLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            window.statusBarColor = HbCream.toArgb()
            window.navigationBarColor = HbCream.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = HambabTypography,
        shapes = HambabShapes,
        content = content,
    )
}
