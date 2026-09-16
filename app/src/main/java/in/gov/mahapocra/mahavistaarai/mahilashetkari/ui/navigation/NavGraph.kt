package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation

import android.R.attr.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.apply.ApplyScreen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsBottomNavBar
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTopAppBar
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.eligibility.EligibilityScreen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.home.HomeScreen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.resources.ResourcesScreen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.track.TrackScreen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.LanguagePreference
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MsNavGraph(repository: MahilaShetkariRepository, languagePreference: LanguagePreference) {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route

    val lang by languagePreference.language.collectAsState(initial = AppLanguage.EN)
    val scope = rememberCoroutineScope()

    // Holds an ack. no. handed off from the chatbot for the Track screen to
    // pre-fill. Kept outside the NavController (rather than as a nav arg)
    // because restoreState — needed for the bottom bar to behave — restores
    // Track's previously *saved* back stack entry instead of honoring a new
    // argument, which would silently drop a second hand-off.
    var pendingTrackAckNo by remember { mutableStateOf<String?>(null) }

    // Single navigation recipe used everywhere (bottom bar and in-screen
    // shortcuts alike) so the back stack stays consistent — mixing this
    // with a plain navController.navigate() elsewhere left stale/duplicate
    // entries that made the bottom bar stop responding after navigating
    // in from a screen shortcut. [payload] is only meaningful for Track.
    val onNavigate: (String, String?) -> Unit = { route, payload ->
        if (route == Screen.Track.route) pendingTrackAckNo = payload
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(Screen.Home.route) { saveState = true }
        }
    }

    Scaffold(
        topBar = {
            MsTopAppBar(
                lang = lang,
                onLanguageChange = { newLang -> scope.launch { languagePreference.setLanguage(newLang) } }
            )
        },
        bottomBar = {
            MsBottomNavBar(
                lang = lang,
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(repository = repository, lang = lang, onNavigate = onNavigate)
            }
            composable(Screen.Eligibility.route) { EligibilityScreen(lang = lang) }
            composable(Screen.Apply.route) { ApplyScreen(repository = repository, lang = lang) }
            composable(Screen.Track.route) {
                TrackScreen(repository = repository, lang = lang, initialAckNo = pendingTrackAckNo)
            }
            composable(Screen.Resources.route) { ResourcesScreen(lang = lang) }
        }
    }
}
