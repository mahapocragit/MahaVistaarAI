package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPreRegistrationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.RegistrationViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONObject

class PreRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreRegistrationBinding
    private val registrationViewModel: RegistrationViewModel by viewModels()
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var languageToLoad: String
    private lateinit var dialog: Dialog
    private var mobile: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PreRegistrationActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPreRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        observeResponse()
        setUpViews()
    }

    private fun observeResponse() {
        registrationViewModel.getOTPRegisterResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    Log.d(TAG, "onResponse: $jSONObject")
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    addVerificationDialog()
                } else if (jSONObject.optInt("status") == 201) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                } else if (jSONObject.optInt("status") == 429) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_LONG).show()
                }
            }
        }

        farmerViewModel.compareOtpResponseReg.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                if (jSONObject.optInt("status") == 200) {
                    startActivity(Intent(this, Registration::class.java).apply {
                        putExtra("name", name)
                        putExtra("mobile", mobile)
                    })
                } else {
                    dialog.dismiss()
                    UIToastMessage.show(this, getString(R.string.wrong_OTP))
                }
            }
        }
    }

    private fun setUpViews() {
        binding.backPressIcon.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
        }

        binding.submitButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            mobile = binding.mobNoEditText.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (mobile.isEmpty()) {
                Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendOTP(mobile)
        }
    }

    private fun sendOTP(mobile: String) {
        if (mobile.isEmpty()) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_err)
            binding.mobNoEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mobile)) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobNoEditText.requestFocus()
        } else {
            registrationViewModel.getOTPRegisterRequest(this, mobile.trim { it <= ' ' })
        }
    }

    private fun addVerificationDialog() {
        var countDownTimer: CountDownTimer? = null
        countDownTimer?.cancel()
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val countdownTextview = dialog.findViewById<TextView>(R.id.countdownTextview)
        countDownTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                countdownTextview.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                countdownTextview.text = "00:00"
            }
        }
        countDownTimer.start()

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = "Enter OTP"

        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
            val enteredOTP: String = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                farmerViewModel.compareOtpReg(
                    this,
                    binding.mobNoEditText.text.toString(),
                    enteredOTP
                )
            }
        }
        resendOTP.setOnClickListener {
            dialog.dismiss()
            sendOTP(mobile)
        }
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                dialog.show()
            }
        }
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