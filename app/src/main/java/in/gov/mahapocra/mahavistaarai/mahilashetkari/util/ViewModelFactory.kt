package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository

/** One generic factory instead of a boilerplate Factory class per ViewModel —
 *  every screen ViewModel here takes only the repository as a dependency. */
class GenericViewModelFactory(
    private val create: (MahilaShetkariRepository) -> ViewModel,
    private val repository: MahilaShetkariRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create(repository) as T
}

@Composable
inline fun <reified T : ViewModel> rememberVm(
    repository: MahilaShetkariRepository,
    noinline create: (MahilaShetkariRepository) -> T
): T = viewModel(factory = GenericViewModelFactory(create, repository))
