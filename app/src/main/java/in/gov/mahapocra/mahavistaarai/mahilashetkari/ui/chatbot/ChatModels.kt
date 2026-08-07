package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot

import androidx.compose.ui.text.input.KeyboardType

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean
)

data class QuickReply(
    val label: String,
    val value: String
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val quickReplies: List<QuickReply> = emptyList(),
    val inputEnabled: Boolean = false,
    val busy: Boolean = false,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val ended: Boolean = false,
    val requestNavigateRoute: String? = null
)
