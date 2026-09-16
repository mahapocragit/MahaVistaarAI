package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsDropdown
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsPrimaryButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTextField
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.StatusStepper
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm

@Composable
fun TrackScreen(repository: MahilaShetkariRepository, lang: AppLanguage, initialAckNo: String? = null) {
    val viewModel = rememberVm(repository) { TrackViewModel(it) }
    val state by viewModel.uiState.collectAsState()
    val strings = Strings.track(lang)

    // Coming from the chatbot with an ack. no. already in hand — fill it in
    // instead of making the user retype it.
    LaunchedEffect(initialAckNo) {
        if (!initialAckNo.isNullOrBlank()) viewModel.prefillAckNo(initialAckNo)
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
                ModeSwitch(state.mode, strings, viewModel::setMode)
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
            ResultCard(result = result)
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
        ModeOption(strings.byAck, mode == TrackMode.BY_ACK, Modifier.weight(1f)) { onModeChange(TrackMode.BY_ACK) }
        ModeOption(strings.byNameVillage, mode == TrackMode.BY_NAME_VILLAGE, Modifier.weight(1f)) { onModeChange(TrackMode.BY_NAME_VILLAGE) }
    }
}

@Composable
private fun ModeOption(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = if (selected) MaterialTheme.colorScheme.surface else androidx.compose.ui.graphics.Color.Transparent,
        onClick = onClick
    ) {
        Text(
            label,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun ResultCard(result: ApplicationStatusDto) {
    val isRejected = result.status.contains("reject")
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

            StatusStepper(statusStep = result.statusStep, status = result.status)

            Spacer(Modifier.height(20.dp))
            InfoRow("Status", result.statusDisplay)
            listOfNotNull(result.districtName, result.talukaName, result.villageName)
                .takeIf { it.isNotEmpty() }
                ?.let { InfoRow("Location", it.joinToString(", ")) }
            InfoRow("Mobile", result.mobile)
            result.submittedAt?.let { InfoRow("Submitted", it.take(10)) }
            result.reviewedAt?.let { InfoRow("Reviewed", it.take(10)) }
            result.workTypes?.takeIf { it.isNotEmpty() }
                ?.let { InfoRow("Work types", it.joinToString(", ") { wt -> wt.name }) }
            InfoRow("Has land", if (result.hasLand) "Yes" else "No")
            InfoRow("Certificate", if (result.hasCertificate) "Issued" else "Not issued yet")

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
