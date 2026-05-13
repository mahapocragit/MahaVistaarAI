package `in`.gov.mahapocra.mahavistaarai.ui.maps_sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vassar.mahanidan.embed.FarmSurveyDetail
import com.vassar.mahanidan.embed.MahanidanAuth
import com.vassar.mahanidan.embed.MahanidanCallback
import com.vassar.mahanidan.embed.MahanidanEmbed
import com.vassar.mahanidan.embed.MahanidanSession
import com.vassar.mahanidan.embed.MyFarmsLaunchRequest
import com.vassar.mahanidan.embed.PestDetectionLaunchRequest
import com.vassar.mahanidan.embed.PestForewarningLaunchRequest
import com.vassar.mahanidan.embed.VillageCropConditionLaunchRequest
import com.vassar.mahanidan.embed.WeatherLaunchRequest
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authClient = MahanidanFarmerAuthClient()
    private var session: MahanidanSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginCheck()
//        binding.loginButton.setOnClickListener {  }
//        binding.weatherButton.setOnClickListener { launchWeather() }
//        binding.myFarmsButton.setOnClickListener { launchMyFarms() }
//        binding.villageCropConditionButton.setOnClickListener { launchVillageCropCondition() }
//        binding.pestDetectionButton.setOnClickListener { launchPestDetection() }
//        binding.pestForewarningButton.setOnClickListener { launchPestForewarning() }
    }

    private fun login() {
        val farmerId = binding.farmerIdInput.text?.toString()?.trim().orEmpty()
        val mpin = binding.mpinInput.text?.toString()?.trim().orEmpty()
        val environment = binding.environmentInput.text?.toString()?.trim().orEmpty().ifBlank { "prod" }

        if (farmerId.isBlank() || mpin.isBlank()) {
            toast("Farmer ID and MPIN are required")
            return
        }

        binding.loginButton.isEnabled = false
        binding.sessionStatus.text = "Logging in..."
        lifecycleScope.launch {
            runCatching {
                authClient.loginFarmer(farmerId, mpin, "prod")
            }.onSuccess { loginSession ->
                session = MahanidanSession(
                    token = loginSession.token,
                    refreshToken = loginSession.refreshToken,
                    userId = loginSession.userId,
                    loginType = 2,
                    environment = environment.lowercase(),
                    language = "marathi",//'hindi' 'marathi'
                    cropCatalog = cropCatalog,
                    farmerCrops = farmerCrops,
                )
                binding.sessionStatus.text = "Session ready for farmer $farmerId"
            }.onFailure { error ->
                session = null
                binding.sessionStatus.text = error.message ?: "Login failed"
            }
            binding.loginButton.isEnabled = true
        }
    }

    private fun launchWeather() {
        val currentSession = requireSession() ?: return
        MahanidanEmbed.launchWeather(this, WeatherLaunchRequest(currentSession), embedCallback)
    }

    private fun launchMyFarms() {
        val currentSession = requireSession() ?: return
        MahanidanEmbed.launchMyFarms(this, MyFarmsLaunchRequest(currentSession), embedCallback)
    }

    private fun launchVillageCropCondition() {
        val currentSession = requireSession() ?: return
        MahanidanEmbed.launchVillageCropCondition(
            this,
            VillageCropConditionLaunchRequest(
                session = currentSession,
                villageUUID = "",
            ),
            embedCallback,
        )
    }

    private fun loginCheck(){

        binding.loginButton.isEnabled = false
        binding.sessionStatus.text = "Logging in..."
        lifecycleScope.launch {
            runCatching {
                MahanidanAuth.loginMahavistaarUserAndSaveSession(
                    context = this@MainActivity,
                    userId = "5014313",
                    farmDetails = listOf(
                        FarmSurveyDetail(
                            villageCode = 536565 ?: 0L,
                            surveyNo = "16",
                        )
                    ),
                    environment = "prod"
                )
            }.onSuccess { loginSession ->
                session = loginSession
                launchVillageCropCondition()
            }.onFailure { error ->
                binding.sessionStatus.text = error.message ?: "Login failed"
            }
            binding.loginButton.isEnabled = true
        }
    }

    private fun launchPestDetection() {
        val currentSession = requireSession() ?: return
        MahanidanEmbed.launchPestDetection(this, PestDetectionLaunchRequest(currentSession), embedCallback)
    }

    private fun launchPestForewarning() {
        val currentSession = requireSession() ?: return
        MahanidanEmbed.launchPestForewarning(this, PestForewarningLaunchRequest(currentSession), embedCallback)
    }

    private fun requireSession(): MahanidanSession? {
        val currentSession = session
        if (currentSession == null) {
            toast("Login first to create a MahanidanSession")
        }
        return currentSession
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val embedCallback = object : MahanidanCallback {
        override fun onClosed(feature: String) {
            toast("Closed $feature")
        }

        override fun onAuthExpired() {
            session = null
            binding.sessionStatus.text = "Session expired. Login again."
        }
    }

    private companion object {
        val cropCatalog = mapOf(
            "Paddy" to "22c07b1f-c92d-4e56-9c8f-8328e0fdb2ae",
            "Cotton" to "3b8973e2-5a16-4d51-b94d-b4cb9ef3d4fb",
        )
        val farmerCrops = listOf("Paddy", "Cotton")
    }
}
