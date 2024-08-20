package `in`.gov.mahapocra.farmerapppks.fragments

import android.annotation.SuppressLint
import `in`.gov.mahapocra.farmerapppks.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.adapter.BbtActivityGrpAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.AdvisoryFeedback
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.languageToLoad
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.ratingbar
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.submitFeedback
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CropAdvisoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

lateinit var onfeedback: TextView
lateinit var cropSapAdvisoryTv: TextView
lateinit var cropAdvisoryTv: TextView
var languageToLoad: String? = null
private lateinit var cropID: String
private lateinit var talukaID: String
private lateinit var villageID: String
 private var cropsapadvisoryCropId: String? = null
private var advisoryCropId: String = ""
private var autoadvisoryRcyl: RecyclerView? = null



class CropAdvisoryFragment : Fragment(), ApiCallbackCode, OnMultiRecyclerItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_crop_advisory, container, false)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(activity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        initial(view)
        onClick()
        return view


    }


    private fun onClick() {
        onfeedback.setOnClickListener { view ->
            val advisoryFeedback = AdvisoryFeedback()
            val bundle = Bundle()
            bundle.putString("cropId", cropID.toString())
            bundle.putString("villageId", villageID.toString())
            bundle.putString("cropsapadvisoryCropId", cropsapadvisoryCropId.toString())
            bundle.putString("advisoryCropId", advisoryCropId.toString())
            advisoryFeedback.setArguments(bundle)
            val fragmentTransaction = getParentFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, advisoryFeedback )
            fragmentTransaction.commit()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun initial(view: View) {
        onfeedback = view.findViewById(R.id.onFeedback)
        cropSapAdvisoryTv = view.findViewById(R.id.cropSapadvisoryTv)
        autoadvisoryRcyl = view.findViewById(R.id.autoadvisoryRcyl)
       // cropAdvisoryTv = view.findViewById(R.id.cropAdvisoryTv)

      val  view = requireActivity().findViewById<View>(R.id.relativeLayoutTopBar)
        view.visibility = View.GONE

      cropID = getArguments()?.getString("cropId").toString()
      talukaID = getArguments()?.getString("talukaId").toString()
      villageID = getArguments()?.getString("villageId").toString()
        getAutoAdvisory()

    }

    private fun getAutoAdvisory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop", cropID)
            jsonObject.put("taluka", talukaID)
            jsonObject.put("village", villageID)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(activity, APIServices.SS0_Temp, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getAutoAdvisory(requestBody)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CropAdvisoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CropAdvisoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status ) {
              val  autoadvisory: JSONArray = response.getAdvisoryArray()
                advisoryCropId=""
                for (i in 0 until autoadvisory.length()){
                    val a = autoadvisory.getJSONObject(i).getString("id")
                    val b:String
                    if( i == autoadvisory.length()-1) {
                        b = a
                    }else{
                        b = a.plus(",")
                    }
                    advisoryCropId = concatenate(advisoryCropId, b)
                    Log.d("advisoryCropId===",advisoryCropId)
                }

                //Recycle view for advisory
                val adaptorDbtActivityGrp = BbtActivityGrpAdapter(activity, this, autoadvisory, "cropAdvisory")
                autoadvisoryRcyl?.setLayoutManager(
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
                Log.d("dbtdata","autoadvisory="+autoadvisory);
                autoadvisoryRcyl?.setAdapter(adaptorDbtActivityGrp)
                adaptorDbtActivityGrp.notifyDataSetChanged()

                var cropsadvisory: JSONObject? = null
                Log.d("autoadvisory value",autoadvisory.toString())
                if (autoadvisory.length() > 0) {
                    cropsadvisory = autoadvisory.getJSONObject(0)
                    var advisoryText = cropsadvisory.getString("advisory")
                    cropsapadvisoryCropId = cropsadvisory.getString("id")
                }
                var cropsapadvisory: JSONObject? = null
                cropsapadvisory = response.getCropsapadvisory()
                cropSapAdvisoryTv.setText("")
                if (cropsapadvisory != null) {
                    var cropSapAdvisoryText = cropsapadvisory.getString("cropsap_advisory")
                    cropSapAdvisoryTv.setText(cropSapAdvisoryText)
                }
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }
    }


    fun concatenate(vararg string: String?): String {
        var result = ""
        string.forEach { result = result.plus(it) }
        return result
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}