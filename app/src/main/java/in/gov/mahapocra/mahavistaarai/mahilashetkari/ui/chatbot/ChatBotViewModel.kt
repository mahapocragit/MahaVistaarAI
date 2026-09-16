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
import kotlin.text.clear

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
        APPLY_DISTRICT, APPLY_TALUKA, APPLY_VILLAGE, APPLY_WORK_TYPES, APPLY_CASTE_CATEGORY,
        APPLY_FAMILY_FARMER_ID, APPLY_FAMILY_FARMER_ID_VALUE, APPLY_DECLARATION,
        TRACK_MODE, TRACK_ACK_NO, TRACK_NAME, TRACK_DISTRICT, TRACK_TALUKA, TRACK_VILLAGE,
        ENDED
    }

    private var step = Step.ASK_APPLY_INTENT

    // ---- Apply flow scratch data ----
    private var aadhaar = ""
    private var txn: String? = null
    private var otp = ""
    private var applicantName = ""
    private var applicantNameMr = ""
    private var photoUrl = ""
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
    private var casteCategories: List<DropdownOption> = emptyList()
    private var selectedCasteCategory: DropdownOption? = null
    private var hasFamilyFarmerId: Boolean? = null
    private var familyFarmerId = ""

    // ---- Track flow scratch data ----
    private var ackNo = ""
    private var trackName = ""

    init {
        viewModelScope.launch {
            val history = historyStore.load()
            val session = if (history.isNotEmpty()) historyStore.loadSession() else null
            if (history.isNotEmpty()) {
                idGen.set(history.maxOf { it.id })
            }
            if (session != null) {
                // Existing conversation with a saved step — resume silently,
                // with no repeated greeting and no re-asked question.
                restoreSession(session)
                _uiState.update {
                    it.copy(
                        messages = history,
                        quickReplies = session.quickReplies,
                        inputEnabled = session.inputEnabled,
                        keyboardType = keyboardTypeFromName(session.keyboardType),
                        ended = session.ended,
                        busy = false
                    )
                }
            } else {
                if (history.isNotEmpty()) {
                    _uiState.update { it.copy(messages = history) }
                }
                botSay(t.greeting)
                askApplyIntent()
            }
        }
    }

    // ---------- message helpers ----------

    private fun botSay(text: String, isError: Boolean = false) {
        _uiState.update { it.copy(messages = it.messages + ChatMessage(idGen.incrementAndGet(), text, isUser = false, isError = isError)) }
        persistMessages()
    }

    /** Surfaces the exact error message returned by the API (or network layer), styled as an error bubble. */
    private fun botError(text: String) = botSay(text, isError = true)

    private fun userSay(text: String) {
        _uiState.update { it.copy(messages = it.messages + ChatMessage(idGen.incrementAndGet(), text, isUser = true)) }
        persistMessages()
    }

    private fun persistMessages() {
        val snapshot = _uiState.value.messages
        viewModelScope.launch { historyStore.save(snapshot) }
    }

    private fun keyboardTypeName(k: KeyboardType) = when (k) {
        KeyboardType.Number -> "Number"
        KeyboardType.Phone -> "Phone"
        else -> "Text"
    }

    private fun keyboardTypeFromName(name: String) = when (name) {
        "Number" -> KeyboardType.Number
        "Phone" -> KeyboardType.Phone
        else -> KeyboardType.Text
    }

    /** Saves enough of the current step + scratch data to resume silently next time the dialog opens. */
    private fun persistSession() {
        val s = _uiState.value
        val session = ChatSessionState(
            step = step.name,
            quickReplies = s.quickReplies,
            inputEnabled = s.inputEnabled,
            keyboardType = keyboardTypeName(s.keyboardType),
            ended = s.ended,
            aadhaar = aadhaar, txn = txn, otp = otp,
            applicantName = applicantName, applicantNameMr = applicantNameMr, photoUrl = photoUrl,
            dob = dob, age = age, gender = gender,
            permanentAddress = permanentAddress, mobile = mobile, currentAddress = currentAddress,
            districts = districts, talukas = talukas, villages = villages,
            selectedDistrict = selectedDistrict, selectedTaluka = selectedTaluka, selectedVillage = selectedVillage,
            workTypes = workTypes, selectedWorkTypeIds = selectedWorkTypeIds,
            casteCategories = casteCategories, selectedCasteCategory = selectedCasteCategory,
            hasFamilyFarmerId = hasFamilyFarmerId, familyFarmerId = familyFarmerId,
            ackNo = ackNo, trackName = trackName
        )
        viewModelScope.launch { historyStore.saveSession(session) }
    }

    private fun restoreSession(session: ChatSessionState) {
        step = try { Step.valueOf(session.step) } catch (e: IllegalArgumentException) { Step.ASK_APPLY_INTENT }
        aadhaar = session.aadhaar; txn = session.txn; otp = session.otp
        applicantName = session.applicantName; applicantNameMr = session.applicantNameMr; photoUrl = session.photoUrl
        dob = session.dob; age = session.age; gender = session.gender
        permanentAddress = session.permanentAddress; mobile = session.mobile; currentAddress = session.currentAddress
        districts = session.districts; talukas = session.talukas; villages = session.villages
        selectedDistrict = session.selectedDistrict; selectedTaluka = session.selectedTaluka; selectedVillage = session.selectedVillage
        workTypes = session.workTypes; selectedWorkTypeIds = session.selectedWorkTypeIds
        casteCategories = session.casteCategories; selectedCasteCategory = session.selectedCasteCategory
        hasFamilyFarmerId = session.hasFamilyFarmerId; familyFarmerId = session.familyFarmerId
        ackNo = session.ackNo; trackName = session.trackName
    }

    /** Clears the saved transcript and starts a brand-new conversation. */
    fun deleteAllHistory() {
        resetFlowState()
        idGen.set(0)
        _uiState.update { ChatUiState() }
        viewModelScope.launch {
            historyStore.clear()
            botSay(t.greeting)
            askApplyIntent()
        }
    }

    private fun setPrompt(quickReplies: List<QuickReply> = emptyList(), inputEnabled: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text) {
        _uiState.update {
            it.copy(quickReplies = quickReplies, inputEnabled = inputEnabled, keyboardType = keyboardType, busy = false, ended = false)
        }
        persistSession()
    }

    private fun setBusy(busy: Boolean) {
        _uiState.update { it.copy(busy = busy, inputEnabled = false, quickReplies = emptyList()) }
    }

    private fun end() {
        step = Step.ENDED
        _uiState.update { it.copy(inputEnabled = false, quickReplies = listOf(QuickReply(t.restart, "restart")), ended = true) }
        persistSession()
    }

    fun consumeNavigationRequest() {
        _uiState.update { it.copy(requestNavigateRoute = null, requestNavigateAckNo = null) }
    }

    fun dismissFemaleOnlyDialog() {
        _uiState.update { it.copy(femaleOnlyDialog = false) }
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
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(districts.map { QuickReply(it.label, it.label) }); return }
                selectedDistrict = option
                selectedTaluka = null; selectedVillage = null
                proceedToTaluka(isApply = true, districtId = option.id)
            }
            Step.APPLY_TALUKA -> {
                val option = matchOption(value, talukas)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(talukas.map { QuickReply(it.label, it.label) }); return }
                selectedTaluka = option
                selectedVillage = null
                proceedToVillage(isApply = true, talukaId = option.id)
            }
            Step.APPLY_VILLAGE -> {
                val option = matchOption(value, villages)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(villages.map { QuickReply(it.label, it.label) }); return }
                selectedVillage = option
                proceedToWorkTypes()
            }
            Step.APPLY_WORK_TYPES -> {
                if (normalize(value) == "done") {
                    if (selectedWorkTypeIds.isEmpty()) { botSay(t.pickAtLeastOne); setPrompt(workTypeReplies()); return }
                    proceedToCasteCategory()
                    return
                }
                val wt = matchWorkType(value)
                if (wt == null) { botSay(t.notUnderstoodOption); setPrompt(workTypeReplies()); return }
                selectedWorkTypeIds = if (wt.id in selectedWorkTypeIds) selectedWorkTypeIds - wt.id else selectedWorkTypeIds + wt.id
                val chosen = workTypes.filter { it.id in selectedWorkTypeIds }.joinToString(", ") { it.name }
                botSay(t.selectedSoFar.format(chosen.ifBlank { "-" }))
                setPrompt(workTypeReplies())
            }
            Step.APPLY_CASTE_CATEGORY -> {
                val option = matchOption(value, casteCategories)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(casteCategories.map { QuickReply(it.label, it.label) }); return }
                selectedCasteCategory = option
                askFamilyFarmerId()
            }
            Step.APPLY_FAMILY_FARMER_ID -> when {
                isYes(value) -> {
                    hasFamilyFarmerId = true
                    step = Step.APPLY_FAMILY_FARMER_ID_VALUE
                    botSay(t.askFamilyFarmerIdValue)
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                }
                isNo(value) -> {
                    hasFamilyFarmerId = false
                    familyFarmerId = ""
                    askDeclaration()
                }
                else -> { botSay(t.notUnderstood); setPrompt(yesNoReplies()) }
            }
            Step.APPLY_FAMILY_FARMER_ID_VALUE -> {
                val digits = value.filter { it.isDigit() }
                if (digits.length != 11) {
                    botSay(t.invalidFarmerId)
                    setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                    return
                }
                familyFarmerId = digits
                askDeclaration()
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
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(districts.map { QuickReply(it.label, it.label) }); return }
                selectedDistrict = option
                proceedToTaluka(isApply = false, districtId = option.id)
            }
            Step.TRACK_TALUKA -> {
                val option = matchOption(value, talukas)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(talukas.map { QuickReply(it.label, it.label) }); return }
                selectedTaluka = option
                proceedToVillage(isApply = false, talukaId = option.id)
            }
            Step.TRACK_VILLAGE -> {
                val option = matchOption(value, villages)
                if (option == null) { botSay(t.notUnderstoodOption); setPrompt(villages.map { QuickReply(it.label, it.label) }); return }
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
    private fun workTypeReplies() = workTypes.map { QuickReply(it.name, it.name) } + QuickReply(t.done, "done")
    private fun trackModeReplies() = listOf(QuickReply(t.byAck, "ack"), QuickReply(t.byName, "name"))

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
                    botError(result.message)
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
                    if (!Validators.isFemale(data.gender)) {
                        aadhaar = ""; txn = null; otp = ""
                        botError(t.femaleOnlyMessage)
                        step = Step.APPLY_AADHAAR
                        _uiState.update { it.copy(femaleOnlyDialog = true) }
                        setPrompt(inputEnabled = true, keyboardType = KeyboardType.Number)
                        return@launch
                    }
                    applicantName = data.name
                    applicantNameMr = data.nameInMarathi
                    photoUrl = data.imageUrl
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
                    botError(result.message)
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
                    botError(result.message)
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
                    botError(result.message)
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
                    botError(result.message)
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
                    botError(result.message)
                    setPrompt(inputEnabled = true)
                }
            }
        }
    }

    private fun proceedToCasteCategory() {
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.getCasteCategories()) {
                is ApiResult.Success -> {
                    casteCategories = result.data.map { DropdownOption(it.id, it.name) }
                    step = Step.APPLY_CASTE_CATEGORY
                    botSay(t.askCasteCategory)
                    setPrompt(casteCategories.map { QuickReply(it.label, it.label) })
                }
                is ApiResult.Error -> {
                    botError(result.message)
                    setPrompt(workTypeReplies())
                }
            }
        }
    }

    private fun askFamilyFarmerId() {
        step = Step.APPLY_FAMILY_FARMER_ID
        botSay(t.askFamilyFarmerId)
        setPrompt(yesNoReplies())
    }

    private fun askDeclaration() {
        step = Step.APPLY_DECLARATION
        botSay(t.askDeclaration)
        setPrompt(yesNoReplies())
    }

    private fun submitApplicationChat() {
        val casteCategoryId = selectedCasteCategory?.id ?: return
        val request = ApplicationRequest(
            applicantName = applicantName.trim(),
            applicantNameMr = applicantNameMr,
            aadhaarNo = aadhaar,
            photoUrl = photoUrl,
            casteCategory = casteCategoryId,
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
            declaration = true,
            farmerId = if (hasFamilyFarmerId == true) familyFarmerId else null
        )
        viewModelScope.launch {
            setBusy(true)
            when (val result = repository.submitApplication(request)) {
                is ApiResult.Success -> {
                    ackNo = result.data.acknowledgmentNo
                    botSay(t.submitSuccess.format(result.data.acknowledgmentNo))
                    endWithNavigateOption()
                }
                is ApiResult.Error -> {
                    botError(result.message)
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
                    botError(result.message)
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
                    ackNo = result.data.acknowledgmentNo
                    botSay(t.trackResultPrefix + "\n" + formatResult(result.data))
                    endWithNavigateOption()
                }
                is ApiResult.Error -> {
                    botError(result.message)
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
                quickReplies = listOf(QuickReply(t.viewDetails, "navigate_track"), QuickReply(t.restart, "restart")),
                busy = false,
                ended = true
            )
        }
        persistSession()
    }

    private fun handleNavigate() {
        _uiState.update { it.copy(requestNavigateRoute = Screen.Track.route, requestNavigateAckNo = ackNo.ifBlank { null }) }
    }

    private fun resetFlowState() {
        aadhaar = ""; txn = null; otp = ""
        applicantName = ""; applicantNameMr = ""; photoUrl = ""
        dob = ""; age = null; gender = ""; permanentAddress = ""
        mobile = ""; currentAddress = ""
        districts = emptyList(); talukas = emptyList(); villages = emptyList()
        selectedDistrict = null; selectedTaluka = null; selectedVillage = null
        workTypes = emptyList(); selectedWorkTypeIds = emptySet()
        casteCategories = emptyList(); selectedCasteCategory = null
        hasFamilyFarmerId = null; familyFarmerId = ""
        ackNo = ""; trackName = ""
    }

    private fun restart() {
        resetFlowState()
        botSay(t.restarting)
        askApplyIntent()
    }
}

