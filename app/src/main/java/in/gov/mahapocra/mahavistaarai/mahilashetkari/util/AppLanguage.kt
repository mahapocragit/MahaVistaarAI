package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util

enum class AppLanguage { EN, MR }

/** Small hand-picked bilingual string table for the chrome that appears on
 *  every screen (nav bar, hero, common section titles). Form field labels
 *  inside Apply/Track stay in English to match the field names coming back
 *  from the API, but every user-facing heading and button is bilingual. */
object Strings {

    data class Nav(val home: String, val eligibility: String, val applyNow: String, val track: String, val resources: String)
    fun nav(lang: AppLanguage) = if (lang == AppLanguage.EN)
        Nav("Home", "Eligibility", "Apply Now", "Track Application", "Resources")
    else
        Nav("मुख्यपृष्ठ", "पात्रता", "अर्ज करा", "अर्जाची स्थिती", "संसाधने")

    data class Home(
        val badge: String,
        val heading: String,
        val body: String,
        val applyNow: String,
        val checkEligibility: String,
        val trackApplication: String,
        val downloadCertificate: String,
        val footer: String
    )
    fun home(lang: AppLanguage) = if (lang == AppLanguage.EN)
        Home(
            badge = "Maharashtra Women Farmer Empowerment Act, 2026",
            heading = "A woman who works the land deserves the identity of a farmer.",
            body = "This new law gives every woman engaged in farming or allied work — regardless of whose name the land is in — an official Woman Farmer identity and certificate.",
            applyNow = "Apply Now",
            checkEligibility = "Check Eligibility",
            trackApplication = "Track Application",
            downloadCertificate = "Download Certificate",
            footer = "First state in India with a dedicated law for women farmers."
        )
    else
        Home(
            badge = "महाराष्ट्र महिला शेतकरी सक्षमीकरण अधिनियम, २०२६",
            heading = "जी स्त्री जमीन कसते, तिला शेतकरी म्हणून ओळख मिळायलाच हवी.",
            body = "हा नवीन कायदा शेती किंवा शेतीपूरक कामात गुंतलेल्या प्रत्येक महिलेला — जमीन कोणाच्याही नावावर असो — अधिकृत महिला शेतकरी ओळख आणि प्रमाणपत्र देतो.",
            applyNow = "अर्ज करा",
            checkEligibility = "पात्रता तपासा",
            trackApplication = "अर्जाची स्थिती पहा",
            downloadCertificate = "प्रमाणपत्र डाउनलोड करा",
            footer = "महिला शेतकऱ्यांसाठी स्वतंत्र कायदा असणारे भारतातील पहिले राज्य."
        )

    data class Apply(
        val title: String,
        val subtitle: String,
        val step1Title: String,
        val step1Body: String,
        val sendOtp: String,
        val step1OtpTitle: String,
        val otpBody: String,
        val verifyContinue: String,
        val resendOtp: String,
        val changeAadhaar: String,
        val step2Title: String,
        val step2Body: String,
        val locationTitle: String,
        val workTypeTitle: String,
        val workTypeSubtitle: String,
        val declaration: String,
        val submit: String,
        val successTitle: String,
        val successBody: String,
        val submitAnother: String
    )
    fun apply(lang: AppLanguage) = if (lang == AppLanguage.EN)
        Apply(
            title = "Apply for Woman Farmer Certificate",
            subtitle = "Woman Farmer identity & certificate",
            step1Title = "Step 1 of 3 — Verify your Aadhaar",
            step1Body = "We'll send a one-time password to the mobile number linked to this Aadhaar card.",
            sendOtp = "Send OTP",
            step1OtpTitle = "Step 1 of 3 — Enter OTP",
            otpBody = "OTP sent to the mobile number linked to Aadhaar ending in %s.",
            verifyContinue = "Verify & Continue",
            resendOtp = "Resend OTP",
            changeAadhaar = "Change Aadhaar number",
            step2Title = "Step 2 of 3 — Your details",
            step2Body = "Verified via Aadhaar. Confirm the details below.",
            locationTitle = "Location",
            workTypeTitle = "Type of agricultural work",
            workTypeSubtitle = "Select all that apply",
            declaration = "I declare that the information provided above is true to the best of my knowledge.",
            submit = "Submit Application",
            successTitle = "Application submitted!",
            successBody = "Save your acknowledgment number to track your application status.",
            submitAnother = "Submit another application"
        )
    else
        Apply(
            title = "महिला शेतकरी प्रमाणपत्रासाठी अर्ज करा",
            subtitle = "महिला शेतकरी ओळख आणि प्रमाणपत्र",
            step1Title = "टप्पा १ पैकी ३ — आधार पडताळणी करा",
            step1Body = "या आधार कार्डाशी जोडलेल्या मोबाइल क्रमांकावर आम्ही एकवेळ पासवर्ड (OTP) पाठवू.",
            sendOtp = "OTP पाठवा",
            step1OtpTitle = "टप्पा १ पैकी ३ — OTP प्रविष्ट करा",
            otpBody = "आधार क्रमांकाशी जोडलेल्या, %s ने समाप्त होणाऱ्या मोबाइल क्रमांकावर OTP पाठवला आहे.",
            verifyContinue = "पडताळा आणि पुढे जा",
            resendOtp = "OTP पुन्हा पाठवा",
            changeAadhaar = "आधार क्रमांक बदला",
            step2Title = "टप्पा २ पैकी ३ — तुमचे तपशील",
            step2Body = "आधारद्वारे पडताळणी झाली. खालील तपशील तपासून पुष्टी करा.",
            locationTitle = "स्थान",
            workTypeTitle = "शेतीकामाचा प्रकार",
            workTypeSubtitle = "लागू असलेले सर्व निवडा",
            declaration = "वर दिलेली माहिती माझ्या माहितीनुसार खरी आहे, असे मी जाहीर करते.",
            submit = "अर्ज सादर करा",
            successTitle = "अर्ज सादर झाला!",
            successBody = "तुमच्या अर्जाची स्थिती पाहण्यासाठी पोच क्रमांक जपून ठेवा.",
            submitAnother = "आणखी एक अर्ज सादर करा"
        )

    data class ChatBot(
        val fabDescription: String,
        val title: String,
        val greeting: String,
        val askApply: String,
        val askTrack: String,
        val goodbye: String,
        val notUnderstood: String,
        val yes: String,
        val no: String,
        val askAadhaar: String,
        val askOtp: String,
        val detailsConfirmed: String,
        val askMobile: String,
        val askCurrentAddress: String,
        val skip: String,
        val askDistrict: String,
        val askTaluka: String,
        val askVillage: String,
        val notUnderstoodOption: String,
        val askWorkTypes: String,
        val selectedSoFar: String,
        val done: String,
        val pickAtLeastOne: String,
        val askDeclaration: String,
        val mustAcceptDeclaration: String,
        val submitSuccess: String,
        val askTrackMode: String,
        val byAck: String,
        val byName: String,
        val askAckNo: String,
        val askTrackName: String,
        val trackResultPrefix: String,
        val restart: String,
        val viewDetails: String,
        val restarting: String,
        val typing: String,
        val inputHint: String,
        val deleteAll: String,
        val deleteAllConfirmTitle: String,
        val deleteAllConfirmBody: String,
        val deleteAllConfirm: String,
        val cancel: String
    )
    fun chatBot(lang: AppLanguage) = if (lang == AppLanguage.EN)
        ChatBot(
            fabDescription = "Chat with assistant",
            title = "Assistant",
            greeting = "Hi! I'm your assistant for the Women Farmer scheme.",
            askApply = "Would you like to apply for the Woman Farmer certificate?",
            askTrack = "Would you like to track your application status instead?",
            goodbye = "Alright, no problem. Tap below anytime to start again.",
            notUnderstood = "Sorry, please answer Yes or No (हो / नाही).",
            yes = "Yes",
            no = "No",
            askAadhaar = "Great! Please say or type your 12-digit Aadhaar number.",
            askOtp = "OTP sent to the mobile number linked to Aadhaar ending in %s. Please enter it.",
            detailsConfirmed = "Thanks %s, I've verified your Aadhaar details.",
            askMobile = "What's your mobile number for SMS updates?",
            askCurrentAddress = "Your current address (optional). Say or type it, or tap Skip.",
            skip = "Skip",
            askDistrict = "Which district are you in?",
            askTaluka = "Which taluka?",
            askVillage = "Which village?",
            notUnderstoodOption = "Sorry, I didn't catch that. Please tap one of the options, or say the name again.",
            askWorkTypes = "What type of agricultural work do you do? Select all that apply, then tap Done.",
            selectedSoFar = "Selected so far: %s",
            done = "Done",
            pickAtLeastOne = "Please select at least one type of work first.",
            askDeclaration = "I declare that the information provided is true to the best of my knowledge. Do you agree?",
            mustAcceptDeclaration = "You need to accept the declaration to submit your application. Do you agree?",
            submitSuccess = "Application submitted! Your acknowledgment number is %s. Save it to track your status.",
            askTrackMode = "How would you like to check your status — by acknowledgment number, or by name and village?",
            byAck = "By acknowledgment number",
            byName = "By name & village",
            askAckNo = "Please say or type your acknowledgment number.",
            askTrackName = "What's the applicant's full name?",
            trackResultPrefix = "Here's what I found:",
            restart = "Start over",
            viewDetails = "Open Track screen",
            restarting = "Okay, let's start again.",
            typing = "Typing…",
            inputHint = "Type or tap the mic to speak",
            deleteAll = "Delete all chat",
            deleteAllConfirmTitle = "Delete all chat?",
            deleteAllConfirmBody = "This will permanently delete this conversation from your device and start a new one. This can't be undone.",
            deleteAllConfirm = "Delete all",
            cancel = "Cancel"
        )
    else
        ChatBot(
            fabDescription = "सहाय्यकाशी बोला",
            title = "सहाय्यक",
            greeting = "नमस्कार! मी महिला शेतकरी योजनेसाठी तुमचा सहाय्यक आहे.",
            askApply = "तुम्हाला महिला शेतकरी प्रमाणपत्रासाठी अर्ज करायचा आहे का?",
            askTrack = "तुम्हाला तुमच्या अर्जाची स्थिती तपासायची आहे का?",
            goodbye = "ठीक आहे, हरकत नाही. पुन्हा सुरू करण्यासाठी खालील बटण दाबा.",
            notUnderstood = "माफ करा, कृपया हो किंवा नाही असे उत्तर द्या.",
            yes = "हो",
            no = "नाही",
            askAadhaar = "छान! कृपया तुमचा १२ अंकी आधार क्रमांक सांगा किंवा टाइप करा.",
            askOtp = "आधार क्रमांकाशी जोडलेल्या, %s ने समाप्त होणाऱ्या मोबाइल क्रमांकावर OTP पाठवला आहे. कृपया तो प्रविष्ट करा.",
            detailsConfirmed = "धन्यवाद %s, तुमचे आधार तपशील पडताळले गेले आहेत.",
            askMobile = "SMS अद्यतनांसाठी तुमचा मोबाइल क्रमांक काय आहे?",
            askCurrentAddress = "तुमचा सध्याचा पत्ता (ऐच्छिक). सांगा किंवा टाइप करा, किंवा वगळा दाबा.",
            skip = "वगळा",
            askDistrict = "तुम्ही कोणत्या जिल्ह्यात आहात?",
            askTaluka = "कोणता तालुका?",
            askVillage = "कोणते गाव?",
            notUnderstoodOption = "माफ करा, समजले नाही. कृपया पर्यायांपैकी एकावर टॅप करा, किंवा नाव पुन्हा सांगा.",
            askWorkTypes = "तुम्ही कोणत्या प्रकारचे शेतीकाम करता? लागू असलेले सर्व निवडा, नंतर पूर्ण दाबा.",
            selectedSoFar = "आतापर्यंत निवडलेले: %s",
            done = "पूर्ण",
            pickAtLeastOne = "कृपया आधी किमान एक कामाचा प्रकार निवडा.",
            askDeclaration = "वर दिलेली माहिती माझ्या माहितीनुसार खरी आहे, असे मी जाहीर करते. तुम्ही सहमत आहात का?",
            mustAcceptDeclaration = "अर्ज सादर करण्यासाठी तुम्हाला घोषणापत्र स्वीकारावे लागेल. तुम्ही सहमत आहात का?",
            submitSuccess = "अर्ज सादर झाला! तुमचा पोच क्रमांक %s आहे. स्थिती तपासण्यासाठी तो जपून ठेवा.",
            askTrackMode = "तुम्हाला तुमची स्थिती कशी तपासायची आहे — पोच क्रमांकाने, की नाव आणि गावाने?",
            byAck = "पोच क्रमांकाने",
            byName = "नाव आणि गावाने",
            askAckNo = "कृपया तुमचा पोच क्रमांक सांगा किंवा टाइप करा.",
            askTrackName = "अर्जदाराचे पूर्ण नाव काय आहे?",
            trackResultPrefix = "मला हे आढळले:",
            restart = "पुन्हा सुरू करा",
            viewDetails = "स्थिती स्क्रीन उघडा",
            restarting = "ठीक आहे, पुन्हा सुरुवात करूया.",
            typing = "टाइप करत आहे…",
            inputHint = "टाइप करा किंवा बोलण्यासाठी माइक दाबा",
            deleteAll = "सर्व चॅट हटवा",
            deleteAllConfirmTitle = "सर्व चॅट हटवायचे का?",
            deleteAllConfirmBody = "हे संभाषण तुमच्या डिव्हाइसवरून कायमचे हटवले जाईल आणि नवीन संभाषण सुरू होईल. ही क्रिया पूर्ववत करता येणार नाही.",
            deleteAllConfirm = "सर्व हटवा",
            cancel = "रद्द करा"
        )

    data class Track(
        val title: String,
        val subtitle: String,
        val byAck: String,
        val byNameVillage: String,
        val checkStatus: String,
        val downloadCertificate: String,
        val certificateGenerating: String
    )
    fun track(lang: AppLanguage) = if (lang == AppLanguage.EN)
        Track(
            title = "Track Application",
            subtitle = "Check your application status",
            byAck = "By Ack. No.",
            byNameVillage = "By Name & Village",
            checkStatus = "Check Status",
            downloadCertificate = "Download Certificate",
            certificateGenerating = "Approved — certificate is being generated. Please check back shortly."
        )
    else
        Track(
            title = "अर्जाची स्थिती पहा",
            subtitle = "तुमच्या अर्जाची स्थिती तपासा",
            byAck = "पोच क्रमांकाने",
            byNameVillage = "नाव आणि गावाने",
            checkStatus = "स्थिती तपासा",
            downloadCertificate = "प्रमाणपत्र डाउनलोड करा",
            certificateGenerating = "मंजूर — प्रमाणपत्र तयार होत आहे. कृपया थोड्या वेळाने पुन्हा तपासा."
        )
}
