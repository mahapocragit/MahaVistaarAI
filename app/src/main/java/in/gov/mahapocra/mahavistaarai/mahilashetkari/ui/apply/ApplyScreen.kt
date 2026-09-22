@file:OptIn(ExperimentalFoundationApi::class)
package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.apply

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.WorkTypeDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsDropdown
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsOutlinedButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsPrimaryButton
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.MsTextField
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.rememberVm
import kotlin.compareTo
import kotlin.ranges.step
import kotlin.text.chunked
import kotlin.text.forEach
import kotlin.toString

@Composable
fun ApplyScreen(repository: MahilaShetkariRepository, lang: AppLanguage) {
    val viewModel = rememberVm(repository) { ApplyViewModel(it) }
    val state by viewModel.uiState.collectAsState()
    val strings = Strings.apply(lang)

    val generalErrorRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(state.generalError) {
        if (state.generalError != null) generalErrorRequester.bringIntoView()
    }

    if (state.femaleOnlyDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissFemaleOnlyDialog,
            title = { Text(strings.femaleOnlyTitle) },
            text = { Text(strings.femaleOnlyMessage) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissFemaleOnlyDialog) { Text(strings.ok) }
            }
        )
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

        if (state.step != ApplyStep.SUCCESS) {
            StepProgress(activeIndex = stepIndex(state.step))
            Spacer(Modifier.height(20.dp))
        }

        state.generalError?.let {
            ErrorBanner(it, modifier = Modifier.bringIntoViewRequester(generalErrorRequester))
        }

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
private fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
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
        text = if (!state.sendOtpLoading && state.resendCooldownSeconds > 0)
            strings.sendOtpIn.format(state.resendCooldownSeconds)
        else
            strings.sendOtp,
        enabled = state.resendCooldownSeconds == 0,
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
        TextButton(onClick = vm::resendOtp, enabled = state.resendCooldownSeconds == 0) {
            Text(
                if (state.resendCooldownSeconds > 0)
                    strings.resendOtpIn.format(state.resendCooldownSeconds)
                else
                    strings.resendOtp
            )
        }
        TextButton(onClick = vm::changeAadhaarNumber) { Text(strings.changeAadhaar) }
    }
}

@Composable
private fun DetailsStep(state: ApplyUiState, vm: ApplyViewModel, strings: Strings.Apply) {
    val mobileFieldRequester = remember { BringIntoViewRequester() }
    val workTypesRequester = remember { BringIntoViewRequester() }
    val familyFarmerIdRequester = remember { BringIntoViewRequester() }
    val declarationRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(
        state.mobileError,
        state.workTypesError,
        state.familyFarmerIdAnswerError,
        state.familyFarmerIdError,
        state.declarationError
    ) {
        when {
            state.mobileError != null -> mobileFieldRequester.bringIntoView()
            state.workTypesError != null -> workTypesRequester.bringIntoView()
            state.familyFarmerIdAnswerError != null -> familyFarmerIdRequester.bringIntoView()
            state.familyFarmerIdError != null -> familyFarmerIdRequester.bringIntoView()
            state.declarationError != null -> declarationRequester.bringIntoView()
        }
    }

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
        onValueChange = {},
        enabled = false
    )
    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MsTextField(label = "Date of birth", value = state.dob, onValueChange = {}, enabled = false, modifier = Modifier.weight(1f))
        MsTextField(label = "Age", value = state.age?.toString() ?: "", onValueChange = {}, enabled = false, modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))

    MsTextField(label = "Permanent address", value = state.permanentAddress, onValueChange = {}, enabled = false, singleLine = false)
    Spacer(Modifier.height(12.dp))

    MsDropdown(
        label = "Caste category (optional)",
        options = state.casteCategories,
        selected = state.selectedCasteCategory,
        onSelected = vm::onCasteCategorySelected,
        enabled = state.casteCategories.isNotEmpty()
    )
    if (state.casteCategoriesLoading) {
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    Spacer(Modifier.height(12.dp))

    MsTextField(
        label = "Mobile number",
        value = state.mobile,
        onValueChange = vm::onMobileChange,
        error = state.mobileError,
        keyboardType = KeyboardType.Phone,
        supportingText = "For SMS updates on your application",
        voiceInputEnabled = true,
        modifier = Modifier.bringIntoViewRequester(mobileFieldRequester)
    )
    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = state.currentAddressSameAsPermanent,
                onValueChange = vm::onCurrentAddressSameAsPermanentToggle
            )
    ) {
        Checkbox(
            checked = state.currentAddressSameAsPermanent,
            onCheckedChange = vm::onCurrentAddressSameAsPermanentToggle
        )
        Text("Current address same as permanent address", style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(Modifier.height(12.dp))

    MsTextField(
        label = "Current address (optional)",
        value = state.currentAddress,
        onValueChange = vm::onCurrentAddressChange,
        enabled = !state.currentAddressSameAsPermanent,
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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.bringIntoViewRequester(workTypesRequester)
        ) {
            state.workTypes.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { workType ->
                        WorkTypeGridItem(
                            workType = workType,
                            checked = workType.id in state.selectedWorkTypeIds,
                            onToggle = { vm.toggleWorkType(workType.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Pad the last, partially-filled row so items keep a consistent width.
                    repeat(2 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
    state.workTypesError?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
    }

    Spacer(Modifier.height(20.dp))
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(familyFarmerIdRequester)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                strings.familyFarmerIdQuestion + " *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            ) {
                YesNoOption(
                    label = strings.yes,
                    selected = state.hasFamilyFarmerId == true,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onFamilyFarmerIdAnswerChange(true) }
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(44.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                YesNoOption(
                    label = strings.no,
                    selected = state.hasFamilyFarmerId == false,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onFamilyFarmerIdAnswerChange(false) }
                )
            }
            state.familyFarmerIdAnswerError?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }
            if (state.hasFamilyFarmerId == true) {
                Spacer(Modifier.height(12.dp))
                MsTextField(
                    label = strings.familyFarmerIdLabel,
                    value = state.familyFarmerId,
                    onValueChange = vm::onFamilyFarmerIdChange,
                    error = state.familyFarmerIdError,
                    enabled = !state.familyFarmerIdVerified,
                    keyboardType = KeyboardType.Number,
                    supportingText = "11-digit number, numbers only"
                )
                Spacer(Modifier.height(8.dp))
                if (state.familyFarmerIdVerified) {
                    Text(
                        "Verified: ${state.familyFarmerName}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                } else {
                    MsPrimaryButton(
                        text = "Verify Farmer ID",
                        enabled = state.familyFarmerId.length == 11,
                        loading = state.familyFarmerIdVerifying,
                        onClick = vm::verifyFamilyFarmerId
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(declarationRequester)
            .toggleable(value = state.declarationAccepted, onValueChange = vm::onDeclarationToggle)
    ) {
        Checkbox(checked = state.declarationAccepted, onCheckedChange = vm::onDeclarationToggle)
        Text(
            strings.declaration.format(
                state.applicantName,
                state.dob,
                state.selectedVillage?.label.orEmpty(),
                state.selectedTaluka?.label.orEmpty(),
                state.selectedDistrict?.label.orEmpty(),
                state.mobile,
                state.aadhaar
            ),
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Spacer(Modifier.height(24.dp))
        MsOutlinedButton(text = strings.submitAnother, modifier = Modifier.fillMaxWidth(), onClick = vm::startOver)
    }
}

@Composable
private fun YesNoOption(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun WorkTypeGridItem(
    workType: WorkTypeDto,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (checked) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = modifier
            .toggleable(value = checked, onValueChange = { onToggle() })
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (workType.imageUrl != null) {
                        AsyncImage(
                            model = workType.imageUrl,
                            contentDescription = workType.nameMr,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(9.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    workType.nameMr,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2
                )
            }
            if (checked) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
    }
}
