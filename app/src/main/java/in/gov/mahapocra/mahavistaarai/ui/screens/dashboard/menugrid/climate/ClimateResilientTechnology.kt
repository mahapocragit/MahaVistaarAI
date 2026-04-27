package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityClimateResilintTechnologyBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ClimateResilientTechnologyAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ClimateResilientTechnology : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivityClimateResilintTechnologyBinding
    private val farmerViewModel: FarmerViewModel by viewModels()

    private lateinit var languageToLoad: String
    private var resilientGrpWiseDetailsJSONArray: JSONArray? = null

    private val groupName = arrayListOf<String>()
    private val groupImagePath = arrayListOf<String>()
    private val webUrl = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLanguage()

        binding = ActivityClimateResilintTechnologyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiResponsive(binding.root)
        setupToolbar()
        setupBubbleAnimation()
        observeClimateResilientGroupList()
        setupChatbotDrag()

        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.climateResilientGroupList(this, languageToLoad)
        } else {
            Snackbar.make(binding.root, "Internet not available", Snackbar.LENGTH_SHORT).show()
        }

        setupBackPressed()
    }

    private fun setupLanguage() {
        languageToLoad = if (AppSettings.getLanguage(this).equals("2", true)) "mr" else "en"
        switchLanguage(this, languageToLoad)
    }

    private fun setupToolbar() {
        binding.relativeLayoutTopBar.textViewHeaderTitle.apply {
            setText(R.string.climateTechnology)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }

        binding.relativeLayoutTopBar.imgBackArrow.apply {
            visibility = View.VISIBLE
            setOnClickListener { AppHelper(this@ClimateResilientTechnology).redirectToHome() }
        }
    }

    private fun setupBubbleAnimation() {
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)

        lifecycleScope.launch {
            delay(5000)
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f
                }
        }
    }

    private fun observeClimateResilientGroupList() {
        farmerViewModel.getClimateResilientListResponse.observe(this) { response ->
            response ?: return@observe

            val jsonObject = JSONObject(response.toString())
            val responseModel = ResponseModel(jsonObject)

            if (responseModel.getStatus()) {
                resilientGrpWiseDetailsJSONArray = responseModel.getResilientyGrpArray()

                val adapter = ClimateResilientTechnologyAdapter(
                    this,
                    this,
                    resilientGrpWiseDetailsJSONArray
                )

                binding.climateResilientRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ClimateResilientTechnology)
                    this.adapter = adapter
                }
            }
        }

        farmerViewModel.error.observe(this) {
            Log.e(TAG, it)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {

        groupName.clear()
        groupImagePath.clear()
        webUrl.clear()

        val jsonObject = obj as JSONObject
        val craGroups = jsonObject.getJSONArray("CRAGroups")

        for (index in 0 until craGroups.length()) {
            val item = craGroups.getJSONObject(index)

            groupName.add(item.optString("GroupName"))
            groupImagePath.add(item.optString("groupimagepath"))
            webUrl.add(item.optString("WbUrl"))
        }

        startActivity(
            Intent(this, ClimateDetailsGrid::class.java).apply {
                putStringArrayListExtra("GroupName", groupName)
                putStringArrayListExtra("GroupImagePath", groupImagePath)
                putStringArrayListExtra("WebUrl", webUrl)
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupChatbotDrag() {
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }
        )
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper(this@ClimateResilientTechnology).redirectToHome()
            }
        })
    }

    override fun attachBaseContext(newBase: Context) {
        val language = if (AppSettings.getLanguage(newBase).equals("1", true)) "en" else "mr"
        super.attachBaseContext(configureLocale(newBase, language))
    }
}