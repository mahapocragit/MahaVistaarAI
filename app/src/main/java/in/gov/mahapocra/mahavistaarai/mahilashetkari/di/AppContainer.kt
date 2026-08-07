package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.di

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.NetworkModule
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository

/** Hand-rolled DI container (no Hilt/Dagger) — kept simple and dependency-free
 *  since Gradle builds can't be verified in this environment. Swap for Hilt
 *  later if the project's build gets wired up in Android Studio. */
class AppContainer {
    val repository: MahilaShetkariRepository by lazy {
        MahilaShetkariRepository(NetworkModule.mahilaShetkariApi)
    }
}
