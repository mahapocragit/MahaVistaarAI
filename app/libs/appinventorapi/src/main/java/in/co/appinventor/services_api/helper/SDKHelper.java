package in.co.appinventor.services_api.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.appinventor.services_api.app_util.AppUtility;

/* renamed from: in.co.appinventor.services_api.helper.SDKHelper */
public class SDKHelper {
    private static final SDKHelper ourInstance = new SDKHelper();

    public static SDKHelper getInstance() {
        return ourInstance;
    }

    private SDKHelper() {
    }

    private List<JSONObject> getRegisterList() {
        List<JSONObject> dataArray = new ArrayList<>();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("package_id", "in.gov.krishi.maharashtra.pocra.ffs");
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("package_id", "in.gov.mahapocra.sma");
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("package_id", "in.gov.mahapocra.container");
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("package_id", "in.gov.pocra.training");
            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("package_id", "in.gov.mahapocra.mlp");
            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("package_id", "com.neevmitra");
            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("package_id", "com.tatamotors.tma");
            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("package_id", "com.krishi_maharashtra_sma");
            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("package_id", "in.gov.mahapocra.mahavistaarai");
            dataArray.add(jsonObject1);
            dataArray.add(jsonObject2);
            dataArray.add(jsonObject3);
            dataArray.add(jsonObject4);
            dataArray.add(jsonObject5);
            dataArray.add(jsonObject6);
            dataArray.add(jsonObject7);
            dataArray.add(jsonObject8);
            dataArray.add(jsonObject9);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataArray;
    }

    public boolean isAppRegister(String packageId) {
        boolean isAppReg = false;
        for (JSONObject jsonObj : getRegisterList()) {
            if (packageId.equalsIgnoreCase(new DataModel(jsonObj).getPackage_id())) {
                isAppReg = true;
            }
        }
        return isAppReg;
    }

    /* renamed from: in.co.appinventor.services_api.helper.SDKHelper$DataModel */
    class DataModel {
        JSONObject jsonObject;
        String package_id;

        private DataModel(JSONObject jsonObject2) {
            this.jsonObject = jsonObject2;
        }

        /* access modifiers changed from: private */
        public String getPackage_id() {
            String sanitizeJSONObj = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "package_id");
            this.package_id = sanitizeJSONObj;
            return sanitizeJSONObj;
        }
    }
}
