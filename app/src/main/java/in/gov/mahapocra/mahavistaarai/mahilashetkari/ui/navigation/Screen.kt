package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Eligibility : Screen("eligibility")
    data object Apply : Screen("apply")
    data object Track : Screen("track")
    data object Resources : Screen("resources")
}
