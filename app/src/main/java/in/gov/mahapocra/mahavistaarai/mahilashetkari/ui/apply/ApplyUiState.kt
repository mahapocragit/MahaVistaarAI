package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.apply

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.WorkTypeDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption

enum class ApplyStep { AADHAAR, OTP, DETAILS, SUCCESS }

data class ApplyUiState(
    val step: ApplyStep = ApplyStep.AADHAAR,

    // Step 1 — Aadhaar
    val aadhaar: String = "",
    val aadhaarError: String? = null,
    val sendOtpLoading: Boolean = false,

    // Step 2 — OTP
    val otp: String = "",
    val otpError: String? = null,
    val txn: String? = null,
    val verifyOtpLoading: Boolean = false,

    // Prefilled from Aadhaar verify
    val applicantName: String = "",
    val nameError: String? = null,
    val dob: String = "",
    val age: Int? = null,
    val gender: String = "",
    val permanentAddress: String = "",

    // Step 3 — Details
    val mobile: String = "",
    val mobileError: String? = null,
    val currentAddress: String = "",

    val districts: List<DropdownOption> = emptyList(),
    val talukas: List<DropdownOption> = emptyList(),
    val villages: List<DropdownOption> = emptyList(),
    val selectedDistrict: DropdownOption? = null,
    val selectedTaluka: DropdownOption? = null,
    val selectedVillage: DropdownOption? = null,
    val geographyLoading: Boolean = false,

    val workTypes: List<WorkTypeDto> = emptyList(),
    val selectedWorkTypeIds: Set<Int> = emptySet(),
    val workTypesError: String? = null,
    val workTypesLoading: Boolean = false,

    val declarationAccepted: Boolean = false,
    val declarationError: String? = null,

    val submitLoading: Boolean = false,

    // Shown as a banner on whichever step it happened on
    val generalError: String? = null,

    val acknowledgmentNo: String? = null
)
