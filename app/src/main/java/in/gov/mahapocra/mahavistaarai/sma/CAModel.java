/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 22/1/19 12:09 PM
 */

package in.gov.mahapocra.mahavistaarai.sma;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class CAModel {

    private int id;
    private String email;
    private String username;
    private String mobile;
    private String first_name;
    private String middle_name;
    private String last_name;
    private int is_active;
    private int taluka_code;
    private int gender;
    private String gender_name;
    private int district_id;
    private String district_name;
    private int age;
    private String physically_challenged;
    private int social_category_id;
    private String social_category_name;
    private int is_assigned;
    private int group_id;
    private String group_name;
    private int is_completed;
    private int is_selected;

    private JSONObject jsonObject;

    public CAModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public int getId() {
        id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "id");
        return id;
    }

    public String getEmail() {
        email= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "email");
        return email;
    }
    public String getUsername() {
        username= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "username");
        return username;
    }

    public String getMobile() {
        mobile= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile");
        return mobile;
    }

    public String getFirst_name() {
        first_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "first_name");
        return first_name;
    }

    public String getMiddle_name() {
        middle_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "middle_name");
        return middle_name;
    }

    public String getLast_name() {
        last_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "last_name");
        return last_name;
    }

    public int getIs_active() {
        is_active = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_active");
        return is_active;
    }

    public int getTaluka_code() {
        taluka_code = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "taluka_code");
        return taluka_code;
    }



    public int getGender() {
        gender = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "gender");
        return gender;
    }

    public String getGender_name() {
        gender_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender_name");
        return gender_name;
    }

    public int getDistrict_id() {
        district_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "district_id");
        return district_id;
    }

    public String getDistrict_name() {
        district_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "district_name");
        return district_name;
    }

    public int getAge() {
        age = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "age");
        return age;
    }

    public String getPhysically_challenged() {
        physically_challenged= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "physically_challenged");
        return physically_challenged;
    }

    public int getSocial_category_id() {
        social_category_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "social_category_id");
        return social_category_id;
    }

    public String getSocial_category_name() {
        social_category_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "social_category_name");
        return social_category_name;
    }


    public int getIs_assigned() {
        is_assigned = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_assigned");
        return is_assigned;
    }

    public int getGroup_id() {
        group_id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "group_id");
        return group_id;
    }

    public String getGroup_name() {
        group_name= AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "group_name");
        return group_name;
    }


    public int getIs_completed() {
        is_completed = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_completed");
        return is_completed;
    }

    public int getIs_selected() {
        is_selected = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_selected");
        return is_selected;
    }
}
