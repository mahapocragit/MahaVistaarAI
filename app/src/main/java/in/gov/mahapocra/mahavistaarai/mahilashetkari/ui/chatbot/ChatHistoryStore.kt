package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.chatHistoryDataStore by preferencesDataStore(name = "ms_chat_history")
private val MESSAGES_KEY = stringPreferencesKey("messages")

/**
 * Persists the chatbot transcript on-device (as JSON in DataStore) so it
 * survives closing the dialog or restarting the app, until the user clears
 * it with "Delete all". Note that answers users type or speak into the chat
 * (Aadhaar number, OTP, mobile number, etc.) are stored here in plain text,
 * same as everywhere else the app keeps local state — nothing is uploaded.
 */
class ChatHistoryStore(context: Context) {
    private val dataStore = context.applicationContext.chatHistoryDataStore
    private val gson = Gson()
    private val listType = object : TypeToken<List<ChatMessage>>() {}.type

    suspend fun load(): List<ChatMessage> {
        val json = dataStore.data.first()[MESSAGES_KEY] ?: return emptyList()
        return try {
            gson.fromJson<List<ChatMessage>>(json, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun save(messages: List<ChatMessage>) {
        dataStore.edit { it[MESSAGES_KEY] = gson.toJson(messages) }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(MESSAGES_KEY) }
    }
}
