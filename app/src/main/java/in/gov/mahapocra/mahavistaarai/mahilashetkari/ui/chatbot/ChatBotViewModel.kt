package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot

import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.WorkTypeDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components.DropdownOption
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation.Screen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.plus

/**
 * Drives a scripted conversation that mirrors the Apply/Track screens step
 * by step, so it can reuse the same repository calls and validation rules.
 * Selection-style answers (district/taluka/village/work type/yes-no) are
 * offered as quick-reply chips but can also be typed or spoken — matched
 * loosely against the option labels — since voice input can't reliably
 * tap a chip.
 */
class ChatBotViewModel(
    private val repository: MahilaShetkariRepository,
    private val lang: AppLanguage,
    private val historyStore: ChatHistoryStore
) : ViewModel() {

    private val t = Strings.chatBot(lang)
    private val idGen = AtomicLong(0)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private enum class Step {
        ASK_APPLY_INTENT, ASK_TRACK_INTENT,
        APPLY_AADHAAR, APPLY_OTP, APPLY_MOBILE, APPLY_CURRENT_ADDRESS,
        APPLY_DISTRICT, APPLY_TALUKA, APPLY_VILLAGE, APPLY_WORK_TYPES, APPLY_DECLARATION,
        TRACK_MODE, TRACK_ACK_NO, TRACK_NAME, TRACK_DISTRICT, TRACK_TALUKA, TRACK_VILLAGE,
        ENDED
    }

    private var step = Step.ASK_APPLY_INTENT

    // ---- Apply flow scratch data ----
    private var aadhaar = ""
    private var txn: String? = null
    private var otp = ""
    private var applicantName = ""
    private var dob = ""
    private var age: Int? = null
    private var gender = ""
    private var permanentAddress = ""
    private var mobile = ""
    private var currentAddress = ""
    private var districts: List<DropdownOption> = emptyList()
    private var talukas: List<DropdownOption> = emptyList()
    private var villages: List<DropdownOption> = emptyList()
    private var selectedDistrict: DropdownOption? = null
    private var selectedTaluka: DropdownOption? = null
    private var selectedVillage: DropdownOption? = null
    private var workTypes: List<WorkTypeDto> = emptyList()
    private var selectedWorkTypeIds: Set<Int> = emptySet()

    // ---- Track flow scratch data ----
    private var ackNo = ""
    private var trackName = ""

    init {
        viewModelScope.launch {
            val history = historyStore.load()
            if (history.isNotEmpty()) {
                idGen.set(history.maxOf { it.id })
                _uiState.update { it.copy(messages = history) }
            }
            botSay(t.greeting)
            askApplyIntent()
        }
    }

    // ---------- message helpers ----------

    private fun botSay(text: String) {
        _uiState.update { it.copy(messages = it.messages + ChatMessage(
            idGen.incrementAndGet(),
            text,
            isUser = false
        )
        ) }
        persistMessages()
    }

    private fun userSay(text: String) {
        _uiState.update { it.copy(messages = it.messages + ChatMessage(
            idGen.incrementAndGet(),
            text,
            isUser = true
        )
        ) }
        persistMessages()
    }

    private fun persistMessages() {
        val snapshot = _uiState.value.messages
        viewModelScope.launch { historyStore.save(snapshot) }
    }

    /** Clears the saved transcript and starts a brand-new conversation. */
    fun deleteAllHistory() {
        resetFlowState()
        idGen.set(0)
        _uiState.update { ChatUiState() }
        viewModelScope.launch { historyStore.clear() }
        botSay(t.greeting)
        askApplyIntent()
    }

    private fun setPrompt(quickReplies: List<QuickReply> = emptyList(), inputEnabled: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text) {
        _uiState.update {
            it.copy(quickReplies = quickReplies, inputEnabled = inputEnabled, keyboardType = keyboardType, busy = false, ended = false)
        }
    }

    private fun setBusy(busy: Boolean) {
        _uiState.update { it.copy(busy = busy, inputEnabled = false, quickReplies = emptyList()) }
    }

    private fun end() {
        step = Step.ENDED
        _uiState.update { it.copy(inputEnabled = false, quickReplies = listOf(
            QuickReply(
                t.restart,
                "restart"
            )
        ), ended = true) }
    }

    fun consumeNavigationRequest() {
        _uiState.update { it.copy(requestNavigateRoute = null) }
    }

    // ---------- entry points from the UI ----------

    fun onQuickReply(reply: QuickReply) {
        userSay(reply.label)
        handle(reply.value)
    }

    fun onTextSubmit(raw: String) {
        val text = raw.trim()
        if (text.isBlank()) return
        userSay(text)
        handle(text)
    }

    // ---------- yes/no parsing ----------

    private fun normalize(text: String) = text.trim().trim('.', '!', '?', ',').lowercase()

    private fun isYes(text: String): Boolean =
        normalize(text) in setOf("yes", "yeah", "yep", "y", "ok", "okay", "sure", "ho", "हो", "होय")

    private fun isNo(text: String): Boolean =
        normalize(text) in setOf("no", "nope", "n", "nahi", "नाही", "नको")

    private fun matchOption(text: String, options: List<DropdownOption>): DropdownOption? {
        val q = normalize(text)
        return options.firstOrNull { it.label.trim().lowercase() == q }
            ?: options.firstOrNull { it.label.trim().lowercase().contains(q) || q.contains(it.label.trim().lowercase()) }
    }

    private fun matchWorkType(text: String): WorkTypeDto? {
        val q = normalize(text)
        return workTypes.firstOrNull { it.name.trim().lowercase() == q || it.nameMr.trim() == text.trim() }
            ?: workTypes.firstOrNull { it.name.trim().lowercase().contains(q) }
    }

    // ---------- router ----------

    private fun handle(value: String) {
        when (step) {
            Step.ASK_APPLY_INTENT -> when {
                isYes(value) -> startApplyFlow()
                isNo(value) -> askTrackIntent()
                else -> { botSay(t.notUnderstood); setPrompt(yesNoReplies()) }
            }
            Step.ASK_TRACK_INTENT -> when {
                isYes(value) -> startTrackFlow()
                isNo(value) -> { botSay(t.goodbye); end() }
                else -> { botSay(t.notUnderstood); setPrompt(yesNoReplies()) }
            }
            Step.APPLY_AADHAAR -> {
                val digits = value.filter { it.isDigit() }
                val error = Validators.aadhaarError(digits)
                if (error != null) { botSay(error); setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number); return }
                aadhaar = digits
                sendOtpForChat()
            }
            Step.APPLY_OTP -> {
                val digits = value.filter { it.isDigit() }
                val error = Validators.otpError(digits)
                if (error != null) { botSay(error); setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number); return }
                otp = digits
                verifyOtpForChat()
            }
            Step.APPLY_MOBILE -> {
                val digits = value.filter { it.isDigit() }
                val error = Validators.mobileError(digits)
                if (error != null) { botSay(error); setPrompt(inputEnabled = true, keyboardType = KeyboardType.Phone); return }
                mobile = digits
                askCurrentAddress()
            }
            Step.APPLY_CURRENT_ADDRESS -> {
                currentAddress = if (normalize(value) == "skip") "" else value
                proceedToDistrict(isApply = true)
            }
            Step.APPLY_DISTRICT -> {
                val option = matchOption(value, districts)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(districts.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedDistrict = option
                selectedTaluka = null; selectedVillage = null
                proceedToTaluka(isApply = true, districtId = option.id)
            }
            Step.APPLY_TALUKA -> {
                val option = matchOption(value, talukas)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(talukas.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedTaluka = option
                selectedVillage = null
                proceedToVillage(isApply = true, talukaId = option.id)
            }
            Step.APPLY_VILLAGE -> {
                val option = matchOption(value, villages)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(villages.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedVillage = option
                proceedToWorkTypes()
            }
            Step.APPLY_WORK_TYPES -> {
                if (normalize(value) == "done") {
                    if (selectedWorkTypeIds.isEmpty()) { botSay(t.pickAtLeastOne); setPrompt(workTypeReplies()); return }
                    askDeclaration()
                    return
                }
                val wt = matchWorkType(value)
                if (wt == null) { botSay(t.notUnderstoodOption); setPrompt(workTypeReplies()); return }
                selectedWorkTypeIds = if (wt.id in selectedWorkTypeIds) selectedWorkTypeIds - wt.id else selectedWorkTypeIds + wt.id
                val chosen = workTypes.filter { it.id in selectedWorkTypeIds }.joinToString(", ") { it.name }
                botSay(t.selectedSoFar.format(chosen.ifBlank { "-" }))
                setPrompt(workTypeReplies())
            }
            Step.APPLY_DECLARATION -> when {
                isYes(value) -> submitApplicationChat()
                isNo(value) -> { botSay(t.mustAcceptDeclaration); setPrompt(yesNoReplies()) }
                else -> { botSay(t.notUnderstood); setPrompt(yesNoReplies()) }
            }
            Step.TRACK_MODE -> when (normalize(value)) {
                "ack", t.byAck.lowercase() -> askAckNo()
                "name", t.byName.lowercase() -> askTrackName()
                else -> { botSay(t.notUnderstoodOption); setPrompt(trackModeReplies()) }
            }
            Step.TRACK_ACK_NO -> {
                ackNo = value.trim().uppercase()
                searchByAck()
            }
            Step.TRACK_NAME -> {
                trackName = value.trim()
                proceedToDistrict(isApply = false)
            }
            Step.TRACK_DISTRICT -> {
                val option = matchOption(value, districts)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(districts.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedDistrict = option
                proceedToTaluka(isApply = false, districtId = option.id)
            }
            Step.TRACK_TALUKA -> {
                val option = matchOption(value, talukas)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(talukas.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedTaluka = option
                proceedToVillage(isApply = false, talukaId = option.id)
            }
            Step.TRACK_VILLAGE -> {
                val option = matchOption(value, villages)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(villages.map {
                    QuickReply(
                        it.label,
                        it.label
                    )
                }); return }
                selectedVillage = option
                searchByNameVillage()
            }
            Step.ENDED -> when (value) {
                "restart" -> restart()
                "navigate_track" -> handleNavigate()
            }
        }
    }

    // ---------- quick-reply builders ----------

    private fun yesNoReplies() = listOf(QuickReply(t.yes, "yes"), QuickReply(t.no, "no"))
    private fun workTypeReplies() = workTypes.map {
        QuickReply(
            it.name,
            it.name
        )
    } + QuickReply(t.done, "done")
    private fun trackModeReplies() = listOf(
        QuickReply(t.byAck, "ack"),
        QuickReply(t.byName, "name")
    )

    // ---------- apply flow ----------

    private fun askApplyIntent() {
        step = Step.ASK_APPLY_INTENT
        botSay(t.askApply)
        setPrompt(yesNoReplies())
    }

    private fun askTrackIntent() {
        step = Step.ASK_TRACK_INTENT
        botSay(t.askTrack)
        setPrompt(yesNoReplies())
    }

    private fun startApplyFlow() {
        step = Step.APPLY_AADHAAR
        botSay(t.askAadhaar)
        setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
    }

    private fun sendOtpForChat() {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.sendOtp(aadhaar)) {
                is ApiResult.Success -> {
                    txn = result.data.txn
                    step = Step.APPLY_OTP
                    botSay(t.askOtp.format(aadhaar.takeLast(4)))
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    step = Step.APPLY_AADHAAR
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                }
            }
        }
    }

    private fun verifyOtpForChat() {
        val currentTxn = txn
        if (currentTxn == null) {
            botSay(t.askAadhaar)
            step = Step.APPLY_AADHAAR
            setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
            return
        }
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.verifyOtp(aadhaar, currentTxn, otp)) {
                is ApiResult.Success -> {
                    val data = result.data
                    applicantName = data.name
                    dob = data.dob
                    age = data.age
                    gender = data.gender
                    permanentAddress = data.address
                    botSay(t.detailsConfirmed.format(applicantName))
                    step = Step.APPLY_MOBILE
                    botSay(t.askMobile)
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Phone)
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    step = Step.APPLY_OTP
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                }
            }
        }
    }

    private fun askCurrentAddress() {
        step = Step.APPLY_CURRENT_ADDRESS
        botSay(t.askCurrentAddress)
        setPrompt(listOf(QuickReply(t.skip, "skip")), inputEnabled = true)
    }

    private fun proceedToDistrict(isApply: Boolean) {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getDistricts()) {
                is ApiResult.Success -> {
                    districts = result.data.map { DropdownOption(it.id, it.districtName) }
                    step = if (isApply) Step.APPLY_DISTRICT else Step.TRACK_DISTRICT
                    botSay(t.askDistrict)
                    setPrompt(districts.map { QuickReply(it.label, it.label) })
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(inputEnabled = true)
                }
            }
        }
    }

    private fun proceedToTaluka(isApply: Boolean, districtId: Int) {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getTalukas(districtId)) {
                is ApiResult.Success -> {
                    talukas = result.data.map { DropdownOption(it.id, it.talukaName) }
                    step = if (isApply) Step.APPLY_TALUKA else Step.TRACK_TALUKA
                    botSay(t.askTaluka)
                    setPrompt(talukas.map { QuickReply(it.label, it.label) })
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(districts.map { QuickReply(it.label, it.label) })
                }
            }
        }
    }

    private fun proceedToVillage(isApply: Boolean, talukaId: Int) {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getVillages(talukaId)) {
                is ApiResult.Success -> {
                    villages = result.data.map { DropdownOption(it.id, it.villageName) }
                    step = if (isApply) Step.APPLY_VILLAGE else Step.TRACK_VILLAGE
                    botSay(t.askVillage)
                    setPrompt(villages.map { QuickReply(it.label, it.label) })
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(talukas.map { QuickReply(it.label, it.label) })
                }
            }
        }
    }

    private fun proceedToWorkTypes() {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getWorkTypes()) {
                is ApiResult.Success -> {
                    workTypes = result.data
                    step = Step.APPLY_WORK_TYPES
                    botSay(t.askWorkTypes)
                    setPrompt(workTypeReplies())
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(inputEnabled = true)
                }
            }
        }
    }

    private fun askDeclaration() {
        step = Step.APPLY_DECLARATION
        botSay(t.askDeclaration)
        setPrompt(yesNoReplies())
    }

    private fun submitApplicationChat() {
        val request = ApplicationRequest(
            applicantName = applicantName.trim(),
            aadhaarNo = aadhaar,
            gender = gender.ifBlank { null },
            mobile = mobile,
            permanentAddress = permanentAddress.ifBlank { null },
            currentAddress = currentAddress.ifBlank { null },
            dob = dob.ifBlank { null },
            age = age,
            district = selectedDistrict?.id,
            taluka = selectedTaluka?.id,
            village = selectedVillage?.id,
            workTypes = selectedWorkTypeIds.toList(),
            declaration = true
        )
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.submitApplication(request)) {
                is ApiResult.Success -> {
                    botSay(t.submitSuccess.format(result.data.acknowledgmentNo))
                    endWithNavigateOption()
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(yesNoReplies())
                }
            }
        }
    }

    // ---------- track flow ----------

    private fun startTrackFlow() {
        step = Step.TRACK_MODE
        botSay(t.askTrackMode)
        setPrompt(trackModeReplies())
    }

    private fun askAckNo() {
        step = Step.TRACK_ACK_NO
        botSay(t.askAckNo)
        setPrompt(inputEnabled = true)
    }

    private fun askTrackName() {
        step = Step.TRACK_NAME
        botSay(t.askTrackName)
        setPrompt(inputEnabled = true)
    }

    private fun searchByAck() {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getStatusByAck(ackNo)) {
                is ApiResult.Success -> {
                    botSay(t.trackResultPrefix + "\n" + formatResult(result.data))
                    endWithNavigateOption()
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    step = Step.TRACK_ACK_NO
                    setPrompt(inputEnabled = true)
                }
            }
        }
    }

    private fun searchByNameVillage() {
        val village = selectedVillage ?: return
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getStatusByNameVillage(trackName, village.id)) {
                is ApiResult.Success -> {
                    botSay(t.trackResultPrefix + "\n" + formatResult(result.data))
                    endWithNavigateOption()
                }
                is ApiResult.Error -> {
                    botSay(result.message)
                    setPrompt(villages.map { QuickReply(it.label, it.label) })
                }
            }
        }
    }

    private fun formatResult(data: ApplicationStatusDto): String {
        val lines = mutableListOf(
            "${data.applicantName} — ${data.acknowledgmentNo}",
            "Status: ${data.statusDisplay}"
        )
        listOfNotNull(data.districtName, data.talukaName, data.villageName)
            .takeIf { it.isNotEmpty() }
            ?.let { lines += "Location: ${it.joinToString(", ")}" }
        if (data.status == "rejected" && !data.rejectionReason.isNullOrBlank()) {
            lines += "Reason: ${data.rejectionReason}"
        }
        if (data.status == "approved" && data.hasCertificate) {
            lines += "Your certificate is ready to download."
        }
        return lines.joinToString("\n")
    }

    private fun endWithNavigateOption() {
        step = Step.ENDED
        _uiState.update {
            it.copy(
                inputEnabled = false,
                quickReplies = listOf(
                    QuickReply(t.viewDetails, "navigate_track"),
                    QuickReply(t.restart, "restart")
                ),
                busy = false,
                ended = true
            )
        }
    }

    private fun handleNavigate() {
        _uiState.update { it.copy(requestNavigateRoute = Screen.Track.route) }
    }

    private fun resetFlowState() {
        aadhaar = ""; txn = null; otp = ""
        applicantName = ""; dob = ""; age = null; gender = ""; permanentAddress = ""
        mobile = ""; currentAddress = ""
        districts = emptyList(); talukas = emptyList(); villages = emptyList()
        selectedDistrict = null; selectedTaluka = null; selectedVillage = null
        workTypes = emptyList(); selectedWorkTypeIds = emptySet()
        ackNo = ""; trackName = ""
    }

    private fun restart() {
        resetFlowState()
        botSay(t.restarting)
        askApplyIntent()
    }
}
