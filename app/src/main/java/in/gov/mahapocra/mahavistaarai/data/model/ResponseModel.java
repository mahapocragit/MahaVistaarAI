package in.gov.mahapocra.mahavistaarai.data.model;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class ResponseModel {
    private boolean status = false;
    private String response;
    private JSONObject cropsapadvisory;
    private JSONObject data;
    private JSONArray kDataArray;
    private JSONArray dataArray;
    private int remaining;
    private String totalWarehouse;
    private String totalAvailableWarehouse;

    private JSONObject jsonObject;

    public ResponseModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean getStatus() {
        int  flag;

        if( !jsonObject.has("status") ){
            flag= AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "ResponceCode");
        }else {
            flag = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "status");
        }
        if (flag == 200) {
            status = true;
        }else if (flag == 202) {
            status = false;
        } else {
            status = false;
        }
        return status;
    }

    public int getCheckFlag() {

        int flag = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "status");

        if (flag == 200) {
            flag = 200;
        }else if (flag == 202) {
            flag = 202;
        }
        return flag;
    }


    public String getResponse() {

        response = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "response");
        return response;
    }

    public String getData() {
        response = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "data");
        return response;
    }

    public JSONArray getDataArrays() {
        kDataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Data");
        return kDataArray;
    }

    public JSONArray getdataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "data");
        return dataArray;
    }

    public JSONArray getAdvisoryArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "autoadvisory");
        return dataArray;
    }
    public JSONArray getuserDataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "getuser_data");
        return dataArray;
    }
    public JSONArray getResponseArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "response");
        return dataArray;
    }


    public JSONArray getNewsArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "News");
        return dataArray;
    }

    public JSONArray getForcastDataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Forcast");
        return dataArray;
    }

    public JSONArray getPriviousWeatherDataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Previous");
        return dataArray;
    }

    public JSONArray getActivityGrpArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "ActivityGroup");
        return dataArray;
    }
    public JSONArray getResilientyGrpArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "CRAMainGroup");
        return dataArray;
    }

    public JSONArray getActivityGrpDetailsArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Activity");
        return dataArray;
    }
    public JSONArray getActivityGrpreqrDocArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "RequiredDocuments");
        return dataArray;
    }
    public JSONArray getActivityGrpEligibiltyArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "EligibilityCriteria");
        return dataArray;
    }
    public JSONArray getActivityGrpNoteArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Notes");
        return dataArray;
    }


    public String total_available_capacity() {
        totalWarehouse = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "total_warehouse");
        return totalWarehouse;
    }

    public String getTotalAvailableWareHouse() {
        totalAvailableWarehouse = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "total_available_capacity");
        return totalAvailableWarehouse;
    }




    public JSONArray getPlot_And_Crop() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "plot_and_crop");
        return dataArray;
    }


//    public String getCropsapad() {
//        cropsapadvisory = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "cropsapadvisory");
//        return cropsapadvisory;
//    }

    public JSONObject getCropsapadvisory() {
        cropsapadvisory = AppUtility.getInstance().sanitizeJSONObjJSONObj(this.jsonObject, "cropsapadvisory");
        return cropsapadvisory;
    }

    public JSONObject getJSONObject() {
        data = AppUtility.getInstance().sanitizeJSONObjJSONObj(this.jsonObject, "data");
        return data;
    }

    public int getRemaining() {
        remaining = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "data");
        return remaining;
    }

    public String getMsg() {
        JSONObject jsonObject = null;
        jsonObject = this.jsonObject;
        String msg = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "response");

       /* try {
            jsonObject = this.jsonObject.getJSONObject("meta");
            msg = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "response");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return msg;
    }
}
