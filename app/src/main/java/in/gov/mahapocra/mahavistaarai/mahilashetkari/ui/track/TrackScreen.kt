package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.track

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.*
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsDropdown
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsPrimaryButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTextField
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.StatusStepper
import java.io.File

@Composable
fun TrackScreen(repository: MahilaShetkariRepository, lang: AppLanguage) {
    val viewModel = rememberVm(repository) { TrackViewModel(it) }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val strings = Strings.track(lang)

    // Once a certificate download lands in state, write it to disk and open
    // it with the system PDF viewer — then clear it so it only fires once.
    LaunchedEffect(state.pendingCertificateBytes) {
        val bytes = state.pendingCertificateBytes ?: return@LaunchedEffect
        try {
            val dir = File(context.getExternalFilesDir(null), "certificates").apply { mkdirs() }
            val ackNo = state.result?.acknowledgmentNo?.replace("/", "-") ?: "certificate"
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
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(strings.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            strings.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ModeSwitch(
                    state.mode,
                    strings,
                    viewModel::setMode
                )
                Spacer(Modifier.height(16.dp))

                if (state.mode == TrackMode.BY_ACK) {
                    MsTextField(
                        label = "Acknowledgment number",
                        value = state.ackNo,
                        onValueChange = viewModel::onAckNoChange,
                        error = state.ackNoError,
                        supportingText = "e.g. MSP-3F8A1B2C",
                        voiceInputEnabled = true
                    )
                } else {
                    MsTextField(
                        label = "Applicant name",
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        error = state.nameError,
                        voiceInputEnabled = true
                    )
                    Spacer(Modifier.height(12.dp))
                    MsDropdown(
                        label = "District",
                        options = state.districts,
                        selected = state.selectedDistrict,
                        onSelected = viewModel::onDistrictSelected,
                        enabled = state.districts.isNotEmpty()
                    )
                    Spacer(Modifier.height(12.dp))
                    MsDropdown(
                        label = "Taluka",
                        options = state.talukas,
                        selected = state.selectedTaluka,
                        onSelected = viewModel::onTalukaSelected,
                        enabled = state.talukas.isNotEmpty()
                    )
                    Spacer(Modifier.height(12.dp))
                    MsDropdown(
                        label = "Village",
                        options = state.villages,
                        selected = state.selectedVillage,
                        onSelected = viewModel::onVillageSelected,
                        enabled = state.villages.isNotEmpty()
                    )
                    state.villageError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(Modifier.height(20.dp))
                MsPrimaryButton(
                    text = strings.checkStatus,
                    loading = state.searchLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::search
                )

                state.searchError?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        state.result?.let { result ->
            Spacer(Modifier.height(28.dp))
            ResultCard(
                result = result,
                strings = strings,
                certificateLoading = state.certificateLoading,
                certificateError = state.certificateError,
                onDownloadCertificate = viewModel::downloadCertificate
            )
        }
    }
}

@Composable
private fun ModeSwitch(mode: TrackMode, strings: Strings.Track, onModeChange: (TrackMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        ModeOption(strings.byAck, mode == TrackMode.BY_ACK, Modifier.weight(1f)) { onModeChange(
            TrackMode.BY_ACK) }
        ModeOption(strings.byNameVillage, mode == TrackMode.BY_NAME_VILLAGE, Modifier.weight(1f)) { onModeChange(
            TrackMode.BY_NAME_VILLAGE) }
    }
}

@Composable
private fun ModeOption(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        onClick = onClick
    ) {
        Text(
            label,
            textAlign = TextAlign.Center,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun ResultCard(
    result: ApplicationStatusDto,
    strings: Strings.Track,
    certificateLoading: Boolean,
    certificateError: String?,
    onDownloadCertificate: () -> Unit
) {
    val isRejected = result.status == "rejected"
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(result.applicantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(result.acknowledgmentNo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))

            StatusStepper(statusStep = result.statusStep, isRejected = isRejected)

            Spacer(Modifier.height(20.dp))
            InfoRow("Status", result.statusDisplay)
            listOfNotNull(result.districtName, result.talukaName, result.villageName)
                .takeIf { it.isNotEmpty() }
                ?.let { InfoRow("Location", it.joinToString(", ")) }
            InfoRow("Mobile", result.mobile)
            result.submittedAt?.let { InfoRow("Submitted", it.take(10)) }
            result.reviewedAt?.let { InfoRow("Reviewed", it.take(10)) }

            if (isRejected && !result.rejectionReason.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        result.rejectionReason,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (result.status == "approved" && result.hasCertificate) {
                Spacer(Modifier.height(20.dp))
                MsPrimaryButton(
                    text = strings.downloadCertificate,
                    loading = certificateLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDownloadCertificate
                )
            } else if (result.status == "approved" && !result.hasCertificate) {
                Spacer(Modifier.height(12.dp))
                Text(
                    strings.certificateGenerating,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            certificateError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
