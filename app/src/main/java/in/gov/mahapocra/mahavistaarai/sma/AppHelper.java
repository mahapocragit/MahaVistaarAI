/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 28/12/18 11:58 AM
 */

package in.gov.mahapocra.mahavistaarai.sma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import in.co.appinventor.services_api.debug.DebugLog;
import in.gov.mahapocra.mahavistaarai.R;

import java.time.YearMonth;

import static java.util.concurrent.TimeUnit.DAYS;

import java.time.temporal.ChronoUnit;


public class AppHelper {

    private static final AppHelper ourInstance = new AppHelper();

    public static AppHelper getInstance() {
        return ourInstance;
    }

    private AppHelper() {

    }


    public JSONArray getPMUDrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Village List");
            jsonObject2.put("icon", "ic_list_black");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Host Farmer List");
            jsonObject3.put("icon", "ic_list_black");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Soil Test Report");
            jsonObject4.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "District List");
            jsonObject6.put("icon", "ic_list_black");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "PD ATMA");
            jsonObject7.put("icon", "ic_list_black");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "SDAO");
            jsonObject8.put("icon", "ic_list_black");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 8);
            jsonObject9.put("name", "KVK");
            jsonObject9.put("icon", "ic_list_black");


            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("id", 9);
            jsonObject10.put("name", "CA");
            jsonObject10.put("icon", "ic_list_black");

            JSONObject jsonObject11 = new JSONObject();
            jsonObject11.put("id", 10);
            jsonObject11.put("name", "Ag Asst");
            jsonObject11.put("icon", "ic_list_black");

            JSONObject jsonObject12 = new JSONObject();
            jsonObject12.put("id", 11);
            jsonObject12.put("name", "FF");
            jsonObject12.put("icon", "ic_list_black");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject10);
            jsonArray.put(jsonObject11);
            jsonArray.put(jsonObject12);

            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getFFSDrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Host Farmer Reg");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Guest Farmer Reg");
            jsonObject3.put("icon", "ic_menu_guest_farmer_reg");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Edit Farmers");
            jsonObject4.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Village List");
            jsonObject5.put("icon", "ic_menu_village_list");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "Host Farmer List");
            jsonObject6.put("icon", "ic_menu_host_farmer_list");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Guest Farmer List");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "Inputs Used");
            jsonObject8.put("icon", "ic_menu_inputs");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 8);
            jsonObject9.put("name", "Soil Test Report");
            jsonObject9.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("id", 9);
            jsonObject10.put("name", "Yield Report");
            jsonObject10.put("icon", "ic_menu_yield_report");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject10);
            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getSDAOKVKDrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Farmers List");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Village List");
            jsonObject3.put("icon", "ic_menu_village_list");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Soil Test Report");
            jsonObject4.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");


            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "SDAO");
            jsonObject6.put("icon", "ic_menu_host_farmer_list");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "KVK");
            jsonObject7.put("icon", "ic_menu_host_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "CA");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 8);
            jsonObject9.put("name", "Ag Asst");
            jsonObject9.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("id", 9);
            jsonObject10.put("name", "FF");
            jsonObject10.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject10);
            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getAOSDAODrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Farmers List");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Village List");
            jsonObject3.put("icon", "ic_menu_village_list");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Soil Test Report");
            jsonObject4.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");


            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "SDAO");
            jsonObject6.put("icon", "ic_menu_host_farmer_list");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "CA");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "Ag Asst");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 8);
            jsonObject9.put("name", "FF");
            jsonObject9.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCADrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");


            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "CA Attendance");
            jsonObject21.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Farmers List");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Village List");
            jsonObject3.put("icon", "ic_menu_village_list");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Soil Test Report");
            jsonObject4.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "CA");
            jsonObject6.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "Ag Asst");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");

            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject21);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCADashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Attendance");
            jsonObject1.put("icon", "d_attendance_in_out");

            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "Activity");
            jsonObject21.put("icon", "d_activity");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "My Profile");
            jsonObject2.put("icon", "ic_profile");

//            JSONObject jsonObject2 = new JSONObject();
//            jsonObject2.put("id", 2);
//            jsonObject2.put("name", "Community Work");
//            jsonObject2.put("icon", "d_community_work");

//            JSONObject jsonObject3 = new JSONObject();
//            jsonObject3.put("id", 3);
//            jsonObject3.put("name", "Micro-level Planning");
//            jsonObject3.put("icon", "d_mpl");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Attendance Report");
            jsonObject3.put("icon", "d_attendance");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Sync Attendance");
            jsonObject4.put("icon", "ic_sync");

//            JSONObject jsonObject4 = new JSONObject();
//            jsonObject4.put("id", 4);
//            jsonObject4.put("name", "Training & Exposure");
//            jsonObject4.put("icon", "d_training");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Assigned Villages");
            jsonObject5.put("icon", "d_village_list");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "Approved Attendance");
            jsonObject6.put("icon", "d_attendance");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "Leave");
            jsonObject7.put("icon", "d_leave");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "My Leave");
            jsonObject8.put("icon", "d_leave");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", 9);
            jsonObject9.put("name", "Boards");
            jsonObject9.put("icon", "d_board");

            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("id", 10);
            jsonObject10.put("name", "Verification");
            jsonObject10.put("icon", "d_report");

            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject21);
            jsonArray.put(jsonObject3);
            //jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject10);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getTNMDashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Create New CA A/c");
            jsonObject1.put("icon", "d_new_ca");
            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "Activate/Deactivate CA");
            jsonObject21.put("icon", "d_activate_deactivate");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Search CA");
            jsonObject2.put("icon", "d_search_white");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Generate Attendance");
            jsonObject3.put("icon", "d_attendance");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Attendance Report");
            jsonObject4.put("icon", "d_attendance");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Total Reg CA");
            jsonObject5.put("icon", "d_group");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "CA Assigned Group");
            jsonObject6.put("icon", "d_group");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "CA Unassigned Group");
            jsonObject7.put("icon", "d_group");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject21);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            //jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getSDAODashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 1);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Search CA/Village");
            jsonObject2.put("icon", "d_search_white");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Attendance");
            jsonObject3.put("icon", "ic_attendance_report");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Leave Application");
            jsonObject4.put("icon", "d_attendance");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Approve Attendance");
            jsonObject5.put("icon", "approve_attendance");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "CA");
            jsonObject6.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "Ag Asst");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            //jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
//            jsonArray.put(jsonObject6);
//            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray getTAODashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 1);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Search CA/Village");
            jsonObject2.put("icon", "d_search_white");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Attendance");
            jsonObject3.put("icon", "ic_attendance_report");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Leave Application");
            jsonObject4.put("icon", "d_attendance");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Approve Attendance");
            jsonObject5.put("icon", "approve_attendance");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            //jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray getTCDashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 0);
            jsonObject2.put("name", "Attendance");
            jsonObject2.put("icon", "d_attendance");

            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "My Profile");
            jsonObject21.put("icon", "ic_profile");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 2);
            jsonObject1.put("name", "Manage CA");
            jsonObject1.put("icon", "ca_manage");


            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Micro-level Planning");
            jsonObject3.put("icon", "d_mpl");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Training & Exposure");
            jsonObject4.put("icon", "d_training");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");


            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject21);
//            jsonArray.put(jsonObject21);
//            jsonArray.put(jsonObject3);
//            jsonArray.put(jsonObject4);
//            jsonArray.put(jsonObject5);
//            jsonArray.put(jsonObject6);
//            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray getKTDashboardMenu() {

        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
//            jsonObject1.put("name", "Village Profile");
            jsonObject1.put("name", "प्रकल्प माहिती");
            jsonObject1.put("icon", R.drawable.logo_pocra);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
//            jsonObject2.put("name", "Activity");
            jsonObject2.put("name", "विस्तार कार्य");
            jsonObject2.put("icon", R.drawable.ktbaithak);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
//            jsonObject3.put("name", "Activity Report");
            jsonObject3.put("name", "विस्तार कार्य अहवाल");
            jsonObject3.put("icon", R.drawable.ktbaithak);

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
//            jsonObject4.put("name", "My Profile");
            jsonObject4.put("name", "प्रोफाइल");
            jsonObject4.put("icon", "ic_profile");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
//            jsonArray.put(jsonObject4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getDSAODashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Manage CA");
            jsonObject1.put("icon", "ca_manage");


            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "My Profile");
            jsonObject21.put("icon", "ic_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "CA Attendance Report");
            jsonObject2.put("icon", "d_attendance");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Micro-level Planning");
            jsonObject3.put("icon", "d_mpl");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Training & Exposure");
            jsonObject4.put("icon", "d_training");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Yield Report");
            jsonObject5.put("icon", "ic_menu_yield_report");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "CA");
            jsonObject6.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "Ag Asst");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject21);
//            jsonArray.put(jsonObject3);
//            jsonArray.put(jsonObject4);
//            jsonArray.put(jsonObject5);
//            jsonArray.put(jsonObject6);
//            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getPMUDashboardMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Reports");
            jsonObject1.put("icon", "ca_manage");


            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("id", 1);
            jsonObject21.put("name", "My Profile");
            jsonObject21.put("icon", "ic_profile");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "CA Attendance And Performance Report");
            jsonObject2.put("icon", "ic_attendance_report");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "Attendance");
            jsonObject3.put("icon", "d_attendance_in_out");


            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Attendance Report");
            jsonObject4.put("icon", "d_attendance");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 5);
            jsonObject5.put("name", "Sync Attendance");
            jsonObject5.put("icon", "ic_sync");


//            JSONObject jsonObject5 = new JSONObject();
//            jsonObject5.put("id", 5);
//            jsonObject5.put("name", "Yield Report");
//            jsonObject5.put("icon", "ic_menu_yield_report");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 6);
            jsonObject6.put("name", "CA");
            jsonObject6.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 7);
            jsonObject7.put("name", "Ag Asst");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 8);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject21);
//            jsonArray.put(jsonObject3);
//            jsonArray.put(jsonObject4);
//            jsonArray.put(jsonObject5);
//            jsonArray.put(jsonObject6);
//            jsonArray.put(jsonObject7);
//            jsonArray.put(jsonObject8);
//            jsonArray.put(jsonObject100);
//            jsonArray.put(jsonObject101);
//            jsonArray.put(jsonObject102);
//            jsonArray.put(jsonObject103);
//            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray getAgAsstDrawerMenu() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "My Profile");
            jsonObject1.put("icon", "ic_menu_profile");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Farmers List");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Village List");
            jsonObject3.put("icon", "ic_menu_village_list");


            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Input Used");
            jsonObject4.put("icon", "ic_menu_inputs");


            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Soil Test Report");
            jsonObject5.put("icon", "ic_menu_soil_test_report");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "Yield Report");
            jsonObject6.put("icon", "ic_menu_yield_report");


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Ag Asst");
            jsonObject7.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "FF");
            jsonObject8.put("icon", "ic_menu_guest_farmer_list");


            JSONObject jsonObject100 = new JSONObject();
            jsonObject100.put("id", 100);
            jsonObject100.put("name", "Census Code");
            jsonObject100.put("icon", "ic_menu_about_us");

            JSONObject jsonObject101 = new JSONObject();
            jsonObject101.put("id", 101);
            jsonObject101.put("name", "About Us");
            jsonObject101.put("icon", "ic_menu_about_us");

            JSONObject jsonObject102 = new JSONObject();
            jsonObject102.put("id", 102);
            jsonObject102.put("name", "Share Us");
            jsonObject102.put("icon", "ic_menu_share_us");

            JSONObject jsonObject103 = new JSONObject();
            jsonObject103.put("id", 103);
            jsonObject103.put("name", "Rate Us");
            jsonObject103.put("icon", "ic_menu_rate_us");

            JSONObject jsonObject104 = new JSONObject();
            jsonObject104.put("id", 104);
            jsonObject104.put("name", "Logout");
            jsonObject104.put("icon", "ic_menu_logout");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject100);
            jsonArray.put(jsonObject101);
            jsonArray.put(jsonObject102);
            jsonArray.put(jsonObject103);
            jsonArray.put(jsonObject104);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getFFSVisits() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Visit 1");
            jsonObject1.put("icon", "ic_menu_profile");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Visit 2");
            jsonObject2.put("icon", "ic_menu_host_farmer_reg");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Visit 3");
            jsonObject3.put("icon", "ic_menu_guest_farmer_reg");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 3);
            jsonObject4.put("name", "Visit 4");
            jsonObject4.put("icon", "ic_menu_village_list");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", 4);
            jsonObject5.put("name", "Visit 5");
            jsonObject5.put("icon", "ic_menu_host_farmer_list");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", 5);
            jsonObject6.put("name", "Visit 6");
            jsonObject6.put("icon", "ic_menu_guest_farmer_list");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", 6);
            jsonObject7.put("name", "Visit 7");
            jsonObject7.put("icon", "ic_menu_inputs");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", 7);
            jsonObject8.put("name", "Visit 8");
            jsonObject8.put("icon", "ic_menu_soil_test_report");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getAttendanceReportOptions() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 1);
            jsonObject1.put("name", "Today's Attendance");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Yesterday's Attendance");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "CA Wise Attendance");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public JSONArray getReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Individual Benefit Activity");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Self Help Group");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Beneficiary Report");


//            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCAReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "District");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Activity");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Gender");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCASHGReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "All");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "District");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 2);
            jsonObject3.put("name", "Sub-Division");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCASHGDistrictReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 0);
            jsonObject2.put("name", "District");


            jsonArray.put(jsonObject2);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getCASHGSubDivisionReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Sub-Division");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Sub-Division");

            jsonArray.put(jsonObject1);
//            jsonArray.put(jsonObject2);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getCASubDivisionReports() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Sub-Division");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Activity");


            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    //::TODO List of Activity For Account Officer SDAO
    public JSONArray getAOSDAOActivity() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 0);
            jsonObject1.put("name", "Individual Benefit Activity");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 1);
            jsonObject2.put("name", "Activity");


            jsonArray.put(jsonObject1);
//            jsonArray.put(jsonObject2);
//            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getAttendanceReportOptionsPMU() {

        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", 1);
            jsonObject1.put("name", "Today's Attendance");


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", 2);
            jsonObject2.put("name", "Yesterday's Attendance");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", 3);
            jsonObject3.put("name", "District Wise Attendance");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", 4);
            jsonObject4.put("name", "Graph Report");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }


    public String getReadableDate(String serverDate) {
        SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String readDate = "";
        try {
            Date date = serverFormatter.parse(serverDate);
            readDate = formatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return readDate;
    }

    public String getTimeStampSec() {
        long milliSec = System.currentTimeMillis() / 1000;
        return String.valueOf(milliSec);
    }

    public String getTimeStampReadable(long dateTimeStamp) {

//        Timestamp stamp = new Timestamp(Long.parseLong(dateTimeStamp));
//        Date date = new Date(stamp.getTime());

       /* if (dateTimeStamp.isEmpty()) {
            return "";

        } else {
            int timestamp = Integer.parseInt(dateTimeStamp);
            Date dateObj = new Date(timestamp/1000);
//            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a", Locale.getDefault());
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            return df.format(dateObj);
        }*/
//        long timestamp = Long.valueOf(dateTimeStamp);
        return getDateFromMilliSec(dateTimeStamp, "dd-MM-yyyy hh:mm a");

    }


    public String getVerificationCode(String message) {
        String code = null;

        int start = 0;
        int length = 6;
        code = message.substring(start, start + length);
        return code.trim().replace(" ", "");

        //int index = message.indexOf(com.tatamotors.tma.app_util.AppConstants.kDELIMITER);
        /*if (index != -1) {
            int start = 0;
            int length = 6;
            code = message.substring(start, start + length);
            //return code;
        }*/
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(yesterday());
    }


    private Date getMeYesterday() {
        return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }


    public String getDaysFromTwoDates(String userDate) {

        String dayDifference = null;

        try {
            //Dates to compare
//            String CurrentDate=  "09/24/2015";
//            userDate =  "09-24-2015";
//            String FinalDate=  "09-26-2015";

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            String currentDateString = dates.format(new Date());
            Log.e("HERE", "currentDateString: " + currentDateString);


            //Setting dates
            date1 = dates.parse(userDate);
            date2 = dates.parse(currentDateString);
//            date2 = dates.parse(FinalDate);

            //Comparing dates
//            long difference = Math.abs(date1.getTime() - date2.getTime());
            long difference = Math.abs(date2.getTime() - date1.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            dayDifference = Long.toString(differenceDates);

            Log.e("HERE", "HERE: " + dayDifference);
        } catch (Exception exception) {
            Log.e("DIDN'T WORK", "exception " + exception);
        }

        return dayDifference;

    }


    public String getTodayDate() {

        SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String readDate = "";
        try {
            readDate = serverFormatter.format(new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return readDate;
    }

    public String getYesterdayDate() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        String readDate = "";
        try {
            readDate = dateFormat.format(cal.getTime()); //your formatted date here

        } catch (Exception e) {
            e.printStackTrace();
        }

        return readDate;
    }


    public String getJoiningDate(String joiningDate) {

        if (joiningDate == null || joiningDate.isEmpty()) {
            return "";

        } else {
            long time = Long.parseLong(joiningDate);

            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date currentTimeZone = new Date(time * 1000);

            return simpleDateFormat.format(currentTimeZone);
        }
    }


    public String getAttendanceDate(String serverDate) {

        SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String readDate = "";

        Date date = null;
        try {
            date = serverFormatter.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        readDate = formatter.format(date);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String monthName = simpleDateFormat.format(calendar.getTime());

        String dateArr[] = readDate.split("-");
        String dateString = dateArr[0];

        //String cal = dateString + " " + monthName;

        DebugLog.getInstance().d("Calendar" + dateString);

        return dateString;

    }


    public String getAttendanceMonth(String serverDate) {

        SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String readDate = "";

        Date date = null;
        try {
            date = serverFormatter.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        readDate = formatter.format(date);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-  yyyy", Locale.getDefault());
        String monthName = simpleDateFormat.format(calendar.getTime());

//        String dateArr[] = readDate.split("-");
//        String dateString = dateArr[0];

        //String cal = dateString + " " + monthName;

        DebugLog.getInstance().d("Calendar" + monthName);

        return monthName;

    }


    public String getTime(long dateTimeStamp) {

//        long milliSec = (long)dateTimeStamp;

        String fullDate = getDateFromMilliSec(dateTimeStamp, "dd-MM-yyyy hh:mm a"); //df.format(dateObj);

        String timeArr[] = fullDate.split(" ");

        return timeArr[1] + " " + timeArr[2];
    }

    public String getDateFromMilliSec(long milliSeconds, String dateFormat) {

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public String getDateTimeByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
    }


    public Address getLatLngAddress(Context context, Location location) {
        Address address = null;
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            DebugLog.getInstance().e("" + ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            DebugLog.getInstance().e("Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude() + "illegalArgumentException=" + illegalArgumentException);
        }

        if (addressList != null && addressList.size() > 0) {
            address = addressList.get(0);
        }

        return address;
    }


    public String calculateTotalHours(long inTimes, long outTimes) {

        String total = "0_0";
        String dateStart = AppHelper.getInstance().getDateFromMilliSec(inTimes, "MM/dd/yyyy HH:mm:ss"); //"08/11/2016 09:29:58";
        String dateStop = AppHelper.getInstance().getDateFromMilliSec(outTimes, "MM/dd/yyyy HH:mm:ss");  //"08/12/2016 10:31:48";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

//            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;

//            long diffSeconds = diff / 1000 % 60;
//            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
//            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*DebugLog.getInstance().d(  "sma days, "+diffDays);
            DebugLog.getInstance().d( "sma hours, "+diffHours);
            DebugLog.getInstance().d( "sma minutes, "+diffMinutes);
            DebugLog.getInstance().d(  "sma seconds."+diffSeconds);*/
            total = String.format("%d_%d", diffHours, diffMinutes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public long getCalculatedTotalHours(long inTimes, long outTimes) {

        long total = 0;
        String dateStart = AppHelper.getInstance().getDateFromMilliSec(inTimes, "MM/dd/yyyy HH:mm:ss"); //"08/11/2016 09:29:58";
        String dateStop = AppHelper.getInstance().getDateFromMilliSec(outTimes, "MM/dd/yyyy HH:mm:ss");  //"08/12/2016 10:31:48";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

//            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
//            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*DebugLog.getInstance().d(  "sma days, "+diffDays);
            DebugLog.getInstance().d( "sma hours, "+diffHours);
            DebugLog.getInstance().d( "sma minutes, "+diffMinutes);
            DebugLog.getInstance().d(  "sma seconds."+diffSeconds);*/
            total = diffHours;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public long getCalculatedTotalMins(long inTimes, long outTimes) {

        long total = 0;
        String dateStart = AppHelper.getInstance().getDateFromMilliSec(inTimes, "MM/dd/yyyy HH:mm:ss"); //"08/11/2016 09:29:58";
        String dateStop = AppHelper.getInstance().getDateFromMilliSec(outTimes, "MM/dd/yyyy HH:mm:ss");  //"08/12/2016 10:31:48";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

//            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
//            long diffHours = diff / (60 * 60 * 1000) % 24;
//            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*DebugLog.getInstance().d(  "sma days, "+diffDays);
            DebugLog.getInstance().d( "sma hours, "+diffHours);
            DebugLog.getInstance().d( "sma minutes, "+diffMinutes);
            DebugLog.getInstance().d(  "sma seconds."+diffSeconds);*/
            total = diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public String getCurrentDateYYYYMMDD() {
        SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String readDate = "";
        try {
            readDate = serverFormatter.format(new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return readDate;
    }


    public String getMonthNumName() {

        String monthNum = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.getDefault());
        String month_name = month_date.format(calendar.getTime());

        if (month_name.equalsIgnoreCase("Jan")) {
            monthNum = "1";

        } else if (month_name.equalsIgnoreCase("Feb")) {
            monthNum = "2";

        } else if (month_name.equalsIgnoreCase("Mar")) {
            monthNum = "3";

        } else if (month_name.equalsIgnoreCase("Apr")) {
            monthNum = "4";

        } else if (month_name.equalsIgnoreCase("May")) {
            monthNum = "5";

        } else if (month_name.equalsIgnoreCase("Jun")) {
            monthNum = "6";

        } else if (month_name.equalsIgnoreCase("Jul")) {
            monthNum = "7";

        } else if (month_name.equalsIgnoreCase("Aug")) {
            monthNum = "8";

        } else if (month_name.equalsIgnoreCase("Sep")) {
            monthNum = "9";

        } else if (month_name.equalsIgnoreCase("Oct")) {
            monthNum = "10";

        } else if (month_name.equalsIgnoreCase("Nov")) {
            monthNum = "11";

        } else if (month_name.equalsIgnoreCase("Dec")) {
            monthNum = "12";
        }

        return monthNum;
    }

    public String getMonthName(String monthName) {

        String monthNum = "";

        if (monthName.equalsIgnoreCase("01")) {
            monthNum = "Jan";

        } else if (monthName.equalsIgnoreCase("02")) {
            monthNum = "Feb";

        } else if (monthName.equalsIgnoreCase("03")) {
            monthNum = "Mar";

        } else if (monthName.equalsIgnoreCase("04")) {
            monthNum = "Apr";

        } else if (monthName.equalsIgnoreCase("05")) {
            monthNum = "May";

        } else if (monthName.equalsIgnoreCase("06")) {
            monthNum = "Jun";

        } else if (monthName.equalsIgnoreCase("07")) {
            monthNum = "Jul";

        } else if (monthName.equalsIgnoreCase("08")) {
            monthNum = "Aug";

        } else if (monthName.equalsIgnoreCase("09")) {
            monthNum = "Sep";

        } else if (monthName.equalsIgnoreCase("10")) {
            monthNum = "Oct";

        } else if (monthName.equalsIgnoreCase("11")) {
            monthNum = "Nov";

        } else if (monthName.equalsIgnoreCase("12")) {
            monthNum = "Dec";
        }

        return monthNum;
    }


    public int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public JSONArray getcurrentPriviousMonthFilterArray(String year) {

        JSONArray jsonArray = new JSONArray();
        try {
            int selectedYear = Integer.parseInt(year);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
            String currentMonthName = month_date.format(calendar.getTime());
            int currentYear = calendar.get(Calendar.YEAR);
            Log.d("selectedtyear", selectedYear + "" + currentYear);
            if (selectedYear < currentYear) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", 1);
                jsonObject.put("name", "December " + selectedYear);
                jsonObject.put("month", "December");
                jsonObject.put("year", selectedYear);
                jsonObject.put("is_selected", 0);

                jsonArray.put(jsonObject);
            } else {
                Log.d("currentMonthName", currentMonthName);

                int[] months = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};


                Calendar calendar1 = Calendar.getInstance();
                calendar1.clear();
                calendar1.setTimeInMillis(System.currentTimeMillis());
                Log.d("SystemcurrentTimeMillis", String.valueOf(System.currentTimeMillis()));
                Log.d("Calendar.DATE", String.valueOf(Calendar.DATE));
                while (calendar1.get(Calendar.DATE) > 1) {
                    calendar1.add(Calendar.DATE, -1)
                    ; // Substract 1 day until first day of month.
                }
                long firstDayOfMonthTimestamp = calendar1.getTimeInMillis();
                Log.d("firstDayOfMonthTimes", String.valueOf(firstDayOfMonthTimestamp));


                for (int i = 0; i < months.length; i++) {


                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                    cal.setTimeInMillis(firstDayOfMonthTimestamp);
                    cal.set(Calendar.MONTH, months[i]);

                    String month_name = simpleDateFormat.format(cal.getTime());
                    String arr[] = month_name.split(" ");
                    String listCurrentMonth = arr[0];
                    Log.d("month_name", month_name);
                    Log.d("arrgdsfgdsfg", String.valueOf(arr));
                    Log.d("arrgsgsdgsgf", arr[0]);
                    Log.d("arr534534534fgdsfgdsfg", arr[1]);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", i + 1);
                    jsonObject.put("name", month_name);
                    jsonObject.put("month", listCurrentMonth);
                    jsonObject.put("year", arr[1]);
                    jsonObject.put("is_selected", 0);

                    jsonArray.put(jsonObject);
                    System.out.println("month_name=" + month_name);

                    if (currentMonthName.equalsIgnoreCase(listCurrentMonth)) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public String convertToMarathiMonth(String englishDate) {
        try {
            // English → Marathi month map
            Map<String, String> monthMap = new HashMap<>();
            monthMap.put("January", "जानेवारी");
            monthMap.put("February", "फेब्रुवारी");
            monthMap.put("March", "मार्च");
            monthMap.put("April", "एप्रिल");
            monthMap.put("May", "मे");
            monthMap.put("June", "जून");
            monthMap.put("July", "जुलै");
            monthMap.put("August", "ऑगस्ट");
            monthMap.put("September", "सप्टेंबर");
            monthMap.put("October", "ऑक्टोबर");
            monthMap.put("November", "नोव्हेंबर");
            monthMap.put("December", "डिसेंबर");

            // Split: "October 2025"
            String[] parts = englishDate.split(" ");
            String monthEN = parts[0];
            String yearEN = parts[1];

            // Convert month
            String monthMR = monthMap.get(monthEN);

            // Convert year digits English→Marathi
            String marathiYear = convertDigitsToMarathi(yearEN);

            return monthMR + " " + marathiYear;

        } catch (Exception e) {
            e.printStackTrace();
            return englishDate;
        }
    }

    public String convertDigitsToMarathi(String number) {
        char[] marathiDigits = {'०', '१', '२', '३', '४', '५', '६', '७', '८', '९'};
        StringBuilder result = new StringBuilder();

        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(marathiDigits[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    public JSONArray getMonthFilterArray() {

        JSONArray jsonArray = new JSONArray();

        try {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
            String currentMonthName = month_date.format(calendar.getTime());
            Log.d("currentMonthName", currentMonthName);

            int[] months = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};


            Calendar calendar1 = Calendar.getInstance();
            calendar1.clear();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            Log.d("SystemcurrentTimeMillis", String.valueOf(System.currentTimeMillis()));
            Log.d("Calendar.DATE", String.valueOf(Calendar.DATE));
            while (calendar1.get(Calendar.DATE) > 1) {
                calendar1.add(Calendar.DATE, -1)
                ; // Substract 1 day until first day of month.
            }
            long firstDayOfMonthTimestamp = calendar1.getTimeInMillis();
            Log.d("firstDayOfMonthTimes", String.valueOf(firstDayOfMonthTimestamp));


            for (int i = 0; i < months.length; i++) {


                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                cal.setTimeInMillis(firstDayOfMonthTimestamp);
                cal.set(Calendar.MONTH, months[i]);

                String month_name = simpleDateFormat.format(cal.getTime());
                String arr[] = month_name.split(" ");
                String listCurrentMonth = arr[0];
                Log.d("month_name", month_name);
                Log.d("arrgdsfgdsfg", String.valueOf(arr));
                Log.d("arrgsgsdgsgf", arr[0]);
                Log.d("arr534534534fgdsfgdsfg", arr[1]);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", i + 1);
                jsonObject.put("name", month_name);
                jsonObject.put("month", listCurrentMonth);
                jsonObject.put("year", arr[1]);
                jsonObject.put("is_selected", 0);

                jsonArray.put(jsonObject);
                System.out.println("month_name=" + month_name);

                if (currentMonthName.equalsIgnoreCase(listCurrentMonth)) {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getFutureMonthArray() {

        JSONArray jsonArray = new JSONArray();

        try {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);

//            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
            String currentMonthName = month_date.format(calendar.getTime());

            System.out.println("currentMonthName=" + currentMonthName);


            int[] months = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};


            Calendar calendar1 = Calendar.getInstance();
            calendar1.clear();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            while (calendar1.get(Calendar.DATE) > 1) {
                calendar1.add(Calendar.DATE, -1); // Substract 1 day until first day of month.
            }
            long firstDayOfMonthTimestamp = calendar1.getTimeInMillis();


            for (int i = 0; i < months.length; i++) {


                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                cal.setTimeInMillis(firstDayOfMonthTimestamp);
                cal.set(Calendar.MONTH, months[i]);

                String month_name = simpleDateFormat.format(cal.getTime());
                String arr[] = month_name.split(" ");
                String listCurrentMonth = arr[0];

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", i + 1);
                jsonObject.put("name", month_name);
                jsonObject.put("month", listCurrentMonth);
                jsonObject.put("year", arr[1]);
                jsonObject.put("is_selected", 0);

                jsonArray.put(jsonObject);
                System.out.println("month_name=" + month_name);

                if (currentMonthName.equalsIgnoreCase(listCurrentMonth)) {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    /**
     * Get a diff between two dates
     *
     * @param oldDate the old date
     * @param newDate the new date
     * @return the diff value, in the days
     * <p>
     * TODO usage
     * int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), "29/05/2017", "31/05/2017");
     * System.out.println("dateDifference: " + dateDifference);
     *
     */
    public long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {

            return DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public int getDaysBetween(int startDate, int endDate) {
        Date start = getTimeStamp(startDate);
        Date end = getTimeStamp(endDate);

        return daysBetween(start, end);

    }

    private Date getTimeStamp(int dateString) {

        long unixSeconds = (long) dateString;
// convert seconds to milliseconds
        return new Date(unixSeconds * 1000L);

       /* Timestamp timestamp = null;
        String date = getTimeStampReadable(Long.parseLong(timeStamp));
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.getDefault());
            Date parsedDate = dateFormat.parse(date);
               timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
        }

        return timestamp;*//*
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  new Timestamp(date.getTime());*/

    }

    public boolean isMockLocationOn(Location location) {
        return location.isFromMockProvider();
    }

    public long getTimeStampFromDate(String fromDate) {
        long timestamp = 0L;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date1 = format.parse(fromDate);
            timestamp = date1.getTime();
            DebugLog.getInstance().d("timestamp=" + timestamp);
        } catch (ParseException var6) {
            var6.printStackTrace();
        }

        return timestamp / 1000L;
    }


}


