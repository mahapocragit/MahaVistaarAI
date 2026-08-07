package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation.Screen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings

/** The app's action bar: brand mark, title, and the EN/MR language switch. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MsTopAppBar(lang: AppLanguage, onLanguageChange: (AppLanguage) -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MS", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Mahila Shetkari",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            LanguageToggle(lang, onLanguageChange)
            Spacer(Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

private data class NavItem(val route: String, val icon: ImageVector, val label: (Strings.Nav) -> String)

private val navItems = listOf(
    NavItem(Screen.Home.route, Icons.Filled.Home) { it.home },
    NavItem(Screen.Eligibility.route, Icons.AutoMirrored.Filled.FactCheck) { it.eligibility },
    NavItem(Screen.Apply.route, Icons.Filled.Description) { it.applyNow },
    NavItem(Screen.Track.route, Icons.Filled.TrackChanges) { it.track },
    NavItem(Screen.Resources.route, Icons.AutoMirrored.Filled.MenuBook) { it.resources },
)

/** Primary section navigation, shown as a bottom bar per modern Android convention. */
@Composable
fun MsBottomNavBar(lang: AppLanguage, currentRoute: String, onNavigate: (String) -> Unit) {
    val nav = Strings.nav(lang)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(
                        text = item.label(nav),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun LanguageToggle(lang: AppLanguage, onLanguageChange: (AppLanguage) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(2.dp)
    ) {
        LangOption("EN", lang == AppLanguage.EN) { onLanguageChange(AppLanguage.EN) }
        LangOption("MR", lang == AppLanguage.MR) { onLanguageChange(AppLanguage.MR) }
    }
}

@Composable
private fun LangOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.onPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
