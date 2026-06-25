package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChangePwdTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.isStrongPassword
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONObject

class ConfirmPassword : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityChangePwdTempBinding
    private lateinit var newPwd: String
    private lateinit var confirmPwd: String
    private lateinit var userMobileNo: String
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ConfirmPassword)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        switchLanguage(this, languageToLoad)
        binding = ActivityChangePwdTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        userMobileNo = intent.getStringExtra("MobileNo").toString()
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
        observeResponse()
        onClick()
        binding.newPasswordEditText.addTextChangedListener(passwordWatcher)
        binding.confirmPasswordEditText.addTextChangedListener(confirmPasswordWatcher)
    }

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val password = s.toString()
            if (!isValidPassword(password)) {
                binding.passwordErrorTextView.text =
                    "Password must be 8+ chars, include uppercase, lowercase, number, and special character."
                binding.passwordErrorTextView.visibility = TextView.VISIBLE
            } else {
                binding.passwordErrorTextView.visibility = TextView.GONE
            }

            // Also check if passwords match when typing
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            if (confirmPassword.isNotEmpty()) {
                checkPasswordsMatch(password, confirmPassword)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val confirmPasswordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val password = binding.newPasswordEditText.text.toString()
            val confirmPassword = s.toString()
            checkPasswordsMatch(password, confirmPassword)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkPasswordsMatch(password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            binding.passwordTextInput.error = "Passwords do not match"
        } else {
            binding.passwordTextInput.error = null
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun onClick() {
        binding.backPressIcon.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
        binding.submitButton.setOnClickListener {
            requestDataValidation()
        }
    }

    private fun requestDataValidation() {
        newPwd = binding.newPasswordEditText.text.toString()
        confirmPwd = binding.confirmPasswordEditText.text.toString()
        if (newPwd.isEmpty()) {
            binding.newPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            binding.newPasswordEditText.requestFocus()
        } else if (confirmPwd.isEmpty()) {
            binding.confirmPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            binding.confirmPasswordEditText.requestFocus()
        } else if (newPwd != confirmPwd) {
            binding.confirmPasswordEditText.error =
                resources.getString(R.string.pass_equals_confirmpass)
            binding.confirmPasswordEditText.requestFocus()
        } else if (!isStrongPassword(binding.confirmPasswordEditText.text.toString())) {
            binding.passwordErrorTextView.visibility = View.VISIBLE
            UIToastMessage.show(this, resources.getString(R.string.weak_password))
        } else {
            authViewModel.resetPassword(userMobileNo, newPwd)
        }
    }

    fun observeResponse(){
        authViewModel.resetPasswordResponse.observe(this){ state ->
            when(state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }
                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jSONObject = JSONObject(state.data.toString())
                    val response =
                        ResponseModel(
                            jSONObject
                        )
                    if (response.getStatus()) {
                        val notificationCountValue: String = jSONObject.getString("response")
                        Toast.makeText(this, notificationCountValue, Toast.LENGTH_LONG).show();
                        val farmerId =
                            AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
                        if (farmerId != 0) {
                            AppHelper(this).redirectToHome()
                        } else {
                            startActivity(Intent(this, LoginScreen::class.java))
                        }
                    } else {
                        val notificationCountValue: String = jSONObject.getString("response")
                        Toast.makeText(this, notificationCountValue, Toast.LENGTH_LONG).show();
                    }
                }
                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
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