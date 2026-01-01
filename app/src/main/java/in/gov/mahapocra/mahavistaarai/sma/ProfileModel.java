
package in.gov.mahapocra.mahavistaarai.sma;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class ProfileModel {

    private int id;
    private String username;
    private String email;
    private String mobile;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String gender;
    private String profile_pic;
    private int social_category_id;
    private String age;
    private String date_of_birth;
    private int city_id;
    private int village_id;
    private String village_name;
    private String village;
    private String level;

    private int service_provider_id;
    private String service_provider_name;

    private JSONArray subDivJSONArray;
    private JSONArray talukaJSONArray;
    private JSONArray rolearray;

    private int taluka_id;
    private String taluka_name;

    private String subdivision_name;
    private int subdivision_id;
    private int district_id;
    private String district_name;
    private String designation;
    private String social_category;
    private int is_active;
    private int is_delete;
    private int created_by;
    private int created_at;
    private int updated_by;
    private int updated_at;
    private int app_id;
    private int role_id;
    private int password_change_requires;

    private JSONObject jsonObject;

    public ProfileModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public int getId() {
        id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "id");
        return id;
    }

    public String getUsername() {
         username = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "username");
        return username;
    }

    public String getEmail() {
        email = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "email");
        return email;
    }

    public String getMobile() {
        mobile = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile");
        return mobile;
    }

    public String getFirst_name() {
        first_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "first_name");
        return first_name;
    }

    public String getMiddle_name() {
        middle_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "middle_name");
        return middle_name;
    }

    public String getLast_name() {
        last_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "last_name");
        return last_name;
    }

    public String getGender() {
        gender = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender");
        return gender;
    }

    public String getProfile_pic() {
        profile_pic = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "profile_pic");
        return profile_pic;
    }

    public int getSocial_category_id() {
        social_category_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "social_category_id");
        return social_category_id;
    }

    public String getAge() {
        age = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "age");
        return age;
    }

    public String getDate_of_birth() {
        date_of_birth = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "date_of_birth");
        return date_of_birth;
    }

    public int getCity_id() {
        city_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "city_id");
        return city_id;
    }

    public int getVillage_id() {
        village_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "village_code");
        return village_id;
    }


    public String getVillage_name() {
        village_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "village_name");
        return village_name;
    }

    public String getVillage() {
        village = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "village");
        return village;
    }


    public JSONArray getSubDivJSONArray() {
        subDivJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "subdivision_data");
        return subDivJSONArray;
    }

    public JSONArray getTalukaJSONArray() {
        talukaJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "taluka_data");
        return talukaJSONArray;
    }

    public int getTaluka_id() {
        taluka_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "taluka_id");
        return taluka_id;
    }

    public String getTaluka_name() {
        taluka_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "taluka_name");
        return taluka_name;
    }

    public int getDistrict_id() {
        district_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "district_code");
        return district_id;
    }

    public int getSubdivision_id() {
        subdivision_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "code");
        return subdivision_id;
    }

    public String getSubdivision_name() {
        subdivision_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "subdivision");
        return subdivision_name;
    }

    public String getDistrict_name() {
        district_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "district");
        return district_name;
    }

    public String getDesignation() {
        designation = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation");
        if (designation.length() == 0) {
            return " ";

        } else  {
            return designation;
        }
    }


    public String getSocial_category() {
        social_category = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "social_category");
        return social_category;
    }

    public int getIs_active() {
        is_active = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_active");
        return is_active;
    }

    public int getIs_delete() {
        is_delete = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_delete");
        return is_delete;
    }

    public int getCreated_by() {
        created_by = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "created_by");
        return created_by;
    }

    public int getCreated_at() {
        created_at = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "created_at");
        return created_at;
    }

    public int getUpdated_by() {
        updated_by = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "updated_by");
        return updated_by;
    }

    public int getUpdated_at() {
        updated_at = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "updated_at");
        return updated_at;
    }

    public int getApp_id() {
        app_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "app_id");
        return app_id;
    }

    public int getRole_id() {
        role_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "role_id");
        return role_id;
    }


    public int getPassword_change_requires() {
        password_change_requires = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "password_change_requires");
        return password_change_requires;
    }


    public int getService_provider_id() {
        service_provider_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "service_provider_id");
        return service_provider_id;
    }

    public String getService_provider_name() {
        service_provider_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "service_provider_name");
        return service_provider_name;
    }

    public String getLevel() {
        level = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "level");
        return level;
    }

    public JSONArray getRolearray() {
        subDivJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "roles");
        return subDivJSONArray;
    }
}

