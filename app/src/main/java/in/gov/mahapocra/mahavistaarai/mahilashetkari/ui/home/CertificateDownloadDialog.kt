package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot.ChatBotDialog
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsOutlinedButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsPrimaryButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTextField
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation.Screen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class CertificateDownloadUiState(
    val ackNo: String = "",
    val ackNoError: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    // Set once a download completes; the dialog persists it to disk, opens it,
    // then calls clearPendingCertificate() to reset this.
    val pendingCertificateBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}

class CertificateDownloadViewModel(private val repository: MahilaShetkariRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificateDownloadUiState())
    val uiState: StateFlow<CertificateDownloadUiState> = _uiState

    fun onAckNoChange(value: String) = _uiState.update { it.copy(ackNo = value.uppercase(), ackNoError = null, error = null) }

    fun submit(ackNoRequiredError: String) {
        val ackNo = _uiState.value.ackNo.trim()
        if (ackNo.isBlank()) {
            _uiState.update { it.copy(ackNoError = ackNoRequiredError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            when (val result = repository.downloadCertificate(ackNo)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(loading = false, pendingCertificateBytes = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(loading = false, error = result.message)
                }
            }
        }
    }

    fun clearPendingCertificate() = _uiState.update { it.copy(pendingCertificateBytes = null) }
}

@Composable
fun CertificateDownloadDialog(repository: MahilaShetkariRepository, lang: AppLanguage, onDismiss: () -> Unit) {
    val viewModel = rememberVm(repository) { CertificateDownloadViewModel(it) }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val strings = Strings.certificateDialog(lang)

    // Once a certificate download lands in state, write it to disk and open
    // it with the system PDF viewer, then dismiss the dialog.
    LaunchedEffect(state.pendingCertificateBytes) {
        val bytes = state.pendingCertificateBytes ?: return@LaunchedEffect
        try {
            val dir = File(context.getExternalFilesDir(null), "certificates").apply { mkdirs() }
            val ackNo = state.ackNo.trim().replace("/", "-").ifBlank { "certificate" }
            val file = File(dir, "WFC_$ackNo.pdf")
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(intent) }
                .onFailure { Toast.makeText(context, "Certificate saved to ${file.path}", Toast.LENGTH_LONG).show() }
        } finally {
            viewModel.clearPendingCertificate()
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(strings.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    strings.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                MsTextField(
                    label = "Acknowledgment number",
                    value = state.ackNo,
                    onValueChange = viewModel::onAckNoChange,
                    error = state.ackNoError,
                    supportingText = "e.g. MSP-3F8A1B2C",
                    voiceInputEnabled = true
                )

                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MsOutlinedButton(
                        text = strings.cancel,
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                        onClick = onDismiss
                    )
                    MsPrimaryButton(
                        text = strings.submit,
                        modifier = Modifier.weight(1f),
                        loading = state.loading,
                        onClick = { viewModel.submit(strings.ackNoError) }
                    )
                }
            }
        }
    }
}
