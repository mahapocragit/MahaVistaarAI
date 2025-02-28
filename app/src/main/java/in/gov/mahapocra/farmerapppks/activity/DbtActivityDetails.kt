package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.*
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class DbtActivityDetails : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {
        private lateinit var activityID:String
        private lateinit var activityName:String

    private var poCRADBTActivityJSONArray: JSONArray? = null
    private var requiredDocumentsJSONArray: JSONArray? = null
    private var eligibilityCriteriaJSONArray: JSONArray? = null
    private var notesJSONArray: JSONArray? = null
    private var subsideyCriteriaJSONArray: JSONArray? = null

    private var recyclerViewPocraDBT: RecyclerView? = null
    private var recyclerViewRequiredDocuments: RecyclerView? = null
    private var recyclerViewEligibility: RecyclerView? = null
    private var recyclerViewNotes: RecyclerView? = null

    private var textviewPocraDBT: TextView? = null
    private var textviewRequiredDocuments: TextView? = null
    private var textviewEligibility: TextView? = null
    private var textviewNotes: TextView? = null
    private var text_grp_activity_name: TextView? = null

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

    lateinit var imageBackArrow: ImageView

    lateinit var imgViewup1: ImageView
    lateinit var imgViewup2: ImageView
    lateinit var imgViewup3: ImageView
    lateinit var imgViewup4: ImageView

    lateinit var imgViewDown1: ImageView
    lateinit var imgViewDown2: ImageView
    lateinit var imgViewDown3: ImageView
    lateinit var imgViewDown4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this@DbtActivityDetails).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        }else{
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_dbt_details)
        init()

        imageMenushow?.visibility = View.VISIBLE
        textViewHeaderTitle?.setText(R.string.pocra_dbt_activity)

        activityID = intent.getStringExtra("ActivityGroupID").toString()
        activityName = intent.getStringExtra("mActivityName").toString()

        text_grp_activity_name?.text = activityName

        imageMenushow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })
        dbtGrpActivityDetails()
        imageMenushow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })
        linlayout1?.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.VISIBLE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
        })
        linlayout2?.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.VISIBLE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
        })
        linlayout3?.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.VISIBLE
            cardView4?.visibility = View.GONE
        })
        linlayout4?.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.VISIBLE
        })
        imgViewDown1.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.VISIBLE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
            imgViewDown1?.visibility = View.GONE
            imgViewup1?.visibility = View.VISIBLE
        })
        imgViewup1.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            imgViewDown1?.visibility = View.VISIBLE
            imgViewup1?.visibility = View.GONE
        })
        imgViewDown2.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.VISIBLE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.GONE
            imgViewDown2?.visibility = View.GONE
            imgViewup2?.visibility = View.VISIBLE
        })
        imgViewup2.setOnClickListener(View.OnClickListener {
            cardView2?.visibility = View.GONE
            imgViewDown2?.visibility = View.VISIBLE
            imgViewup2?.visibility = View.GONE
        })
        imgViewDown3.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.VISIBLE
            cardView4?.visibility = View.GONE
            imgViewDown3?.visibility = View.GONE
            imgViewup3?.visibility = View.VISIBLE
        })
        imgViewup3.setOnClickListener(View.OnClickListener {
            cardView3?.visibility = View.GONE
            imgViewDown3?.visibility = View.VISIBLE
            imgViewup3?.visibility = View.GONE
        })
        imgViewDown4.setOnClickListener(View.OnClickListener {
            cardView1?.visibility = View.GONE
            cardView2?.visibility = View.GONE
            cardView3?.visibility = View.GONE
            cardView4?.visibility = View.VISIBLE
            imgViewDown4?.visibility = View.GONE
            imgViewup4?.visibility = View.VISIBLE
        })
        imgViewup4.setOnClickListener(View.OnClickListener {
            cardView4?.visibility = View.GONE
            imgViewDown4?.visibility = View.VISIBLE
            imgViewup4?.visibility = View.GONE
        })
    }

    private fun init() {
        textviewPocraDBT = findViewById(R.id.textviewPocraDBT)
        textviewRequiredDocuments = findViewById(R.id.textviewRequiredDocuments)
        textviewEligibility = findViewById(R.id.textviewEligibility)
        textviewNotes = findViewById(R.id.textviewNotes)
        text_grp_activity_name = findViewById(R.id.text_grp_activity_name)

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
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
        imageMenushow=findViewById(R.id.imageMenushow)
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
        imageBackArrow=findViewById(R.id.imgBackArrow)

    }

    private fun dbtGrpActivityDetails() {
        try {
            val secreateKey: String = APIServices.SSO_KEY
            val lang: String = "mr"
            val data: String = "No"

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
            val responseCall: Call<JsonObject> = apiRequest.getDbtActivitiesGrpDetails(secreateKey, lang, data,activityID)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun dbtGrpActivityDocDetails() {
        try {
            val secreateKey: String = APIServices.SSO_KEY
            //val lang: String = "mr"
            val data: String = "No"


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
            val responseCall: Call<JsonObject> = apiRequest.getDbtActivitiesGrpDocDetails(secreateKey, languageToLoad, data,activityID)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 2)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(jSONObject)
            Log.d("jSONObject121212", jSONObject.toString())
            if (response.status) {
                poCRADBTActivityJSONArray = response.getActivityGrpDetailsArray()
                Log.d("warehouseAvailability",poCRADBTActivityJSONArray.toString())

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
                ResponseModel(jSONObject)
            if (response.status) {

                requiredDocumentsJSONArray = response.getActivityGrpreqrDocArray()
                eligibilityCriteriaJSONArray = response.getActivityGrpEligibiltyArray()
                notesJSONArray = response.getActivityGrpNoteArray()

                Log.d("requiredDocuments", requiredDocumentsJSONArray.toString())
                Log.d("eligibilityCriteria", eligibilityCriteriaJSONArray.toString())
                Log.d("notesJSONArray", notesJSONArray.toString())


               //  if (requiredDocumentsJSONArray?.length()!! > 0) {
                val adaptorDbtActivityGrpReqirdDoc =
                    BbtActivityGrpRequiredDocAdapter(this, requiredDocumentsJSONArray)
                     recyclerViewRequiredDocuments?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                     recyclerViewRequiredDocuments?.setAdapter(adaptorDbtActivityGrpReqirdDoc)
                     adaptorDbtActivityGrpReqirdDoc.notifyDataSetChanged()
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
        if (i==1){
            showSubsideyDialog(jsonObject)
        }
    }

    private fun showSubsideyDialog(jsonObject: JSONObject) {
        subsideyCriteriaJSONArray = jsonObject.getJSONArray("SubsidyDetails")
        var activityName: String= jsonObject.getString("ActivityName")

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

        cancelButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        val adaptorDbtSubsidy =
            BbtActivitySubsidy(
                this,
                this,
                subsideyCriteriaJSONArray
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



