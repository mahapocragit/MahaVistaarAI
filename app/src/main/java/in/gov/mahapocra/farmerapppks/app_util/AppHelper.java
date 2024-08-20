package in.gov.mahapocra.farmerapppks.app_util;

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
            jsonObject2.put("name", "Yashogatha");
            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "My DBT Application Status");
            jsonObject3.put("icon", "icon3");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "News");
            jsonObject4.put("icon", "news_drawer10");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "GIS");
            jsonObject5.put("icon", "icon7");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "Trainings/Workshops/Events/Exposures");
            jsonObject6.put("icon", "icon9");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Login/Registration");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "Logout");
            jsonObject9.put("icon", "logout");



            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject1);

            jsonArray.put(jsonObject3);

            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray getDistMemLocationJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "District");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Subdivision");

            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);

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
            jsonObject1.put("name","माझे प्रोफाईल");
            jsonObject1.put("icon", "myprofile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", " यशोगाथा");
            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "डीबीटी अर्ज स्थिती");
            jsonObject3.put("icon", "icon3");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "कृषी वार्ता");
            jsonObject4.put("icon", "news_drawer10");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "जीआयएस- भौगोलिक माहिती प्रणाली");
            jsonObject5.put("icon", "icon7");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "प्रशिक्षण, कार्यशाळा, कार्यक्रम, भेटी");
            jsonObject6.put("icon", "icon9");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "लॉगइन/नोंदणी");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "बाहेर पडणे");
            jsonObject9.put("icon", "logout");



            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }
}
