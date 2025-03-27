package in.gov.mahapocra.farmerapppks.util.app_util;

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

//            JSONObject jsonObject2 = new JSONObject();
//            jsonObject2.put("id", 1);
//            jsonObject2.put("name", "Yashogatha");
//            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "News");
            jsonObject4.put("icon", "news_drawer10");

//            JSONObject jsonObject6 = new JSONObject();
//            jsonObject6.put("id", 5);
//            jsonObject6.put("name", "Trainings/Workshops/Events/Exposures");
//            jsonObject6.put("icon", "icon9");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Login/Registration");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "Logout");
            jsonObject9.put("icon", "logout");

//            JSONObject jsonObject11 = new JSONObject();
//            jsonObject11.put("id", 11);
//            jsonObject11.put("name", "My Village Profile");
//            jsonObject11.put("icon", "icon9");

//            JSONObject jsonObject12 = new JSONObject();
//            jsonObject12.put("id", 12);
//            jsonObject12.put("name", "My Application");
//            jsonObject12.put("icon", "icon9");

            JSONObject jsonObject13 = new JSONObject();
            jsonObject13.put("id", 13);
            jsonObject13.put("name", "Suggestion & Grievances");
            jsonObject13.put("icon", "icon9");

            jsonArray.put(jsonObject1);
//            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
//            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject11);
//            jsonArray.put(jsonObject12);
            jsonArray.put(jsonObject13);
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

//            JSONObject jsonObject2 = new JSONObject();
//            jsonObject2.put("id", 1);
//            jsonObject2.put("name", " यशोगाथा");
//            jsonObject2.put("icon", "icon9");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "कृषी वार्ता");
            jsonObject4.put("icon", "news_drawer10");

//            JSONObject jsonObject6 = new JSONObject();
//            jsonObject6.put("id", 5);
//            jsonObject6.put("name", "प्रशिक्षण, कार्यशाळा, कार्यक्रम, भेटी");
//            jsonObject6.put("icon", "icon9");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "लॉगइन/नोंदणी");
            jsonObject7.put("icon", "myprofile");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 7);
            jsonObject9.put("name", "बाहेर पडणे");
            jsonObject9.put("icon", "logout");

//            JSONObject jsonObject11 = new JSONObject();
//            jsonObject11.put("id", 11);
//            jsonObject11.put("name", "ग्राम कृषी संजीवनी");
//            jsonObject11.put("icon", "icon9");

//            JSONObject jsonObject12 = new JSONObject();
//            jsonObject12.put("id", 12);
//            jsonObject12.put("name", "माझे अर्ज");
//            jsonObject12.put("icon", "icon9");

            JSONObject jsonObject13 = new JSONObject();
            jsonObject13.put("id", 13);
            jsonObject13.put("name", "सूचना व तक्रार निवारण");
            jsonObject13.put("icon", "icon9");

            jsonArray.put(jsonObject1);
//            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
//            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject11);
//            jsonArray.put(jsonObject12);
            jsonArray.put(jsonObject13);
            jsonArray.put(jsonObject9);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }
}
