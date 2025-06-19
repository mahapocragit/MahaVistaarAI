package in.gov.mahapocra.mahavistaarai.util.app_util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppHelper {

    private static final AppHelper ourInstance = new AppHelper();

    public static AppHelper getInstance() {
        return ourInstance;
    }

    private AppHelper() {

    }

    public JSONArray getMenuOption() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("id", 0);
            jsonObject0.put("name", "My Profile");
            jsonObject0.put("icon", "myprofile");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Credits");
            jsonObject4.put("icon", "partners_ic");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 3);
            jsonObject1.put("name", "About");
            jsonObject1.put("icon", "about_ic");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "Logout");
            jsonObject9.put("icon", "logout");

            jsonArray.put(jsonObject0);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getForGuestOption() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Credits");
            jsonObject4.put("icon", "partners_ic");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 3);
            jsonObject1.put("name", "About");
            jsonObject1.put("icon", "about_ic");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Login/Registration");
            jsonObject7.put("icon", "myprofile");

            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getMenuOptionMarathi() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("id", 0);
            jsonObject0.put("name","माझी प्रोफाईल");
            jsonObject0.put("icon", "myprofile");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "भागीदार");
            jsonObject4.put("icon", "partners_ic");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 3);
            jsonObject1.put("name", "आमच्या विषयी");
            jsonObject1.put("icon", "about_ic");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "बाहेर पडा");
            jsonObject9.put("icon", "logout");

            jsonArray.put(jsonObject0);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getMenuOptionForGuestMarathi() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "भागीदार");
            jsonObject4.put("icon", "partners_ic");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 3);
            jsonObject1.put("name", "आमच्या विषयी");
            jsonObject1.put("icon", "about_ic");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "लॉगइन/नोंदणी");
            jsonObject7.put("icon", "myprofile");

            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
