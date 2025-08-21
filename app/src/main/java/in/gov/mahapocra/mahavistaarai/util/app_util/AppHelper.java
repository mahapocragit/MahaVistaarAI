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
            JSONObject profileObject = new JSONObject();
            profileObject.put("id", 0);
            profileObject.put("name", "My Profile");
            profileObject.put("icon", "myprofile");

            JSONObject aboutObject = new JSONObject();
            aboutObject.put("id", 1);
            aboutObject.put("name", "About");
            aboutObject.put("icon", "about_ic");

            JSONObject expertsObject = new JSONObject();
            expertsObject.put("id", 2);
            expertsObject.put("name", "Experts Corner");
            expertsObject.put("icon", "experts_ic");

            JSONObject partnerObject = new JSONObject();
            partnerObject.put("id", 3);
            partnerObject.put("name", "Credits");
            partnerObject.put("icon", "partners_ic");

            JSONObject newsObject = new JSONObject();
            newsObject.put("id", 4);
            newsObject.put("name", "News");
            newsObject.put("icon", "news_ic");

            JSONObject logoutObject = new JSONObject();
            logoutObject.put("id", 7);
            logoutObject.put("name", "Logout");
            logoutObject.put("icon", "logout");

            jsonArray.put(profileObject);
            jsonArray.put(partnerObject);
            jsonArray.put(expertsObject);
            jsonArray.put(aboutObject);
            jsonArray.put(newsObject);
            jsonArray.put(logoutObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getMenuOptionMarathi() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject profileObject = new JSONObject();
            profileObject.put("id", 0);
            profileObject.put("name", "माझी प्रोफाईल");
            profileObject.put("icon", "myprofile");

            JSONObject aboutObject = new JSONObject();
            aboutObject.put("id", 1);
            aboutObject.put("name", "आमच्या विषयी");
            aboutObject.put("icon", "about_ic");

            JSONObject expertsObject = new JSONObject();
            expertsObject.put("id", 2);
            expertsObject.put("name", "Experts Corner");
            expertsObject.put("icon", "experts_ic");

            JSONObject partnerObject = new JSONObject();
            partnerObject.put("id", 3);
            partnerObject.put("name", "भागीदार");
            partnerObject.put("icon", "partners_ic");

            JSONObject newsObject = new JSONObject();
            newsObject.put("id", 4);
            newsObject.put("name", "बातम्या");
            newsObject.put("icon", "news_ic");

            JSONObject logoutObject = new JSONObject();
            logoutObject.put("id", 7);
            logoutObject.put("name", "बाहेर पडा");
            logoutObject.put("icon", "logout");

            jsonArray.put(profileObject);
            jsonArray.put(aboutObject);
            jsonArray.put(expertsObject);
            jsonArray.put(partnerObject);
            jsonArray.put(newsObject);
            jsonArray.put(logoutObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getForGuestOption() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject aboutObject = new JSONObject();
            aboutObject.put("id", 1);
            aboutObject.put("name", "About");
            aboutObject.put("icon", "about_ic");

            JSONObject expertsObject = new JSONObject();
            expertsObject.put("id", 2);
            expertsObject.put("name", "Experts Corner");
            expertsObject.put("icon", "experts_ic");

            JSONObject creditObject = new JSONObject();
            creditObject.put("id", 3);
            creditObject.put("name", "Credits");
            creditObject.put("icon", "partners_ic");

            JSONObject newsObject = new JSONObject();
            newsObject.put("id", 4);
            newsObject.put("name", "News");
            newsObject.put("icon", "news_ic");

            JSONObject profileObject = new JSONObject();
            profileObject.put("id", 6);
            profileObject.put("name", "Login/Registration");
            profileObject.put("icon", "myprofile");

            jsonArray.put(aboutObject);
            jsonArray.put(creditObject);
            jsonArray.put(expertsObject);
            jsonArray.put(newsObject);
            jsonArray.put(profileObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getMenuOptionForGuestMarathi() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject aboutObject = new JSONObject();
            aboutObject.put("id", 1);
            aboutObject.put("name", "आमच्या विषयी");
            aboutObject.put("icon", "about_ic");

            JSONObject expertsObject = new JSONObject();
            expertsObject.put("id", 2);
            expertsObject.put("name", "Experts Corner");
            expertsObject.put("icon", "experts_ic");

            JSONObject creditObject = new JSONObject();
            creditObject.put("id", 3);
            creditObject.put("name", "भागीदार");
            creditObject.put("icon", "partners_ic");

            JSONObject newsObject = new JSONObject();
            newsObject.put("id", 4);
            newsObject.put("name", "बातम्या");
            newsObject.put("icon", "news_ic");

            JSONObject profileObject = new JSONObject();
            profileObject.put("id", 6);
            profileObject.put("name", "लॉगइन/नोंदणी");
            profileObject.put("icon", "myprofile");

            jsonArray.put(aboutObject);
            jsonArray.put(creditObject);
            jsonArray.put(expertsObject);
            jsonArray.put(newsObject);
            jsonArray.put(profileObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
