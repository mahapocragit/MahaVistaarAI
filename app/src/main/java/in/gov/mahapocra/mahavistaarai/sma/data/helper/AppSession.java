
package in.gov.mahapocra.mahavistaarai.sma.data.helper;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.mahavistaarai.sma.data.constants.AppConstants;
import in.gov.mahapocra.mahavistaarai.sma.data.models.ProfileModel;

public class AppSession {


    private Context mContext;

    public AppSession(Context mContext) {
        this.mContext = mContext;
    }


    //::TODO User Roles

    public int getPMURole() {
        return 3;
    }

    public int getPDATMARole() {
        return 4;
    }

    public int getDSAORole() {
        return 5;
    }

    public int getSDAORole() {
        return 6;
    }

    public int getKVKRole() {
        return 7;
    }

    public int getCARole() {
        return 22;
    }

    public int getAgAsstRole() {
        return 9;
    }

    public int getFFSRole() {
        return 10;
    }

    public int getFarmerRole() {
        return 11;
    }

    public int getAOSDAORole() {
        return 22;
    }

    public int getSPRole() {
        return 23;
    }


    public boolean isUserLoggedIn() {
        return AppSettings.getInstance().getBooleanValue(mContext, AppConstants.kIS_LOGGED_IN, false);
    }


    public int getUserId() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kUSER_ID, 0);
    }


    public String getToken() {
        return AppSettings.getInstance().getSavedValue(mContext, AppConstants.kTOKEN);
    }


    public String getTimeStamp() {
        //When we divide by 1000 then it will return a timestamp
        return String.valueOf(System.currentTimeMillis() / 1000);
    }


    //::TODO Storage Dir
    public File getCommonStorageDir() {
        File storageDir = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), AppConstants.kDIR + "/" + AppConstants.kCOMMON_DIR);
        return storageDir;
    }


    public File getOnlineStorageDir() {
        File storageDir = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), AppConstants.kDIR + "/" + AppConstants.kVISITS_DIR + "/" + AppConstants.kVISITS_ONLINE_DIR);
        return storageDir;
    }

    public File getOfflineStorageDir() {
        File storageDir = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), AppConstants.kDIR + "/" + AppConstants.kVISITS_DIR + "/" + AppConstants.kVISITS_OFFLINE_DIR);
        return storageDir;
    }

    public File getOfflineStorageDirs() {
        String child = AppConstants.kDIR + "/" + AppConstants.kVISITS_DIR + "/" + AppConstants.kVISITS_OFFLINE_DIR + "/";
        File storageDir = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), child);
        return storageDir;
    }


    public void deleteAllFiles() {
        try {
            String childDir = AppConstants.kDIR + "/" + AppConstants.kVISITS_DIR + "/" + AppConstants.kVISITS_OFFLINE_DIR;
            File dir = new File(mContext.getExternalFilesDir(null).getAbsolutePath()+ "/" + childDir);
            if (dir != null && dir.isDirectory()) {
                if (dir.listFiles() != null) {
                    if (dir.listFiles().length > 0) {
                        AppUtility.getInstance().deleteAllFileFromDirectory(dir);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File createImageFile(File storageDir, String customFileName) throws IOException {

//        File storageDir = session.getOnlineStorageDir();

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String imageFileName;

        if (customFileName.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            imageFileName = "_" + timeStamp + "_";
        } else {
            imageFileName = customFileName;
        }

        File image = new File(storageDir+"/"+imageFileName+".jpg");

        return image;
    }


    //TODO If flag is true means that user is offline data access else online data access
    public boolean isOfflineMode() {
        return AppSettings.getInstance().getBooleanValue(mContext, AppConstants.kOnlineOfflineMode, false);
    }

    public ProfileModel getProfileModel() {
        String profile = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kLOGIN_DATA);

        try {
            JSONObject jsonObject = new JSONObject(profile);
            return new ProfileModel(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    //:: TODO App ID For ME APP
    public int getAppId() {
        return 3;
    }


    //:: TODO Get Deploy Build Version
    public int getServerAppBuildVer() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kAPP_BUILD_VER);
        int buildVer = 0;
        try {
            if (data.equalsIgnoreCase(AppConstants.kAPP_BUILD_VER)) {
                return buildVer;
            }
            JSONObject jsonObject = new JSONObject(data);
            buildVer = jsonObject.getInt("build_version");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return buildVer;
    }


    public int getUserRoleId() {
        int role_id = AppSettings.getInstance().getIntValue(mContext, AppConstants.kROLE_ID, 0);
        return role_id; //Host farmer registration
    }

    //::TODO Schedule Status

    public int scheduleCompleted() {
        return 1;
    }

    public int reschedule() {
        return 2;
    }

    public int scheduleMissed() {
        return 3;
    }

    public int todaySchedule() {
        return 4;
    }

    public int upcomingSchedule() {
        return 5;
    }


    //:: TODO Dashboard
    public JSONArray getSchedule() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kSCHEDULES);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kSCHEDULES)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getHistoryOfVisits() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kHISTORY_OF_VISITS);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kHISTORY_OF_VISITS)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getCropAdvisory() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kCROP_ADVISORY);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kCROP_ADVISORY)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public int getPlotID() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kPLOT_ID, 0);
    }

    public int getScheduleId() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kSCHEDULE_ID, 0);
    }

    public int getVisitNumber() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kVISIT_NUM, 0);
    }

    public int getVisitCount() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kVISIT_COUNT, 0);
    }

    public String getCropName() {
        return AppSettings.getInstance().getSavedValue(mContext, AppConstants.kCROP_NAME);
    }

    public String getInterCropName() {
        return AppSettings.getInstance().getSavedValue(mContext, AppConstants.kINTER_CROP_NAME);
    }

    public int getInterVisitCropCount() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kINTER_CROP_VISIT_COUNT, 0);
    }

    public JSONArray getData() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kDATA);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kDATA)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getAttendance() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kATTENDANCE)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public String getAttendanceFileName() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE_FILE);
        String fileName = null;
        if (data.equalsIgnoreCase(AppConstants.kATTENDANCE_FILE)) {
            fileName = null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(data);
                fileName = jsonObject.getString("file_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    public String getAttendanceFilePath() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE_PATH);
        if (data.equalsIgnoreCase(AppConstants.kATTENDANCE_PATH)) {
            return "";
        }
        return AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE_PATH);
    }


    public String getAttendanceFileURL() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE_FILE);
        String fileName = null;
        if (data.equalsIgnoreCase(AppConstants.kATTENDANCE_FILE)) {
            fileName = null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(data);
                fileName = jsonObject.getString("file_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    public String getAttendanceFileLatLon() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kATTENDANCE_FILE_LOC);
        String latLong = null;
        if (data.equalsIgnoreCase(AppConstants.kATTENDANCE_FILE_LOC)) {
            latLong = "0";
        } else {
            latLong = data;
        }

        return latLong;
    }


    public int getVisitUniqueId() {
        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kVISIT_UNIQUE_ID, 0);
    }

    public int getCropId() {
        int id = AppSettings.getInstance().getIntValue(mContext, AppConstants.kCROP_UNIQUE_ID, 0);
        return id;
    }


    //::TODO Soil Test Report

    public String getSoilTestReportFileName() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kSOIL_TEST_REPORT_FILE);
        String fileName = null;
        if (data.equalsIgnoreCase(AppConstants.kSOIL_TEST_REPORT_FILE)) {
            fileName = null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(data);
                fileName = jsonObject.getString("file_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    public String getSoilTestReportFileURL() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kSOIL_TEST_REPORT_FILE);
        String fileName = null;
        if (data.equalsIgnoreCase(AppConstants.kSOIL_TEST_REPORT_FILE)) {
            fileName = null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(data);
                fileName = jsonObject.getString("file_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    public JSONArray getYieldAttendance() {
        String data = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kYIELD_ATTENDANCE);
        JSONArray jsonArray = null;
        try {
            if (data.equalsIgnoreCase(AppConstants.kYIELD_ATTENDANCE)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public int getCAAttendanceId() {
        int id = AppSettings.getInstance().getIntValue(mContext, AppConstants.kCA_ATTENDANCE_ID, 0);
        return id;
    }

    public String getCACheckInDate() {
        String id = AppSettings.getInstance().getValue(mContext, AppConstants.kCA_ATTENDANCE_IN_TIMESTAMP,"");
        return id;
    }

    public String getCAAttendanceUrl() {
        String file = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kCA_ATTENDANCE);
        if (file.equalsIgnoreCase(AppConstants.kCA_ATTENDANCE)) {
            file = "";
        }
        return file;
    }

    public JSONArray getCAClusterDataArray() {

        String cluster = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kCA_CLUSTER_DATA);
        JSONArray jsonArray = null;
        if (cluster.equalsIgnoreCase(AppConstants.kCA_CLUSTER_DATA)) {
            jsonArray = new JSONArray();
        } else {
            try {
                jsonArray = new JSONArray(cluster);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }


    public void setSessionTimeStamp(String timeStamp) {
        AppSettings.getInstance().setValue(mContext, AppConstants.kSERVER_TIME_STAMP, timeStamp);
    }

    public String getSessionTimeStamp() {
        String timeStamp = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kSERVER_TIME_STAMP);
        if (timeStamp.equalsIgnoreCase(AppConstants.kSERVER_TIME_STAMP)) {
            timeStamp = "";
        }
        return timeStamp;
    }


    public String getDOJ() {
        String doj = AppSettings.getInstance().getSavedValue(mContext, AppConstants.kDOJ);
        if (doj.equalsIgnoreCase(AppConstants.kDOJ)) {
            doj = "";
        }
        return doj;
    }


    //::TODO Offline

    public boolean isOfflineSynced() {
        return AppSettings.getInstance().getBooleanValue(mContext, AppConstants.kIS_OFFLINE, false);
    }


    public void logoutFromApplication() {
        AppUtility.getInstance().clearAppSharedPrefData(mContext, AppConstants.kSHARED_PREF);
        AppSettings.getInstance().setValue(mContext, AppConstants.kSCHEDULES, AppConstants.kSCHEDULES);
        AppSettings.getInstance().setValue(mContext, AppConstants.kHISTORY_OF_VISITS, AppConstants.kHISTORY_OF_VISITS);
        AppSettings.getInstance().setValue(mContext, AppConstants.kCROP_ADVISORY, AppConstants.kCROP_ADVISORY);
        AppSettings.getInstance().setIntValue(mContext, AppConstants.kSEASON_ID, 0);
        AppSettings.getInstance().setIntValue(mContext, AppConstants.kCA_ATTENDANCE_ID, 0);
        AppSettings.getInstance().setBooleanValue(mContext, AppConstants.kIS_LOGGED_IN, false);

    }


}
