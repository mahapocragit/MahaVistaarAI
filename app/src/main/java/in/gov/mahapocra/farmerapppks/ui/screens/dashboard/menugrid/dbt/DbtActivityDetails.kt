package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.dbt

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivityGrpDetailsAdapter
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivityGrpEligibilityAdapter
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivityGrpNotesAdapter
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivityGrpRequiredDocAdapter
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivitySubsidy
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class DbtActivityDetails : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {
    private lateinit var activityID: String
    private lateinit var activityName: String

    private var poCRADBTActivityJSONArray: JSONArray? = null
    private var requiredDocumentsJSONArray: JSONArray? = null
    private var eligibilityCriteriaJSONArray: JSONArray? = null
    private var notesJSONArray: JSONArray? = null
    private var subsidyCriteriaJSONArray: JSONArray? = null

    private var recyclerViewPocraDBT: RecyclerView? = null
    private var recyclerViewRequiredDocuments: RecyclerView? = null
    private var recyclerViewEligibility: RecyclerView? = null
    private var recyclerViewNotes: RecyclerView? = null

    private var textviewPocraDBT: TextView? = null
    private var textviewRequiredDocuments: TextView? = null
    private var textviewEligibility: TextView? = null
    private var textviewNotes: TextView? = null
    private var textGrpActivityName: TextView? = null

    private var cardView1: CardView? = null
    private var cardView2: CardView? = null
    private var cardView3: CardView? = null
    private var cardView4: CardView? = null

    private var linlayout1: LinearLayout? = null
    private var linlayout2: LinearLayout? = null
    private var linlayout3: LinearLayout? = null
    private var linlayout4: LinearLayout? = null

    private var textViewHeaderTitle: TextView? = null
    private var imageMenushow: ImageView? = null
    var languageToLoad: String? = null

    private lateinit var imageBackArrow: ImageView

    private lateinit var imgViewup1: ImageView
    private lateinit var imgViewup2: ImageView
    private lateinit var imgViewup3: ImageView
    private lateinit var imgViewup4: ImageView

    private lateinit var imgViewDown1: ImageView
    private lateinit var imgViewDown2: ImageView
    private lateinit var imgViewDown3: ImageView
    private lateinit var imgViewDown4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this@DbtActivityDetails).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        } else {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_dbt_details)
        init()

        imageMenushow?.visibility = View.VISIBLE
        textViewHeaderTitle?.setText(R.string.pocra_dbt_activity)

        activityID = intent.getStringExtra("ActivityGroupID").toString()
        activityName = intent.getStringExtra("mActivityName").toString()

        textGrpActivityName?.text = activityName

        imageMenushow?.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        dbtGrpActivityDetails()
        imageMenushow?.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        linlayout1?.setOnClickListener {
            cardView1?.visibility = View.VISIBLE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
        }
        linlayout2?.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.VISIBLE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
        }
        linlayout3?.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.VISIBLE
            cardView4?.visibility = View.GONE
        }
        linlayout4?.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.VISIBLE
        }
        imgViewDown1.setOnClickListener {
            cardView1?.visibility = View.VISIBLE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
            imgViewDown1.visibility = View.GONE
            imgViewup1.visibility = View.VISIBLE
        }
        imgViewup1.setOnClickListener {
            cardView1?.visibility = View.GONE
            imgViewDown1.visibility = View.VISIBLE
            imgViewup1.visibility = View.GONE
        }
        imgViewDown2.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.VISIBLE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
            imgViewDown2.visibility = View.GONE
            imgViewup2.visibility = View.VISIBLE
        }
        imgViewup2.setOnClickListener {
            cardView2?.visibility = View.GONE
            imgViewDown2.visibility = View.VISIBLE
            imgViewup2.visibility = View.GONE
        }
        imgViewDown3.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.VISIBLE
            cardView4?.visibility = View.GONE
            imgViewDown3.visibility = View.GONE
            imgViewup3.visibility = View.VISIBLE
        }
        imgViewup3.setOnClickListener {
            cardView3?.visibility = View.GONE
            imgViewDown3.visibility = View.VISIBLE
            imgViewup3.visibility = View.GONE
        }
        imgViewDown4.setOnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.VISIBLE
            imgViewDown4.visibility = View.GONE
            imgViewup4.visibility = View.VISIBLE
        }
        imgViewup4.setOnClickListener {
            cardView4?.visibility = View.GONE
            imgViewDown4.visibility = View.VISIBLE
            imgViewup4.visibility = View.GONE
        }
    }

    private fun init() {
        textviewPocraDBT = findViewById(R.id.textviewPocraDBT)
        textviewRequiredDocuments = findViewById(R.id.textviewRequiredDocuments)
        textviewEligibility = findViewById(R.id.textviewEligibility)
        textviewNotes = findViewById(R.id.textviewNotes)
        textGrpActivityName = findViewById(R.id.text_grp_activity_name)

        recyclerViewPocraDBT = findViewById(R.id.recyclerViewPocraDBT)
        recyclerViewRequiredDocuments = findViewById(R.id.recyclerViewRequiredDocuments)
        recyclerViewEligibility = findViewById(R.id.recyclerViewEligibility)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)

        cardView1 = findViewById(R.id.cardview1)
        cardView2 = findViewById(R.id.cardview2)
        cardView3 = findViewById(R.id.cardview3)
        cardView4 = findViewById(R.id.cardview4)

        linlayout1 = findViewById(R.id.linlayout1)
        linlayout2 = findViewById(R.id.linlayout2)
        linlayout3 = findViewById(R.id.linlayout3)
        linlayout4 = findViewById(R.id.linlayout4)
        imgViewup1 = findViewById(R.id.imgViewup1)
        imgViewDown1 = findViewById(R.id.imgViewDown1)
        imgViewup2 = findViewById(R.id.imgViewup2)
        imgViewDown2 = findViewById(R.id.imgViewDown2)
        imgViewup3 = findViewById(R.id.imgViewup3)
        imgViewDown3 = findViewById(R.id.imgViewDown3)
        imgViewup4 = findViewById(R.id.imgViewup4)
        imgViewDown4 = findViewById(R.id.imgViewDown4)
        // dbtActivityGrp.setHasFixedSize(false)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageBackArrow = findViewById(R.id.imgBackArrow)

    }

    private fun dbtGrpActivityDetails() {
        try {
            val secreateKey: String = APIServices.SSO_KEY
            val lang = "mr"
            val data = "No"

            val api =
                AppInventorApi(
                    this,
                    APIServices.DBT,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> =
                apiRequest.getDbtActivitiesGrpDetails(secreateKey, lang, data, activityID)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun dbtGrpActivityDocDetails() {
        try {
            val secreateKey: String = APIServices.SSO_KEY
            //val lang: String = "mr"
            val data = "No"


            val api =
                AppInventorApi(
                    this,
                    APIServices.DBT,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDbtActivitiesGrpDocDetails(
                secreateKey,
                languageToLoad,
                data,
                activityID
            )
            api.postRequest(responseCall, this, 2)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            Log.d("jSONObject121212", jSONObject.toString())
            if (response.status) {
                poCRADBTActivityJSONArray = response.getActivityGrpDetailsArray()
                Log.d("warehouseAvailability", poCRADBTActivityJSONArray.toString())

                // if (activityGrpWiseDetailsJSONArray?.length()!! > 0) {
                val adaptorDbtActivityGrp =
                    BbtActivityGrpDetailsAdapter(
                        this,
                        this,
                        poCRADBTActivityJSONArray
                    )
                recyclerViewPocraDBT?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                recyclerViewPocraDBT?.setAdapter(adaptorDbtActivityGrp)
                adaptorDbtActivityGrp.notifyDataSetChanged()
                // }
                dbtGrpActivityDocDetails()

            } else {
                // UIToastMessage.show(this, response.response)
            }
        }


        if (i == 2 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {

                requiredDocumentsJSONArray = response.getActivityGrpreqrDocArray()
                eligibilityCriteriaJSONArray = response.getActivityGrpEligibiltyArray()
                notesJSONArray = response.getActivityGrpNoteArray()

                Log.d("requiredDocuments", requiredDocumentsJSONArray.toString())
                Log.d("eligibilityCriteria", eligibilityCriteriaJSONArray.toString())
                Log.d("notesJSONArray", notesJSONArray.toString())


                //  if (requiredDocumentsJSONArray?.length()!! > 0) {
                val adaptorDbtActivityGrpRequiredDocuments =
                    BbtActivityGrpRequiredDocAdapter(this, requiredDocumentsJSONArray)
                recyclerViewRequiredDocuments?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                recyclerViewRequiredDocuments?.setAdapter(adaptorDbtActivityGrpRequiredDocuments)
                adaptorDbtActivityGrpRequiredDocuments.notifyDataSetChanged()
                //   }

                // if (eligibilityCriteriaJSONArray?.length()!! > 0) {
                val adaptorDbtActivityGrpElig =
                    BbtActivityGrpEligibilityAdapter(this, eligibilityCriteriaJSONArray)
                recyclerViewEligibility?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                recyclerViewEligibility?.setAdapter(adaptorDbtActivityGrpElig)
                adaptorDbtActivityGrpElig.notifyDataSetChanged()
                // }

                //  if (notesJSONArray?.length()!! > 0) {
                val adaptorDbtActivityGrpNote =
                    BbtActivityGrpNotesAdapter(this, notesJSONArray)
                recyclerViewNotes?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                recyclerViewNotes?.setAdapter(adaptorDbtActivityGrpNote)
                adaptorDbtActivityGrpNote.notifyDataSetChanged()
            }


        } else {
            // UIToastMessage.show(this, response.response)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        DebugLog.getInstance().d("onMultiRecyclerViewItemClick=$jsonObject")
        if (i == 1) {
            showSubsidyDialog(jsonObject)
        }
    }

    private fun showSubsidyDialog(jsonObject: JSONObject) {
        subsidyCriteriaJSONArray = jsonObject.getJSONArray("SubsidyDetails")
        val activityName: String = jsonObject.getString("ActivityName")

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.subsidy_list_dialog)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        val dialogRecyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewDbtSubsidy)
        dialogTitle.text = activityName

        cancelButton.setOnClickListener { dialog.dismiss() }

        val adaptorDbtSubsidy =
            BbtActivitySubsidy(
                this,
                this,
                subsidyCriteriaJSONArray
            )
        dialogRecyclerView.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        dialogRecyclerView!!.adapter = adaptorDbtSubsidy
        adaptorDbtSubsidy.notifyDataSetChanged()

        dialog.show()
    }


}



