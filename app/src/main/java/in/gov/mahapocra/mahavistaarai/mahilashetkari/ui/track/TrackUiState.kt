package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.track

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption

enum class TrackMode { BY_ACK, BY_NAME_VILLAGE }

data class TrackUiState(
    val mode: TrackMode = TrackMode.BY_ACK,

    val ackNo: String = "",
    val ackNoError: String? = null,

    val name: String = "",
    val nameError: String? = null,

    val districts: List<DropdownOption> = emptyList(),
    val talukas: List<DropdownOption> = emptyList(),
    val villages: List<DropdownOption> = emptyList(),
    val selectedDistrict: DropdownOption? = null,
    val selectedTaluka: DropdownOption? = null,
    val selectedVillage: DropdownOption? = null,
    val villageError: String? = null,
    val geographyLoading: Boolean = false,

    val searchLoading: Boolean = false,
    val searchError: String? = null,
    val result: ApplicationStatusDto? = null,

    val certificateLoading: Boolean = false,
    val certificateError: String? = null,
    // Set once a download completes; the screen persists it to disk, opens it,
    // then calls clearPendingCertificate() to reset this.
    val pendingCertificateBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}
