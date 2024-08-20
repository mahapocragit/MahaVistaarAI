package `in`.gov.mahapocra.farmerapppks.fragments.advisory



import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.CropAdvisoryDetails
import `in`.gov.mahapocra.farmerapppks.activity.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.adapter.CropAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.fragments.CropAdvisoryFragment
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CropAdvisorySelection.newInstance] factory method to
 * create an instance of this fragment.
 */
class CropAdvisorySelection : Fragment(), ApiCallbackCode, AlertListEventListener,
    DatePickerRequestListener, OnMultiRecyclerItemClickListener {

    lateinit var textViewHeaderTitle: TextView

    private var recyclerViewCropList: RecyclerView? = null
    private var cropAdArray: JSONArray? = null
    var languageToLoad: String? = null

    lateinit var textViewDistrict: TextView
    lateinit var textViewTaluka: TextView
    lateinit var textViewVillage: TextView
    lateinit var textViewCrop: TextView
    lateinit var textViewSowingDate: TextView
    lateinit var nextButton: TextView

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var cropJSONArray: JSONArray? = null
    private var villJSONArray: JSONArray? = null

    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var districtName: String
    private var cropID: Int = 0
    lateinit var cropName: String
    var sowingDate: String = ""
    val date = Date()

    private var villageID: Int = 0
    lateinit var villageName: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_crop_advisory_selection, container, false)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(activity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        init(view)
        onClick()
        return view
    }


    private fun onClick() {
        textViewDistrict.setOnClickListener {
            showDistrict()
        }
        textViewTaluka.setOnClickListener {
            showTaluka()
        }
        textViewVillage.setOnClickListener {
            showVillage()
        }
        textViewCrop.setOnClickListener {
            showCrop()
        }
        textViewSowingDate.setOnClickListener {
            AppUtility.getInstance().showDisabledFutureDatePicker(activity, date, 1, this)
        }
        nextButton.setOnClickListener { view ->
            val fragmentTransaction = getParentFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, CropAdvisoryFragment(), "advisory")
            fragmentTransaction.addToBackStack("advisory")
            fragmentTransaction.commit()
        }
    }

    private fun init(view:View) {
        textViewDistrict = view.findViewById(R.id.textViewDistrict)
        textViewTaluka = view.findViewById(R.id.textViewTaluka)
        textViewCrop = view.findViewById(R.id.textViewCrop)
        textViewVillage = view.findViewById(R.id.textViewVillage)
        textViewSowingDate = view.findViewById(R.id.textViewSowingDate)
        nextButton = view.findViewById(R.id.nextButton)

        try {
            view.isFocusableInTouchMode = true
            view.requestFocus()
            view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    val i = Intent(activity, DashboardScreen::class.java)
                    startActivity(i)
                    (activity as Activity?)!!.overridePendingTransition(0, 0)
                    return@OnKeyListener true
                }
                false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getDistrictData()


    }

    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
            //jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(activity, APIServices.SSO, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun fetchTalukaMasterData() {
        val jsonObject = JSONObject()
        try {
            // jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_id", districtID)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(activity, APIServices.SSO, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 2)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_id", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(activity, APIServices.SSO, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.kGetVillageList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 3)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun fetchCropMasterData() {
        try {
            val api =
                AppinventorApi(activity, APIServices.SSO, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropList()
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 4)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }



    private fun showDistrict() {
        if (districtJSONArray == null) {
            getDistrictData()
        } else {
            AppUtility.getInstance().showListDialogIndex(
                districtJSONArray,
                1,
                getString(R.string.farmer_select_district),
                "name",
                "id",
                activity,
                this
            )
        }
    }

    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                fetchTalukaMasterData()
            } else {
                UIToastMessage.show(
                    activity,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    talukaJSONArray,
                    2,
                    getString(R.string.farmer_select_taluka),
                    "name",
                    "id",
                    activity,
                    this
                )
        }
    }
    private fun showVillage() {
        if (villJSONArray == null) {
            if (talukaID > 0) {
               // showCrop()
                getVillageAgainstTaluka()
            } else {
                UIToastMessage.show(activity, resources.getString(R.string.error_farmer_select_taluka))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(villJSONArray, 3, getString(R.string.farmer_select_village), "name",
                    "id",activity, this)
        }
    }
    private fun showCrop() {
        if (cropJSONArray == null) {
            if (villageID > 0) {
                fetchCropMasterData()
            } else {
                UIToastMessage.show(
                    activity,
                    resources.getString(R.string.farmer_select_taluka)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    cropJSONArray,
                    4,
                    getString(R.string.select_crop),
                    "name",
                    "id",
                    activity,
                    this
                )
        }
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CropAdvisorySelection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                CropAdvisorySelection().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }



    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("Weather::districtArray", districtJSONArray.toString())
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("Weather::talukaArray", talukaJSONArray.toString())
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }


        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                villJSONArray = response.getdataArray()
                Log.d("villJSONArray", villJSONArray.toString())
            }
            else {
                Toast.makeText(activity, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 4 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                cropJSONArray = response.getdataArray()
                Log.d("Weather::talukaArray", cropJSONArray.toString())
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }

//        if (i == 3 && jSONObject != null) {
//            val response = ResponseModel(jSONObject)
//            if (response.status) {
//                cropAdArray = response.getdataArray()
//                Log.d("CropAdvisory=","cropjsonArray="+ cropAdArray.toString())
//                val adaptorDbtActivityGrp = CropAdvisoryAdapter(this, this, cropAdArray,languageToLoad)
//                recyclerViewCropList?.setLayoutManager(
//                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                )
//                recyclerViewCropList?.setAdapter(adaptorDbtActivityGrp)
//                adaptorDbtActivityGrp.notifyDataSetChanged()
//            } else {
//                UIToastMessage.show(this, response.response)
//            }
//        }
        if (i == 5) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    recyclerViewCropList?.setVisibility(View.VISIBLE)
                    cropAdArray = response.getdataArray()
                    Log.d("CropAdvisory=","cropjsonArray="+ cropAdArray.toString())

                    val adaptorDbtActivityGrp = CropAdvisoryAdapter(activity, this, cropAdArray,languageToLoad)
                    recyclerViewCropList?.setLayoutManager(
                        LinearLayoutManager(
                            activity,
                            LinearLayoutManager.VERTICAL,
                            false)
                    )
                    recyclerViewCropList?.setAdapter(adaptorDbtActivityGrp)
                    adaptorDbtActivityGrp.notifyDataSetChanged()
                }
                else {
                    recyclerViewCropList?.setVisibility(View.GONE)
                    UIToastMessage.show(activity, response.response)
                }
            }
        }
    }
    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            textViewDistrict.setText(s)
            if (districtID > 0) {
                fetchTalukaMasterData()
            }

            talukaID = 0
            textViewTaluka.setText("")
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)
            villJSONArray =null
            villageID = 0
            textViewVillage.setText("")
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
            textViewVillage.setHintTextColor(Color.GRAY)

            cropJSONArray = null
            cropID = 0
            textViewCrop.setText("")
            textViewCrop.setHint(resources.getString(R.string.select_crop))
            textViewCrop.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            try {
                talukaID = s1!!.toInt()
                if (s != null) {
                    talukaName = s
                    getVillageAgainstTaluka()
                }
                textViewTaluka.setText(s)

                villageID = 0
                textViewVillage.setText("")
                textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
                textViewVillage.setHintTextColor(Color.GRAY)

                cropJSONArray = null
                cropID = 0
                textViewCrop.setText("")
                textViewCrop.setHint(resources.getString(R.string.select_crop))
                textViewCrop.setHintTextColor(Color.GRAY)
            } catch (ex: NumberFormatException) {
                System.err.println("Invalid string in argumment")
                Toast.makeText(activity, "Data not Found...", Toast.LENGTH_SHORT).show();
            }
        }
        if (i == 3) {
            try {
                villageID = s1!!.toInt()
                if (s != null) {
                    villageName = s
                    fetchCropMasterData()
                }
                textViewVillage.setText(s)

                cropID = 0
                textViewCrop.setText("")
                textViewCrop.setHint(resources.getString(R.string.select_crop))
                textViewCrop.setHintTextColor(Color.GRAY)
            } catch (ex: NumberFormatException) {
                Toast.makeText(activity, "Data not Found...", Toast.LENGTH_SHORT).show();
            }
        }

        if (i == 4) {
            try {
                cropID = s1!!.toInt()
                if (s != null) {
                    cropName = s
                   // fetchCropMasterData()
                    val cropAdvisoryFragment = CropAdvisoryFragment()
                    val bundle = Bundle()
                    bundle.putString("cropId", cropID.toString())
                    bundle.putString("talukaId", talukaID.toString())
                    bundle.putString("villageId", villageID.toString())
                    cropAdvisoryFragment.setArguments(bundle)
                    val fragmentTransaction = getParentFragmentManager().beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayout, cropAdvisoryFragment,"AdvisorySelection")
                    fragmentTransaction.addToBackStack("AdvisorySelection")
                    fragmentTransaction.commit()
                }
                textViewCrop.setText(s)

            } catch (ex: NumberFormatException) {
                Toast.makeText(activity, "Data not Found...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {

        val jsonObject = obj as JSONObject
        val cropId: String =jsonObject.getString("id")
        val cropNameEn: String =jsonObject.getString("name")
        val cropNameMr: String =jsonObject.getString("name_mr")
        val cropSap: String =jsonObject.getString("cropsap")
        Log.d("CropId page 1", cropId)
        Log.d("talukaID page 1", talukaID.toString())
        UIToastMessage.show(activity,"You selected "+cropNameEn)
        val intent = Intent(activity, CropAdvisoryDetails::class.java)
        intent.putExtra("CropId", cropId)
        intent.putExtra("CropNameEn", cropNameEn)
        intent.putExtra("CropNameMr", cropNameMr)
        intent.putExtra("CropSap", cropSap)
        intent.putExtra("TalukaId", talukaID.toString())
        startActivity(intent)
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            Log.d("i2", day.toString())
            Log.d("i3", month.toString())
            Log.d("i4", year.toString())
            sowingDate = "" + year + "-" + month + "-" + day
            textViewSowingDate.text = sowingDate
        }
    }
}

