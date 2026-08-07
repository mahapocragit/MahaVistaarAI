package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Returns a click handler that launches the device's speech-to-text UI
 * (Android's own mic dialog provides the "listening" feedback, so callers
 * don't need to render their own loading/spinner state) and forwards the
 * recognized text to [onResult].
 */
@Composable
fun rememberVoiceInputLauncher(languageTag: String? = null, onResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!text.isNullOrBlank()) onResult(text.spokenDigitWordsToNumber().toEnglishDigits())
        }
    }
    return {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag ?: Locale.getDefault().toLanguageTag())
        }
        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Speech input is not available on this device", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Marathi (and Hindi) speech recognition returns digits in Devanagari
 * script (०-९); normalize them to plain ASCII digits so downstream
 * fields (mobile number, Aadhaar, OTP, etc.) get usable numbers.
 */
private fun String.toEnglishDigits(): String = map { char ->
    if (char in '०'..'९') '0' + (char - '०') else char
}.joinToString("")

/** Single-digit number words as transcribed by the Marathi/Hindi speech models. */
private val DIGIT_WORDS = mapOf(
    "शून्य" to '0', "सुन्य" to '0',
    "एक" to '1',
    "दोन" to '2', "दो" to '2',
    "तीन" to '3',
    "चार" to '4',
    "पाच" to '5', "पांच" to '5', "पाँच" to '5',
    "सहा" to '6', "छह" to '6', "छः" to '6',
    "सात" to '7',
    "आठ" to '8',
    "नऊ" to '9', "नौ" to '9'
)

/**
 * Users reading out a long number (e.g. an Aadhaar number) say each digit as a
 * word ("एक दोन तीन..." / "ek do tin...") rather than a numeral, and the speech
 * recognizer transcribes them as those words. Map each recognized digit word to
 * its numeral and, since the spoken digits belong to one number, join
 * consecutive digit words together without spaces; other words are left as-is.
 */
private fun String.spokenDigitWordsToNumber(): String {
    val words = trim().split(Regex("\\s+"))
    val result = StringBuilder()
    var prevWasDigitWord = false
    for (word in words) {
        val digit = DIGIT_WORDS[word.trim(',', '.', '।')]
        if (digit != null) {
            if (!prevWasDigitWord && result.isNotEmpty()) result.append(' ')
            result.append(digit)
            prevWasDigitWord = true
        } else {
            if (result.isNotEmpty()) result.append(' ')
            result.append(word)
            prevWasDigitWord = false
        }
    }
    return result.toString()
}
