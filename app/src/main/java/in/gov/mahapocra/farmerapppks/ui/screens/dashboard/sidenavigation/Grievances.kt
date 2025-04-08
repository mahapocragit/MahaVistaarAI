package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation


import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.api.AppinventorIncAPI
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.ApUtil
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.util.app_util.ManagePermission
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Grievances : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    AlertListEventListener {
    lateinit var textViewHeaderTitle: TextView
    lateinit var imageBackArrow: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var mobNoEditText: EditText
    private lateinit var issue_address: EditText
    private lateinit var textViewCategories: TextView
    private lateinit var grievance_descpt: EditText
    private lateinit var textViewDoc: TextView
    private lateinit var textViewVerify: TextView
    private lateinit var submitButton: Button
    private lateinit var grievancesImage: ImageView
    private lateinit var grievancePdfLLayout: Button

    private var categoryJSONArray: JSONArray? = null
    private var cotegoryID: Int = 0
    lateinit var categoryName: String
    private val REQUEST_CAMERA = 55

    var farmerRigisterID: Int = 0
    private var fAAPRegistrationID: String = ""
    lateinit var userName: String
    lateinit var mob: String
    lateinit var registerMob: String
    lateinit var pass: String
    lateinit var confrmPass: String
    lateinit var emailid: String
    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var villageName: String
    lateinit var villageCode: String
    private var villageID: Int = 0

    var currentTime: String? = null
    private var selectedImage = "0"
    private val PICK_FILE_REQUEST = 2
    private val APP_PERMISSION_REQUEST_CODE = 111
    private var managePermissions: ManagePermission? = null
    val CAMERA = 0x5
    private val IMAGE_DIRECTORY = "/Pocra_FarmerApp"
    private var imgFile: File? = null
    private var selectedFilePath: String? = null
    private var photoFile: File? = null
    private var flag = ""
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Grievances).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_grievances)
        init()
        setConfig()
        onClick()
        getCategoryList()
    }
    private fun getCategoryList() {
        var url: String = APIServices.kGrievanceCategory
        Log.d("param", url)
        val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
        api.getRequestData(url, this, 1)
    }
    private fun init() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageBackArrow = findViewById(R.id.imgBackArrow)
        nameEditText = findViewById(R.id.nameEditText)
        mobNoEditText = findViewById(R.id.mobNoEditText)
        issue_address = findViewById(R.id.issue_address)
        textViewCategories = findViewById(R.id.textViewCategories)
        grievance_descpt = findViewById(R.id.grievance_descpt)
       // textViewDoc = findViewById(R.id.textViewDoc)
        textViewVerify = findViewById(R.id.textViewVerify)
        submitButton = findViewById(R.id.submitButton)
        grievancesImage = findViewById(R.id.grievancesImage)
        grievancePdfLLayout = findViewById(R.id.grievancePdfLLayout)
    }

    private fun onClick() {
        imageBackArrow.setOnClickListener(View.OnClickListener {
            finish()
        })
        submitButton.setOnClickListener({
//            Log.d("submitButton", "Onclick submitButton")
//            selectedFilePath?.let { it1 -> imgFile?.let { it2 -> addGrievance(it2, it1) } }
//            uploadPDFonServer()
             addGrievance()
        })
        textViewCategories.setOnClickListener({
            showCategories()
        })
        grievancesImage.setOnClickListener({
            if (Build.VERSION.SDK_INT < 19) {
                selectImage(this)
            } else {
                if (checkUserPermission()) {
                    selectImage(this)
                }
            }
            grievancePdfLLayout.setVisibility(View.GONE)
            flag=="img"
        })
        grievancePdfLLayout.setOnClickListener(View.OnClickListener {
            uploadPDFonSever()
            grievancesImage.setVisibility(View.GONE)
            flag=="pdf"
//            val uri = intent.data
//            Log.d("Grievances===","uri==="+uri);
//            val fileImagePath = getRealPathFromURI(uri)
//            Log.d("Grievances===","fileImagePath==="+fileImagePath);
        })
    }
    private fun checkUserPermission(): Boolean {
        val permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionWrite =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionStorage =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded = ArrayList<String>()
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            managePermissions = ManagePermission(
                this,
                listPermissionsNeeded,
                APP_PERMISSION_REQUEST_CODE
            )
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                APP_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    private fun uploadPDFonSever()
    {
            val intent = Intent()
            //sets the select file to all types of files
            intent.type = "application/pdf"
            //allows to select data and return it
            intent.action = Intent.ACTION_GET_CONTENT
            //starts new activity to select file and return data
            startActivityForResult(
                Intent.createChooser(intent, "Choose File to Upload.."),
                PICK_FILE_REQUEST
            )
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose image upload option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                if (Build.VERSION.SDK_INT < 19) {
                    navigateToCaptureCamera()
                } else {
                    if (checkUserPermission()) {
                        navigateToCaptureCamera()
                    }
                }
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Choose Image to Upload.."), PICK_FILE_REQUEST
                )
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun navigateToCaptureCamera() {
        if (checkUserPermission()) {
            //getTrackerLocation()
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(
                MediaStore.EXTRA_SCREEN_ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            )
            startActivityForResult(
                cameraIntent,
               REQUEST_CAMERA
            )
        }
    }

    @SuppressLint("MissingSuperCall", "Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("data32323", data.toString())
        super.onActivityResult(requestCode, resultCode, data)
        // for camera img
        if (REQUEST_CAMERA == requestCode && resultCode == RESULT_OK) {
            val bitmap = data!!.extras!!["data"] as Bitmap?
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                this@Grievances.getContentResolver(),
                bitmap,
                "IMG_" + Calendar.getInstance().getTime(),null
            )
            val filesDir: File = this@Grievances.getFilesDir()
           // if (selectedImage.equals("1", ignoreCase = true)) {
                imgFile = File(filesDir, "image" + ".jpg")
                grievancesImage.setImageBitmap(bitmap)
           // }
            if (imgFile != null) {
                val os: OutputStream
                try {
                    os = FileOutputStream(imgFile)
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()
                } catch (e: java.lang.Exception) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                }
            }
            Log.d("onActivityResult=","REQUEST_CAMERA_imgFile="+imgFile)
            Log.d("onActivityResult=","REQUEST_CAMERA="+path)
        }
        // for gallary img
        if (requestCode == PICK_FILE_REQUEST) {
            val uri = data!!.data
            val uriString = uri.toString()
            val myFile = File(uriString)
            val path = myFile.absolutePath
            var displayName: String? = null
            if (uriString.startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(uri!!, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("PICK_FILE_REQUEST=", displayName.toString())

                        if (FileUtil.isValidPdfName(displayName)) {
                            // File name is valid
                            // Your code here
                            selectedFilePath = ApUtil.getStringFilePathForN(uri, this)  // for pdf
                            imgFile = ApUtil.getFilePathForN(uri, this)  // for camaera and galary image
                            Log.d("onActivityResult=","PICK_FILE_REQUEST="+selectedFilePath.toString())
                            Log.d("onActivityResult=","PICK_FILE_REQUEST="+imgFile.toString())
                            addGrievance()
                            if (selectedFilePath != null && selectedFilePath != "") {
                                grievancePdfLLayout.setText(displayName)
                            } else {
                                Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            try {
                                Picasso.get().invalidate(imgFile!!)
                                Picasso.get()
                                    .load(imgFile!!)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .placeholder(R.drawable.ic_thumbnail)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .error(R.drawable.ic_thumbnail)
                                    .into(grievancesImage)
                            } catch (ex: java.lang.Exception) {
                                ex.toString()
                            }
                        } else {

                            Toast.makeText(this, "Invalid PDF file name", Toast.LENGTH_SHORT).show()
                        }
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.name
                Log.d("name", displayName)
            }
            DebugLog.getInstance().d("fileNameIs" + imgFile.toString())
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private object FileUtil {

        private const val PDF_NAME_REGEX = "^[a-zA-Z0-9_-]+\\.pdf$"

        fun isValidPdfName(fileName: String): Boolean {
            val pattern = Pattern.compile(PDF_NAME_REGEX)
            val matcher = pattern.matcher(fileName)
            return matcher.matches()
        }
        // Rest of the code...
    }

    private fun setConfig() {
        imageBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.supportgrivience)

        farmerRigisterID = intent.getIntExtra("FAAPRegistrationID", 0)
        if (farmerRigisterID > 0) {
            fAAPRegistrationID = farmerRigisterID.toString()
            userName =
                AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
            registerMob = AppSettings.getInstance().getValue(
                this, AppConstants.uMobileNo,
                AppConstants.uMobileNo
            )
            emailid =
                AppSettings.getInstance().getValue(this, AppConstants.uEmail, AppConstants.uEmail)
            districtName =
                AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
            talukaName =
                AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
            villageName = AppSettings.getInstance()
                .getValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
            districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
            talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
            villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

            nameEditText.setText(userName)
            mobNoEditText.setText(registerMob)
            nameEditText.setEnabled(false)
            mobNoEditText.setEnabled(false)
        }
    }

    private fun showCategories() {
        if (categoryJSONArray == null) {
            getCategoryList()
        } else {
            AppUtility.getInstance().showListDialogIndex(
                categoryJSONArray,
                1,
                getString(R.string.grivience_categories),
                "name",
                "id",
                this,
                this
            )
        }
    }
    private fun addGrievance() {
        try {
            var name: String = nameEditText.text.toString()
            var mobileNumber: String = mobNoEditText.text.toString()
            var areaOfIssues: String = issue_address.text.toString()
            var descriptionText: String = grievance_descpt.text.toString()
            Log.d("Grievances===", "addGrievance method called....")
            if (!(cotegoryID > 0)) {
                textViewCategories.error = resources.getString(R.string.name_error)
                textViewCategories.requestFocus()
            } else if (descriptionText.isEmpty()) {
                grievance_descpt.error = resources.getString(R.string.area_of_issues_err)
                grievance_descpt.requestFocus()
            }
            var partBody: MultipartBody.Part? = null
            DebugLog.getInstance().d("imgName=$imgFile")
            Log.d("addGrievance=", "imgFile===" + imgFile)
            val params: HashMap<String, RequestBody> = HashMap<String,RequestBody>()

            params.put("mobile", AppInventorApi.toRequestBody(mobileNumber.trim { it <= ' ' }))
            params.put("category" , AppInventorApi.toRequestBody(cotegoryID.toString()))
            params.put("grievance", AppInventorApi.toRequestBody(descriptionText))
            params.put("name",
                AppInventorApi.toRequestBody(name))
            params.put("location", AppInventorApi.toRequestBody(areaOfIssues))
            if( flag.equals("pdf")){
                //creating a file
                val file = selectedFilePath as File   //pdf
                Log.d("file", "selectedFilePath===" + file)
                 val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                partBody = MultipartBody.Part.createFormData("fileToUpload", file.name, reqFile)
            }else {
                //creating a file
                val file = File(imgFile!!.path)  //image
                Log.d("file", "imgFile===" + file)
                val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
                partBody = MultipartBody.Part.createFormData("fileToUpload", file.name, reqFile)
            }
            DebugLog.getInstance().d("params1=$params")
            val api = AppinventorIncAPI(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit = api.retrofitInstance
            //creating our api
            val apiRequest = retrofit.create(APIRequest::class.java)
            //creating a call and calling the upload image method
            val responseCall: Call<JsonObject> = apiRequest.submitGrievance(partBody, params)
            //creating a call and calling the upload image method
            api.postRequest(responseCall, this, 33)

                DebugLog.getInstance().d("param" + responseCall.request().toString())
                DebugLog.getInstance().d("param" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: Exception) {
            Log.d("e232323232", "e===" + e)
            e.printStackTrace()
        }
    }
    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {
            cotegoryID = s1!!.toInt()
            if (s != null) {
                categoryName = s
            }
            textViewCategories?.setText(s)
        }
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                categoryJSONArray = response.getdataArray()
                Log.d("categorieJSONArray11111", categoryJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
        if (i == 33) {
            if (jSONObject != null) {
              //  Log.d("jSONObject232dacsboard", jSONObject.toString())
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                Toast.makeText(this, response.response, Toast.LENGTH_LONG).show()
                 }
            }
        }
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }
}