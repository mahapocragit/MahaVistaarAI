package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.rememberVoiceInputLauncher
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotDialog(
    repository: MahilaShetkariRepository,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val historyStore = remember { ChatHistoryStore(context) }
    val viewModel = rememberVm(repository) { ChatBotViewModel(it, lang, historyStore) }
    val state by viewModel.uiState.collectAsState()
    val strings = Strings.chatBot(lang)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.requestNavigateRoute) {
        state.requestNavigateRoute?.let { route ->
            viewModel.consumeNavigationRequest()
            onDismiss()
            onNavigate(route)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.deleteAllConfirmTitle) },
            text = { Text(strings.deleteAllConfirmBody) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteAllHistory()
                }) { Text(strings.deleteAllConfirm) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(strings.cancel) }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(strings.title) },
                        navigationIcon = {
                            Icon(
                                Icons.Filled.SmartToy,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = { showDeleteConfirm = true }) {
                                Icon(Icons.Filled.DeleteSweep, contentDescription = strings.deleteAll)
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    MessageList(
                        messages = state.messages,
                        busy = state.busy,
                        typingLabel = strings.typing,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.quickReplies.isNotEmpty()) {
                        QuickReplyRow(replies = state.quickReplies, onSelect = viewModel::onQuickReply)
                    }
                    InputBar(
                        enabled = state.inputEnabled,
                        keyboardType = state.keyboardType,
                        hint = strings.inputHint,
                        languageTag = if (lang == AppLanguage.MR) "mr-IN" else "en-IN",
                        onSubmit = viewModel::onTextSubmit
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageList(
    messages: List<ChatMessage>,
    busy: Boolean,
    typingLabel: String,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size, busy) {
        val target = messages.size - 1 + if (busy) 1 else 0
        if (target >= 0) listState.animateScrollToItem(target)
    }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(messages, key = { it.id }) { message ->
            ChatBubble(message)
        }
        if (busy) {
            item(key = "typing") { TypingBubble(typingLabel) }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun TypingBubble(label: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickReplyRow(replies: List<QuickReply>, onSelect: (QuickReply) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(replies) { reply ->
            SuggestionChip(onClick = { onSelect(reply) }, label = { Text(reply.label) })
        }
    }
}

@Composable
private fun InputBar(
    enabled: Boolean,
    keyboardType: KeyboardType,
    hint: String,
    languageTag: String,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val startVoiceInput = rememberVoiceInputLauncher(languageTag = languageTag) { spoken ->
        onSubmit(spoken)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                enabled = enabled,
                placeholder = { Text(hint) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (text.isNotBlank()) { onSubmit(text); text = "" }
                }),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = startVoiceInput, enabled = enabled) {
                Icon(Icons.Filled.Mic, contentDescription = null)
            }
            IconButton(
                onClick = { if (text.isNotBlank()) { onSubmit(text); text = "" } },
                enabled = enabled && text.isNotBlank()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
    }
}
