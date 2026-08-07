package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ms_settings")
private val LANGUAGE_KEY = stringPreferencesKey("language")

/** Persists the EN/MR toggle so the app remembers the user's choice between launches. */
class LanguagePreference(private val context: Context) {

    val language: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[LANGUAGE_KEY]) {
            "MR" -> AppLanguage.MR
            else -> AppLanguage.EN
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language.name }
    }
}
