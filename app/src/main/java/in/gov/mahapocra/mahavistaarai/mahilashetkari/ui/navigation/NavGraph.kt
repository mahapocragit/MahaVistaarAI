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
import androidx.compose.ui.Modifier

@Composable
fun MsNavGraph(repository: MahilaShetkariRepository, languagePreference: LanguagePreference) {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route

    val lang by languagePreference.language.collectAsState(initial = AppLanguage.EN)
    val scope = rememberCoroutineScope()

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
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Home.route) { saveState = true }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(repository = repository, lang = lang, onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                })
            }
            composable(Screen.Eligibility.route) { EligibilityScreen(lang = lang) }
            composable(Screen.Apply.route) { ApplyScreen(repository = repository, lang = lang) }
            composable(Screen.Track.route) { TrackScreen(repository = repository, lang = lang) }
            composable(Screen.Resources.route) { ResourcesScreen(lang = lang) }
        }
    }
}
