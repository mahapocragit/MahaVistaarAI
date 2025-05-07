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
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "myprofile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "About");
            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "News");
            jsonObject4.put("icon", "news_drawer10");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Login/Registration");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "Logout");
            jsonObject9.put("icon", "logout");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getMenuOptionMarathi() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name","माझी प्रोफाईल");
            jsonObject1.put("icon", "myprofile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "आमच्या विषयी");
            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "कृषी वार्ता");
            jsonObject4.put("icon", "news_drawer10");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "लॉगइन/नोंदणी");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "बाहेर पडा");
            jsonObject9.put("icon", "logout");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
