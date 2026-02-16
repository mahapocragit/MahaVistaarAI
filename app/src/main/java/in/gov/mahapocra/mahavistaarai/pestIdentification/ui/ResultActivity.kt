package `in`.gov.mahapocra.mahavistaarai.pestIdentification.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResultBinding
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.data.model.AnalysisData
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.data.repository.PredictRepository
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityResultBinding
    private var predictRespStoredId: Int = 0
    private var cropNameEn = ""
    private lateinit var textToSpeech: TextToSpeech
    private var currentUtteranceId: String? = null
    private var currentPlayingButton: ImageView? = null

    var languageToLoad = "mr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (AppSettings.getLanguage(this@ResultActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.analysis_result_text)
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            stopTtsAndNavigate()
        }

        val analysisData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("analysis_data", AnalysisData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("analysis_data")
        }

        initViewModel()
        viewModel.getStoreDataResponse.observe(this) { result ->
            if (result != null) {
                val json = JSONObject(result)
                predictRespStoredId = json.optString("data").toInt()
                if (predictRespStoredId > 0) binding.edtFeedBack.visibility =
                    View.VISIBLE else binding.edtFeedBack.visibility = View.GONE
            }
        }
        analysisData?.let {
            if (it.farmerId != 0) {
                viewModel.storeResponse(
                    it.farmerId,
                    it.cropId,
                    it.sowingDate,
                    it.isSuccess,
                    it.responseString,
                    it.imageUri,
                    it.diseaseId
                )
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopTtsAndNavigate()
            }
        })

        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {

                val marathiLocale = Locale.forLanguageTag("mr-IN")
                val result = textToSpeech.setLanguage(marathiLocale)

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    handleTtsFallback()
                }
            }
        }

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                currentUtteranceId = null

                currentPlayingButton?.setImageDrawable(
                    ContextCompat.getDrawable(this@ResultActivity, R.drawable.ic_speaker)
                )
                currentPlayingButton = null
            }

            override fun onError(utteranceId: String?) {
                currentUtteranceId = null

                currentPlayingButton?.setImageDrawable(
                    ContextCompat.getDrawable(this@ResultActivity, R.drawable.ic_speaker)
                )
                currentPlayingButton = null
            }
        })

        val resultObjectString = intent.getStringExtra("resultObjectString")
        val imageUriString = intent.getStringExtra("imageUriString")
        val predictionJsonString = intent.getStringExtra("predictionJsonObject")
        val cropNameMr = intent.getStringExtra("cropNameMr")

        val predictionJsonObject = JSONArray(predictionJsonString ?: "[]")
        binding.advisoryCard.visibility =
            if (predictionJsonObject.length() > 0) View.VISIBLE else View.GONE
        binding.advisoryCard.visibility = View.VISIBLE

        Log.d("TAGGER", "onCreate: $predictionJsonObject and $predictionJsonString on the monday")
        binding.edtFeedBack.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.mainScrollView.post {
                    binding.mainScrollView.smoothScrollTo(0, binding.edtFeedBack.bottom)
                }
            }
        }
        val imageUri = imageUriString?.let { Uri.parse(it) }
        binding.imageSubmittedPreview.setImageURI(imageUri)

        val jSONObject = JSONObject(resultObjectString)
        val dataObject = jSONObject.optJSONObject("data")
        val cropType = dataObject?.optString("crop_type")
        val cropTypeMr = dataObject?.optString("crop_type")
        val predictionArray = dataObject?.optJSONArray("predictions")

        binding.textSummary.text = cropType?.replaceFirstChar { it.uppercaseChar() }
        predictionArray?.length()?.let {
            try {
                val dataObject = predictionJsonObject.get(0) as JSONObject
                cropNameEn = dataObject.optString("crop_name")
                val diseasePest = if (dataObject.optString("disease_pest") != "null") {
                    dataObject.optString("disease_pest")
                } else {
                    ""
                }
                val diseasePestMr = if (dataObject.optString("disease_pest_mr") != "null") {
                    dataObject.optString("disease_pest_mr")
                } else {
                    ""
                }
                binding.pestOne.text =
                    if (languageToLoad == "mr") "${getString(R.string.disease_text)} $diseasePestMr"
                    else "${getString(R.string.disease_text)} $diseasePest"
                val preventiveMeasuresTxt =
                    if (dataObject.optString("preventive_measures") != "null") {
                        dataObject.optString("preventive_measures").trim()
                    } else {
                        ""
                    }
                val curativeMeasuresTxt = if (dataObject.optString("curative_measures") != "null") {
                    dataObject.optString("curative_measures").trim()
                } else {
                    ""
                }

                binding.textSummary.text = if (languageToLoad == "en") cropNameEn else cropNameMr
                val note = if (dataObject.optString("note") == "null") {
                    ""
                } else {
                    dataObject.optString("note").trim()
                }
                binding.noteTextView.text = note
                binding.noteTextView.visibility = if (note == "") View.GONE else View.VISIBLE
                binding.noteTextView.visibility = if (note.trim().isNotEmpty()) View.VISIBLE else View.GONE

                Log.d(
                    "TAGGER",
                    "onCreate: preventive $preventiveMeasuresTxt and curative $curativeMeasuresTxt"
                )

                binding.preventiveMeasureTextView.text = preventiveMeasuresTxt
                binding.preventiveMeasureSoundImageView.visibility =
                    if (preventiveMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE
                binding.preventiveMeasureTextView.visibility =
                    if (preventiveMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE
                binding.preventiveMeasureLabelTextView.visibility =
                    if (preventiveMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE

                binding.curativeMeasureTextView.text = curativeMeasuresTxt
                binding.curativeMeasureSoundImageView.visibility =
                    if (curativeMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE
                binding.curativeMeasureLabelTextView.visibility =
                    if (curativeMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE
                binding.curativeMeasureTextView.visibility =
                    if (curativeMeasuresTxt.isNotEmpty()) View.VISIBLE else View.GONE



                binding.preventiveMeasureSoundImageView.setOnClickListener {
                    toggleTts(
                        text = preventiveMeasuresTxt,
                        utteranceId = "PREVENTIVE_MEASURES_TTS",
                        binding.preventiveMeasureSoundImageView
                    )
                }

                binding.curativeMeasureSoundImageView.setOnClickListener {
                    toggleTts(
                        text = curativeMeasuresTxt,
                        utteranceId = "CURATIVE_MEASURES_TTS",
                        binding.curativeMeasureSoundImageView
                    )
                }
            } catch (e: Exception) {
                Log.d("TAGGER", "onCreate: ${e.message}")
            }
        }

        viewModel.feedbackResponse.observe(this) { result ->
            ProgressHelper.disableProgressDialog()
            Log.d("feedbackResponse==", "feedbackResponse: $result")
            val json = JSONObject(result)
            val message = json.optString("message")
            showToast(message)
            stopTtsAndNavigate()
        }

        binding.btnNewScan.setOnClickListener {
            stopTtsAndNavigate()
        }

        binding.btnSubmitFeedback.setOnClickListener { validateFeedbackAndSubmit() }
        binding.edtFeedBack.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    val feedbackStr = it.toString()
                    if (feedbackStr.isNotEmpty()) {
                        binding.btnSubmitFeedback.visibility = View.VISIBLE
                    } else {
                        binding.btnSubmitFeedback.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun handleTtsFallback() {
        // Try Hindi
        val hindiLocale = Locale.forLanguageTag("hi-IN")
        val result = textToSpeech.setLanguage(hindiLocale)

        if (result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            // Final fallback to English
            textToSpeech.language = Locale.forLanguageTag("en-US")
        }

        // Prompt user to install TTS data
        try {
            val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("TTS", "Unable to open TTS install screen", e)
        }
    }


    private fun toggleTts(text: String, utteranceId: String, button: ImageView) {

        // If the same button is clicked while playing → stop it
        if (textToSpeech.isSpeaking && currentUtteranceId == utteranceId) {
            textToSpeech.stop()
            currentUtteranceId = null

            button.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_speaker)
            )
            currentPlayingButton = null
            return
        }

        // 🔹 If another button was playing → reset it
        currentPlayingButton?.let {
            it.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_speaker)
            )
        }

        // 🔹 Stop any existing speech
        textToSpeech.stop()

        // 🔹 Start new speech
        currentUtteranceId = utteranceId
        currentPlayingButton = button

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId
        )

        button.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_pause)
        )
    }

    private fun initViewModel() {
        val repo = PredictRepository(this)
        val factory = MainViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun validateFeedbackAndSubmit() {
        val feedbackString = binding.edtFeedBack.text.toString()

        if (feedbackString.isEmpty()) {
            showToast("Please enter your feedback")
            return
        }
        viewModel.submitFeedback(this, predictRespStoredId, feedbackString)
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun stopTtsAndNavigate() {
        if (::textToSpeech.isInitialized && textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        startActivity(Intent(this@ResultActivity, PestIdentificationActivity::class.java))
        finish()
    }

    override fun onPause() {
        super.onPause()

        // Stop TTS when app goes to background
        if (::textToSpeech.isInitialized && textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }

        // Reset UI state
        currentUtteranceId = null
        currentPlayingButton?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_speaker)
        )
        currentPlayingButton = null
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}