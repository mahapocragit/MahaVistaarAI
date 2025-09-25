package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityClimateResilintTechnologyBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ClimateResilientTechnologyAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONObject

class ClimateResilientTechnology : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private var climateResilientGroup: RecyclerView? = null
    private lateinit var binding: ActivityClimateResilintTechnologyBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var textViewHeaderTitle: TextView? = null
    private var imgBackArrow: ImageView? = null
    private lateinit var languageToLoad: String
    private var resilientGrpWiseDetailsJSONArray: JSONArray? = null
    private lateinit var resilientCRAGroupJSONArray: JSONArray

    private var groupName: ArrayList<String> = ArrayList()
    private var groupImagePath: ArrayList<String> = ArrayList()
    private var webUrl: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        } else {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityClimateResilintTechnologyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        init()
        textViewHeaderTitle?.setText(R.string.climateTechnology)
        textViewHeaderTitle?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        imgBackArrow?.visibility = View.VISIBLE
        imgBackArrow?.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.climateResilientGroupList(this, languageToLoad)
        } else {
            Snackbar.make(binding.root, "Internet not available", Snackbar.LENGTH_SHORT).show()
        }

        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)

        val isGuest =
            AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnClickListener {
            if (!isGuest) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                AlertDialog.Builder(this)
                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        // Handle login action here
                        startActivity(Intent(this, LoginScreen::class.java).apply {
                            putExtra("from", "dashboard")
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ClimateResilientTechnology, DashboardScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })
    }

    private fun init() {
        climateResilientGroup = findViewById(R.id.climateResilientRecyclerView)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)
    }

    private fun observeClimateResilientGroupList() {
        farmerViewModel.getClimateResilientListResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    resilientGrpWiseDetailsJSONArray = response.getResilientyGrpArray()
                    val adaptorResilientTechnologyGrp =
                        ClimateResilientTechnologyAdapter(
                            this,
                            this,
                            resilientGrpWiseDetailsJSONArray
                        )
                    climateResilientGroup?.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    climateResilientGroup?.setAdapter(adaptorResilientTechnologyGrp)
                    adaptorResilientTechnologyGrp.notifyDataSetChanged()
                }
            }
        }
        farmerViewModel.error.observe(this) {
            Log.d("TAGGER", "observeClimateResilientGroupList: $it")
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        resilientCRAGroupJSONArray = jsonObject.getJSONArray("CRAGroups")
        val craGroppLength: Int = resilientCRAGroupJSONArray.length()
        for (i in 0 until craGroppLength) {
            val userDetail = resilientCRAGroupJSONArray.getJSONObject(i)
            groupName.add(userDetail.getString("GroupName"))
            groupImagePath.add(userDetail.getString("groupimagepath"))
            webUrl.add(userDetail.getString("WbUrl"))
        }
        val intent = Intent(this, ClimateDetailsGrid::class.java)
        val b = Bundle()
        b.putSerializable("craGroppLength", craGroppLength)
        b.putSerializable("GroupName", groupName)
        b.putSerializable("GroupImagePath", groupImagePath)
        b.putSerializable("WebUrl", webUrl)
        intent.putExtras(b)
        startActivity(intent)
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