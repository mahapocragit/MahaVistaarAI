package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackViewModel(private val repository: MahilaShetkariRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState

    init {
        loadDistricts()
    }

    fun setMode(mode: TrackMode) = _uiState.update { it.copy(mode = mode, searchError = null, result = null) }

    fun onAckNoChange(value: String) = _uiState.update { it.copy(ackNo = value.uppercase(), ackNoError = null, searchError = null) }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null, searchError = null) }

    private fun loadDistricts() {
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getDistricts()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(geographyLoading = false, districts = result.data.map { d -> DropdownOption(d.id, d.districtName) })
                }
                is ApiResult.Error -> _uiState.update { it.copy(geographyLoading = false) }
            }
        }
    }

    fun onDistrictSelected(option: DropdownOption) {
        _uiState.update {
            it.copy(selectedDistrict = option, selectedTaluka = null, selectedVillage = null, talukas = emptyList(), villages = emptyList())
        }
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getTalukas(option.id)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(geographyLoading = false, talukas = result.data.map { t -> DropdownOption(t.id, t.talukaName) })
                }
                is ApiResult.Error -> _uiState.update { it.copy(geographyLoading = false, searchError = result.message) }
            }
        }
    }

    fun onTalukaSelected(option: DropdownOption) {
        _uiState.update { it.copy(selectedTaluka = option, selectedVillage = null, villages = emptyList()) }
        viewModelScope.launch {
            _uiState.update { it.copy(geographyLoading = true) }
            when (val result = repository.getVillages(option.id)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(geographyLoading = false, villages = result.data.map { v -> DropdownOption(v.id, v.villageName) })
                }
                is ApiResult.Error -> _uiState.update { it.copy(geographyLoading = false, searchError = result.message) }
            }
        }
    }

    fun onVillageSelected(option: DropdownOption) = _uiState.update { it.copy(selectedVillage = option, villageError = null) }

    fun search() {
        val state = _uiState.value
        if (state.mode == TrackMode.BY_ACK) {
            if (state.ackNo.isBlank()) {
                _uiState.update { it.copy(ackNoError = "Enter your acknowledgment number.") }
                return
            }
        } else {
            var nameErr: String? = null
            var villageErr: String? = null
            if (state.name.isBlank()) nameErr = "Enter the applicant's name."
            if (state.selectedVillage == null) villageErr = "Select a village."
            if (nameErr != null || villageErr != null) {
                _uiState.update { it.copy(nameError = nameErr, villageError = villageErr) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(searchLoading = true, searchError = null, result = null) }
            val result = if (state.mode == TrackMode.BY_ACK) {
                repository.getStatusByAck(state.ackNo.trim())
            } else {
                repository.getStatusByNameVillage(state.name.trim(), state.selectedVillage!!.id)
            }
            when (result) {
                is ApiResult.Success -> _uiState.update { it.copy(searchLoading = false, result = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(searchLoading = false, searchError = result.message) }
            }
        }
    }

    fun downloadCertificate() {
        val ackNo = _uiState.value.result?.acknowledgmentNo ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(certificateLoading = true, certificateError = null) }
            when (val result = repository.downloadCertificate(ackNo)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(certificateLoading = false, pendingCertificateBytes = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(certificateLoading = false, certificateError = result.message)
                }
            }
        }
    }

    fun clearPendingCertificate() = _uiState.update { it.copy(pendingCertificateBytes = null) }
}
