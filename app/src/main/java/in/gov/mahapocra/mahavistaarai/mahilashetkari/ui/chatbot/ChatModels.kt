package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot

import androidx.compose.ui.text.input.KeyboardType
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.WorkTypeDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

data class QuickReply(
    val label: String,
    val value: String
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val quickReplies: List<QuickReply> = emptyList(),
    val inputEnabled: Boolean = false,
    val busy: Boolean = false,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val ended: Boolean = false,
    val requestNavigateRoute: String? = null,
    val requestNavigateAckNo: String? = null,
    val femaleOnlyDialog: Boolean = false
)

/**
 * Snapshot of everything needed to silently resume a chat in progress —
 * the current step plus the scratch data collected so far — persisted
 * alongside the transcript so reopening the dialog doesn't replay the
 * greeting or lose quick-reply context (e.g. the fetched district list).
 */
data class ChatSessionState(
    val step: String,
    val quickReplies: List<QuickReply> = emptyList(),
    val inputEnabled: Boolean = false,
    val keyboardType: String = "Text",
    val ended: Boolean = false,
    val aadhaar: String = "",
    val txn: String? = null,
    val otp: String = "",
    val applicantName: String = "",
    val applicantNameMr: String = "",
    val photoUrl: String = "",
    val dob: String = "",
    val age: Int? = null,
    val gender: String = "",
    val permanentAddress: String = "",
    val mobile: String = "",
    val currentAddress: String = "",
    val districts: List<DropdownOption> = emptyList(),
    val talukas: List<DropdownOption> = emptyList(),
    val villages: List<DropdownOption> = emptyList(),
    val selectedDistrict: DropdownOption? = null,
    val selectedTaluka: DropdownOption? = null,
    val selectedVillage: DropdownOption? = null,
    val workTypes: List<WorkTypeDto> = emptyList(),
    val selectedWorkTypeIds: Set<Int> = emptySet(),
    val casteCategories: List<DropdownOption> = emptyList(),
    val selectedCasteCategory: DropdownOption? = null,
    val hasFamilyFarmerId: Boolean? = null,
    val familyFarmerId: String = "",
    val familyFarmerName: String = "",
    val ackNo: String = "",
    val trackName: String = ""
)
