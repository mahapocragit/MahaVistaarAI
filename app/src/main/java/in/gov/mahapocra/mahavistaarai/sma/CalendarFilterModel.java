/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 5/3/19 2:10 PM
 */

package in.gov.mahapocra.mahavistaarai.sma;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class CalendarFilterModel {

    private int id;
    private String name;
    private String month;
    private int year;
    private int is_selected;
    private int not_selected;
    private JSONObject jsonObject;

    public CalendarFilterModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public int getId() {
        id = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "id");
        return id;
    }

    public String getName() {
        name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "name");
        return name;
    }

    public String getMonth() {
        month = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "month");
        return month;
    }

    public int getYear() {
        year = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "year");
        return year;
    }

    public int getIs_selected() {
        is_selected = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_selected");
        return is_selected;
    }
    public int getNot_selected() {
        not_selected = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "not_selected");
        return not_selected;
    }
}
