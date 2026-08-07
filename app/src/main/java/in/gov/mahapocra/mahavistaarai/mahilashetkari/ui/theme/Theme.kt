package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val MsColorScheme = lightColorScheme(
    primary = MsGreenPrimary,
    onPrimary = MsSurface,
    primaryContainer = MsGreenLight,
    onPrimaryContainer = MsGreenDark,
    secondary = MsAmber,
    onSecondary = MsSurface,
    secondaryContainer = MsAmberLight,
    onSecondaryContainer = MsAmberDark,
    tertiary = MsGreenDeep,
    onTertiary = MsSurface,
    tertiaryContainer = MsSurfaceTint,
    onTertiaryContainer = MsGreenDark,
    background = MsBackground,
    onBackground = MsOnSurface,
    surface = MsSurface,
    onSurface = MsOnSurface,
    surfaceVariant = MsGreenLight,
    onSurfaceVariant = MsOnSurfaceMuted,
    outline = MsOutline,
    error = MsError,
    errorContainer = MsErrorBg,
    onErrorContainer = MsError,
)

private val MsShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun MahilaShetkariTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MsColorScheme,
        typography = MsTypography,
        shapes = MsShapes,
        content = content
    )
}
