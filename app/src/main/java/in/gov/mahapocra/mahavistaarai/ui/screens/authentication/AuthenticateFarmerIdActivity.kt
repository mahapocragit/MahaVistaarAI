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
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAuthenticateFarmerIdBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LoginViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONObject

class AuthenticateFarmerIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticateFarmerIdBinding
    private val loginViewModel: LoginViewModel by viewModels()
    var languageToLoad = "mr"
    private var farmerId = ""
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (AppSettings.getLanguage(this@AuthenticateFarmerIdActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityAuthenticateFarmerIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        observeResponse()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.sendFarmerIdOTPButton.setOnClickListener {
            farmerId = binding.farmerIdEditText.text.toString()
            if (farmerId.isEmpty()) {
                binding.farmerIdEditText.error = "Please enter valid Farmer ID"
                return@setOnClickListener
            }
            loginViewModel.sendOtpToFarmerId(this, farmerId)
        }

        binding.backPressIcon.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        OnBackPressedDispatcher().addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@AuthenticateFarmerIdActivity, DashboardScreen::class.java))
            }
        })
    }

    private fun observeResponse() {

        var userDataJson = JSONObject()

        loginViewModel.sendOtpToFarmerIdResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                val response = jSONObject.optString("response")
                if (status == 200) {
                    userDataJson = jSONObject.optJSONObject("data")
                    addVerificationDialog()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginViewModel.compareOtpToFarmerIdResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d(TAG, "observe compareOtpToFarmerIdResponse: $jSONObject")
                val status = jSONObject.optInt("status")
                val response = jSONObject.optString("response")
                if (status == 200) {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, FarmerDetailsConfirmationActivity::class.java).apply {
                            putExtra("farmerId", farmerId)
                            putExtra("farmerFetchedData", userDataJson.toString())
                        }
                    )
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addVerificationDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = resources.getString(R.string.enterOtp)
        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {

            val enteredOTP = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                loginViewModel.compareOtpToFarmerId(this, farmerId, enteredOTP)
            }
        }

        resendOTP.setOnClickListener {
            dialog.dismiss()
            loginViewModel.sendOtpToFarmerId(this, farmerId)
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
                        this@AuthenticateFarmerIdActivity,
                        R.color.actionbar_color_figma
                    )
                )
                resendOTP.setTextColor(
                    ContextCompat.getColor(
                        this@AuthenticateFarmerIdActivity,
                        R.color.white
                    )
                )
            }
        }.start()
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