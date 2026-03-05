package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForgetPasswordTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.OtpRateLimiter.provideValidEncryptedString
import org.json.JSONObject

class ForgetPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordTempBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var mob: String
    private var timestamp: Long = 0
    private var userPass: String = ""
    private lateinit var dialog: Dialog
    var languageToLoad: String = "mr"
    private lateinit var otpFields: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ForgetPassword)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        switchLanguage(this, languageToLoad)
        binding = ActivityForgetPasswordTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        observeResponse()
        onClick()

        val farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId != 0) {
            if (languageToLoad == "en") {
                binding.forgetHeadingText1.text = "Change"
                binding.forgetHeadingText2.text = "Password"
            } else {
                binding.forgetHeadingText1.text = "पासवर्ड"
                binding.forgetHeadingText2.text = "बदला"
            }
        }
    }

    private fun setupOtpInputs(otpFields: List<EditText>) {

        otpFields.forEachIndexed { index, editText ->

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    editText.text.isEmpty() &&
                    index > 0
                ) {
                    otpFields[index - 1].requestFocus()
                    otpFields[index - 1].setSelection(
                        otpFields[index - 1].text.length
                    )
                }
                false
            }
        }
    }

    private fun observeResponse() {

        authViewModel.sendOtpToMobileResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                val response = jSONObject.optString("response")
                if (status == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    addVerificationDialog()
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                }
            }
        }

        authViewModel.error.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.compareOtpResponse.observe(this) {
            if (it != null) {
                val calculatedResponse = provideValidEncryptedString(timestamp)
                val jSONObject = JSONObject(it.toString())
                val status = jSONObject.optInt("status")
                val response = jSONObject.optString("response")
                if (status == 200) {
                    if (calculatedResponse != response) {
                        userVerification()
                    } else {
                        Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                }
            }
        }

        authViewModel.error.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onClick() {
        binding.sendOTPButton.setOnClickListener {
            mob = binding.mobileEditText.text.toString()
            userPass = ""
            sendOTP()
        }

        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendOTP() {
        mob = binding.mobileEditText.text.toString()
        if (mob.isEmpty()) {
            binding.mobileEditText.error = resources.getString(R.string.login_mob_err)
            binding.mobileEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            binding.mobileEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobileEditText.requestFocus()
        } else {
            authViewModel.sendOtpToMobile(this, mob.trim { it <= ' ' })
        }
    }

    private fun addVerificationDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        val otpFields = listOf(
            dialog.findViewById<EditText>(R.id.otp1),
            dialog.findViewById<EditText>(R.id.otp2),
            dialog.findViewById<EditText>(R.id.otp3),
            dialog.findViewById<EditText>(R.id.otp4),
            dialog.findViewById<EditText>(R.id.otp5),
            dialog.findViewById<EditText>(R.id.otp6)
        )
        setupOtpInputs(otpFields)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = getString(R.string.enterOtp)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
            timestamp = System.currentTimeMillis()
            val enteredOTP = otpFields.joinToString("") { it.text.toString() }

            if (enteredOTP.length < 6) {
                Toast.makeText(this, "Enter valid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                authViewModel.compareOtp(
                    timestamp,
                    binding.mobileEditText.text.toString(),
                    enteredOTP
                )
                dialog.dismiss()
            }
        }

        resendOTP.setOnClickListener {
            dialog.dismiss()
            sendOTP()
        }
        dialog.show()
    }

    private fun otpVerification(resendOTP: Button) {
        // Disable and gray out the button before starting timer
        resendOTP.isEnabled = false
        resendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        resendOTP.setTextColor(ContextCompat.getColor(this, R.color.white))

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.text =
                    resources.getString(R.string.Time) + ":" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                // Enable and reset button appearance
                resendOTP.isEnabled = true
                resendOTP.text = resources.getString(R.string.Resend_OTP)
                resendOTP.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ForgetPassword,
                        R.color.actionbar_color_figma
                    )
                )
                resendOTP.setTextColor(ContextCompat.getColor(this@ForgetPassword, R.color.white))
            }
        }.start()
    }

    private fun userVerification() {
        Toast.makeText(this, "Thank you...", Toast.LENGTH_LONG).show()
        val intent = Intent(this, ConfirmPassword::class.java)
        intent.putExtra("MobileNo", mob)
        startActivity(intent)
        dialog.dismiss()
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