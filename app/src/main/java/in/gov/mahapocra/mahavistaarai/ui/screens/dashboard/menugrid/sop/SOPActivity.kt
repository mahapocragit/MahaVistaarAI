package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivitySopactivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.SOPAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.abs

class SOPActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivitySopactivityBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    var cropId: Int? = 0
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""
    private lateinit var languageToLoad: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SOPActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivitySopactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")

        binding.sowingInfoLayout.sowingDateTextView.visibility = View.GONE
        binding.sowingInfoLayout.editSowingDateIcon.visibility = View.GONE
        binding.sowingInfoLayout.textView7.text = getString(R.string.selected_crop)
        binding.sowingInfoLayout.cropNameTextView.text = cropName

        cropId?.let { farmerViewModel.fetchSOPDate(this, it) }
        ProgressHelper.showProgressDialog(this)
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f // reset alpha in case you show it again
                }
                .start()
        }

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.SOP_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DashboardScreen::class.java
                )
            )
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.sop_title)

        farmerViewModel.sopResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataArray = jsonObject.getJSONArray("data")
                if (dataArray.length() != 0) {
                    binding.sopRecyclerView.visibility = View.VISIBLE
                    binding.noDataFoundImageView.visibility = View.GONE
                    binding.sopTextView.visibility = View.GONE
                    binding.sopRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.sopRecyclerView.adapter = SOPAdapter(this, this, dataArray)
                } else {
                    binding.sopRecyclerView.visibility = View.GONE
                    binding.noDataFoundImageView.visibility = View.VISIBLE
                    binding.sopTextView.visibility = View.VISIBLE
                }
            }
        }

        farmerViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            binding.sopRecyclerView.visibility = View.GONE
            binding.noDataFoundImageView.visibility = View.VISIBLE
            binding.sopTextView.visibility = View.VISIBLE
        }
        val isGuest =
            AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)

        binding.chatbotIcon.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f
            private var startX = 0f
            private var startY = 0f
            private val CLICK_THRESHOLD = 20 // px movement allowed

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        startX = event.rawX
                        startY = event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val parent = v.parent as View
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY

                        // calculate boundaries (you can adjust margin if needed)
                        val margin = 32 // px margin from edges
                        val maxX = parent.width - v.width - margin
                        val maxY = parent.height - v.height - margin
                        val minX = margin
                        val minY = margin

                        // constrain movement inside screen
                        val boundedX = newX.coerceIn(minX.toFloat(), maxX.toFloat())
                        val boundedY = newY.coerceIn(minY.toFloat(), maxY.toFloat())

                        v.animate()
                            .x(boundedX)
                            .y(boundedY)
                            .setDuration(0)
                            .start()
                    }

                    MotionEvent.ACTION_UP -> {
                        val diffX = abs(event.rawX - startX)
                        val diffY = abs(event.rawY - startY)

                        if (diffX < CLICK_THRESHOLD && diffY < CLICK_THRESHOLD) {
                            if (!isGuest) {
                                startActivity(Intent(this@SOPActivity, ChatbotActivity::class.java))
                            } else {
                                AlertDialog.Builder(this@SOPActivity)
                                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                                    .setPositiveButton(R.string.yes) { dialog, _ ->
                                        startActivity(Intent(this@SOPActivity, LoginScreen::class.java).apply {
                                            putExtra("from", "dashboard")
                                        })
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }
                    }
                }
                return true
            }
        })

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SOPActivity, DashboardScreen::class.java))
            }
        })
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

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 2) {
            val jsonObject = obj as JSONObject
            val webViewUrl = jsonObject.optString("url")
            val intent = Intent(this, SOPWebViewActivity::class.java)
            val b = Bundle()
            b.putSerializable("webUrl", webViewUrl)
            intent.putExtras(b)
            startActivity(intent)
        }
    }
}