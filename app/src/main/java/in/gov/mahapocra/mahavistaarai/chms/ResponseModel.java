
package in.gov.mahapocra.mahavistaarai.chms;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class ResponseModel {

    private boolean status = false;
    private String response;
    private String refreshToken;
    private String token;
    private JSONObject data;
    private JSONArray dataArray;
    private int remaining;

    private JSONObject jsonObject;


    public ResponseModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public boolean getStatus() {

         int flag = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "status");

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
    public String getRefreshToken() {

        refreshToken = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "refresh_token");
        return refreshToken;
    }

    public String getCount() {

        response = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "count");
        return response;
    }
    public JSONObject getData() {

        data = AppUtility.getInstance().sanitizeJSONObjJSONObj(this.jsonObject, "data");
        return data;
    }


    public JSONArray getDataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "data");
        return dataArray;
    }

    public JSONArray getPlotGeoFence() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "PlotGeoFence");
        return dataArray;
    }

    public JSONArray getPlot_And_Crop() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "plot_and_crop");
        return dataArray;
    }


    public String getToken() {
        token = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "token");
        return token;
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
