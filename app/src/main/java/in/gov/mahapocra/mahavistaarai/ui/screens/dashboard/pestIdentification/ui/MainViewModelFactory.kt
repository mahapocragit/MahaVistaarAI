package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.repository.PredictRepository

class MainViewModelFactory(private val repo: PredictRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repo) as T
    }
}