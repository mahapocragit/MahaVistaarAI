package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApplyViewModel(private val repository: MahilaShetkariRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplyUiState())
    val uiState: StateFlow<ApplyUiState> = _uiState

    // ---------- Step 1: Aadhaar ----------

    fun onAadhaarChange(value: String) {
        if (value.length <= 12 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(aadhaar = value, aadhaarError = null, generalError = null) }
        }
    }

    fun sendOtp() {
        val aadhaar = _uiState.value.aadhaar
        val error = Validators.aadhaarError(aadhaar)
        if (error != null) {
            _uiState.update { it.copy(aadhaarError = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(sendOtpLoading = true, generalError = null) }
            when (val result = repository.sendOtp(aadhaar)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(sendOtpLoading = false, txn = result.data.txn, step = ApplyStep.OTP)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(sendOtpLoading = false, generalError = result.message)
                }
            }
        }
    }

    // ---------- Step 2: OTP ----------

    fun onOtpChange(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(otp = value, otpError = null, generalError = null) }
        }
    }

    fun resendOtp() = sendOtp()

    fun changeAadhaarNumber() {
        _uiState.update { it.copy(step = ApplyStep.AADHAAR, otp = "", txn = null, generalError = null) }
    }

    fun verifyOtp() {
        val state = _uiState.value
        val error = Validators.otpError(state.otp)
        if (error != null) {
            _uiState.update { it.copy(otpError = error) }
            return
        }
        val txn = state.txn ?: run {
            _uiState.update { it.copy(generalError = "OTP session expired. Please resend the OTP.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(verifyOtpLoading = true, generalError = null) }
            when (val result = repository.verifyOtp(state.aadhaar, txn, state.otp)) {
                is ApiResult.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            verifyOtpLoading = false,
                            step = ApplyStep.DETAILS,
                            applicantName = data.name,
                            dob = data.dob,
                            age = data.age,
                            gender = data.gender,
                            permanentAddress = data.address
                        )
                    }
                    loadDistricts()
                    loadWorkTypes()
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(verifyOtpLoading = false, generalError = result.message)
                }
            }
        }
    }

    // ---------- Step 3: Details ----------

    fun onNameChange(value: String) = _uiState.update { it.copy(applicantName = value, nameError = null) }

    fun onMobileChange(value: String) {
        if (value.length <= 10 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(mobile = value, mobileError = null) }
        }
    }

    fun onCurrentAddressChange(value: String) = _uiState.update { it.copy(currentAddress = value) }

    private fun loadDistricts() {
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getDistricts()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        geographyLoading = false,
                        districts = result.data.map { d -> DropdownOption(d.id, d.districtName) }
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(geographyLoading = false, generalError = result.message)
                }
            }
        }
    }

    fun onDistrictSelected(option: DropdownOption) {
        _uiState.update {
            it.copy(
                selectedDistrict = option,
                selectedTaluka = null,
                selectedVillage = null,
                talukas = emptyList(),
                villages = emptyList()
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getTalukas(option.id)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        geographyLoading = false,
                        talukas = result.data.map { t -> DropdownOption(t.id, t.talukaName) }
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(geographyLoading = false, generalError = result.message)
                }
            }
        }
    }

    fun onTalukaSelected(option: DropdownOption) {
        _uiState.update { it.copy(selectedTaluka = option, selectedVillage = null, villages = emptyList()) }
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getVillages(option.id)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        geographyLoading = false,
                        villages = result.data.map { v -> DropdownOption(v.id, v.villageName) }
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(geographyLoading = false, generalError = result.message)
                }
            }
        }
    }

    fun onVillageSelected(option: DropdownOption) = _uiState.update { it.copy(selectedVillage = option) }

    private fun loadWorkTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(workTypesLoading = true) }
            when (val result = repository.getWorkTypes()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(workTypesLoading = false, workTypes = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(workTypesLoading = false, generalError = result.message)
                }
            }
        }
    }

    fun toggleWorkType(id: Int) {
        _uiState.update {
            val updated = if (id in it.selectedWorkTypeIds) it.selectedWorkTypeIds - id else it.selectedWorkTypeIds + id
            it.copy(selectedWorkTypeIds = updated, workTypesError = null)
        }
    }

    fun onDeclarationToggle(checked: Boolean) = _uiState.update { it.copy(declarationAccepted = checked, declarationError = null) }

    fun submitApplication() {
        val state = _uiState.value

        val nameError = Validators.nameError(state.applicantName)
        val mobileError = Validators.mobileError(state.mobile)
        val workTypesError = Validators.workTypesError(state.selectedWorkTypeIds.toList())
        val declarationError = Validators.declarationError(state.declarationAccepted)

        if (listOf(nameError, mobileError, workTypesError, declarationError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    mobileError = mobileError,
                    workTypesError = workTypesError,
                    declarationError = declarationError
                )
            }
            return
        }

        val request = ApplicationRequest(
            applicantName = state.applicantName.trim(),
            aadhaarNo = state.aadhaar,
            gender = state.gender.ifBlank { null },
            mobile = state.mobile,
            permanentAddress = state.permanentAddress.ifBlank { null },
            currentAddress = state.currentAddress.ifBlank { null },
            dob = state.dob.ifBlank { null },
            age = state.age,
            district = state.selectedDistrict?.id,
            taluka = state.selectedTaluka?.id,
            village = state.selectedVillage?.id,
            workTypes = state.selectedWorkTypeIds.toList(),
            declaration = state.declarationAccepted
        )

        viewModelScope.launch {
            _uiState.update { it.copy(submitLoading = true, generalError = null) }
            when (val result = repository.submitApplication(request)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        submitLoading = false,
                        step = ApplyStep.SUCCESS,
                        acknowledgmentNo = result.data.acknowledgmentNo
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(submitLoading = false, generalError = result.message)
                }
            }
        }
    }

    fun startOver() {
        _uiState.value = ApplyUiState()
    }
}
