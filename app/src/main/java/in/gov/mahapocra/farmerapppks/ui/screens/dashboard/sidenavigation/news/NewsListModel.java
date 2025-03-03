package in.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation.news;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class NewsListModel {
    private String id;
    private int app_id;
    private int user_id;
    private String mobile_no;
    private String role_name;
    String notification_data;
    private String fcm_response;
    private String read_at;
    private String is_read;
    private String is_overview;
    private String overview_at;
    private String created_at;
    private String updated_at;
    private String version_number;
    private String app_name;
    private String user_name;
    private String createdatetime;
    private String updateddatetime;

    private JSONObject jsonObject;
    public NewsListModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getNotification_data() {
        notification_data = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "notification_data");
        return notification_data;
    }
    public String getIs_read() {
        is_read = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "is_read");
        return is_read;
    }
    public String getCreated_at() {
        created_at = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "created_at");
        return created_at;
    }

    public String getCreatedatetime() {
        createdatetime = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "createdatetime");
        return createdatetime;
    }
    public String getId() {
        id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");
        return id;
    }

}
