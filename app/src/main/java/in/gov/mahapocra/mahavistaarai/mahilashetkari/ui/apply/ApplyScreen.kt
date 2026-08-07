package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.apply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.*
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsDropdown
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsOutlinedButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsPrimaryButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTextField

@Composable
fun ApplyScreen(repository: MahilaShetkariRepository, lang: AppLanguage) {
    val viewModel = rememberVm(repository) { ApplyViewModel(it) }
    val state by viewModel.uiState.collectAsState()
    val strings = Strings.apply(lang)

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

        if (state.step != ApplyStep.SUCCESS) {
            StepProgress(activeIndex = stepIndex(state.step))
            Spacer(Modifier.height(20.dp))
        }

        state.generalError?.let { ErrorBanner(it) }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                when (state.step) {
                    ApplyStep.AADHAAR -> AadhaarStep(state, viewModel, strings)
                    ApplyStep.OTP -> OtpStep(state, viewModel, strings)
                    ApplyStep.DETAILS -> DetailsStep(state, viewModel, strings)
                    ApplyStep.SUCCESS -> SuccessStep(state, viewModel, strings)
                }
            }
        }
    }
}

private fun stepIndex(step: ApplyStep) = when (step) {
    ApplyStep.AADHAAR, ApplyStep.OTP -> 0
    ApplyStep.DETAILS -> 1
    ApplyStep.SUCCESS -> 2
}

@Composable
private fun StepProgress(activeIndex: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { index ->
            val active = index <= activeIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            )
            if (index < 2) Spacer(Modifier.width(6.dp))
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun AadhaarStep(state: ApplyUiState, vm: ApplyViewModel, strings: Strings.Apply) {
    Text(strings.step1Title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    Text(
        strings.step1Body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
    MsTextField(
        label = "Aadhaar number",
        value = state.aadhaar,
        onValueChange = vm::onAadhaarChange,
        error = state.aadhaarError,
        keyboardType = KeyboardType.Number,
        supportingText = "12-digit number, numbers only",
        voiceInputEnabled = true
    )
    Spacer(Modifier.height(20.dp))
    MsPrimaryButton(
        text = strings.sendOtp,
        loading = state.sendOtpLoading,
        modifier = Modifier.fillMaxWidth(),
        onClick = vm::sendOtp
    )
}

@Composable
private fun OtpStep(state: ApplyUiState, vm: ApplyViewModel, strings: Strings.Apply) {
    Text(strings.step1OtpTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    Text(
        strings.otpBody.format(state.aadhaar.takeLast(4)),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
    MsTextField(
        label = "6-digit OTP",
        value = state.otp,
        onValueChange = vm::onOtpChange,
        error = state.otpError,
        keyboardType = KeyboardType.NumberPassword
    )
    Spacer(Modifier.height(20.dp))
    MsPrimaryButton(
        text = strings.verifyContinue,
        loading = state.verifyOtpLoading,
        modifier = Modifier.fillMaxWidth(),
        onClick = vm::verifyOtp
    )
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = vm::resendOtp) { Text(strings.resendOtp) }
        TextButton(onClick = vm::changeAadhaarNumber) { Text(strings.changeAadhaar) }
    }
}

@Composable
private fun DetailsStep(state: ApplyUiState, vm: ApplyViewModel, strings: Strings.Apply) {
    Text(strings.step2Title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
    Text(
        strings.step2Body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))

    MsTextField(
        label = "Full name",
        value = state.applicantName,
        onValueChange = vm::onNameChange,
        error = state.nameError,
        voiceInputEnabled = true
    )
    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MsTextField(
            label = "Date of birth",
            value = state.dob,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.weight(1f)
        )
        MsTextField(
            label = "Age",
            value = state.age?.toString() ?: "",
            onValueChange = {},
            enabled = false,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(Modifier.height(12.dp))

    MsTextField(
        label = "Permanent address",
        value = state.permanentAddress,
        onValueChange = {},
        enabled = false,
        singleLine = false
    )
    Spacer(Modifier.height(12.dp))

    MsTextField(
        label = "Mobile number",
        value = state.mobile,
        onValueChange = vm::onMobileChange,
        error = state.mobileError,
        keyboardType = KeyboardType.Phone,
        supportingText = "For SMS updates on your application",
        voiceInputEnabled = true
    )
    Spacer(Modifier.height(12.dp))

    MsTextField(
        label = "Current address (optional)",
        value = state.currentAddress,
        onValueChange = vm::onCurrentAddressChange,
        singleLine = false,
        voiceInputEnabled = true
    )
    Spacer(Modifier.height(20.dp))

    Text(strings.locationTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    MsDropdown(
        label = "District",
        options = state.districts,
        selected = state.selectedDistrict,
        onSelected = vm::onDistrictSelected,
        enabled = state.districts.isNotEmpty()
    )
    Spacer(Modifier.height(12.dp))
    MsDropdown(
        label = "Taluka",
        options = state.talukas,
        selected = state.selectedTaluka,
        onSelected = vm::onTalukaSelected,
        enabled = state.talukas.isNotEmpty()
    )
    Spacer(Modifier.height(12.dp))
    MsDropdown(
        label = "Village",
        options = state.villages,
        selected = state.selectedVillage,
        onSelected = vm::onVillageSelected,
        enabled = state.villages.isNotEmpty()
    )
    if (state.geographyLoading) {
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }

    Spacer(Modifier.height(20.dp))
    Text(strings.workTypeTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
    Text(
        strings.workTypeSubtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(8.dp))
    if (state.workTypesLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {
        state.workTypes.forEach { workType ->
            val checked = workType.id in state.selectedWorkTypeIds
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = checked, onValueChange = { vm.toggleWorkType(workType.id) })
            ) {
                Checkbox(checked = checked, onCheckedChange = { vm.toggleWorkType(workType.id) })
                Text(workType.name, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    state.workTypesError?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
    }

    Spacer(Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = state.declarationAccepted, onValueChange = vm::onDeclarationToggle)
    ) {
        Checkbox(checked = state.declarationAccepted, onCheckedChange = vm::onDeclarationToggle)
        Text(
            strings.declaration,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    state.declarationError?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
    }

    Spacer(Modifier.height(24.dp))
    MsPrimaryButton(
        text = strings.submit,
        loading = state.submitLoading,
        modifier = Modifier.fillMaxWidth(),
        onClick = vm::submitApplication
    )
}

@Composable
private fun SuccessStep(state: ApplyUiState, vm: ApplyViewModel, strings: Strings.Apply) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(strings.successTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            strings.successBody,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                state.acknowledgmentNo ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(24.dp))
        MsOutlinedButton(
            text = strings.submitAnother,
            modifier = Modifier.fillMaxWidth(),
            onClick = vm::startOver
        )
    }
}
