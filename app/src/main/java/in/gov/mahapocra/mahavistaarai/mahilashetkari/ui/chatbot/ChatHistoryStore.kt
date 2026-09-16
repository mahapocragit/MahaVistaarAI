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
private val SESSION_KEY = stringPreferencesKey("session")

/**
 * Persists the chatbot transcript and in-progress session state on-device
 * (as JSON in DataStore) so it survives closing the dialog or restarting
 * the app, until the user clears it with "Delete all". The session state
 * (current step + scratch data such as the fetched district list) lets the
 * dialog resume silently — without repeating the greeting — instead of
 * only replaying the transcript. Note that answers users type or speak
 * into the chat (Aadhaar number, OTP, mobile number, etc.) are stored here
 * in plain text, same as everywhere else the app keeps local state —
 * nothing is uploaded.
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

    suspend fun loadSession(): ChatSessionState? {
        val json = dataStore.data.first()[SESSION_KEY] ?: return null
        return try {
            gson.fromJson(json, ChatSessionState::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveSession(session: ChatSessionState) {
        dataStore.edit { it[SESSION_KEY] = gson.toJson(session) }
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(MESSAGES_KEY)
            it.remove(SESSION_KEY)
        }
    }
}