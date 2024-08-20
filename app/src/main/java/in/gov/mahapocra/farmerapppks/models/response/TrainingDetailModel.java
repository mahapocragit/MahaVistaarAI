package in.gov.mahapocra.farmerapppks.models.response;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class TrainingDetailModel {


    private String id;
    private String role_id;
    private String event_type;
    private String event_type_name;
    private String event_sub_type_id;
    private String event_sub_type_name;
    private String title;
    private String participints;
    private String start_date;
    private String end_date;
    private String event_start_time;
    private String event_end_time;
    private String reporting_date;
    private String reporting_time;
    private String district_id;
    private String district_name;
    private String census_code;
    private String village_name;
    private String venue;
    private String venue_name;
    private String other_venue;
    private JSONArray session;
    private JSONArray gp;
    private JSONArray village;
    private JSONArray facilitator;
    private JSONArray other;
    private JSONArray resource_person;
    private JSONArray ca_resource_person;
    private JSONArray co_coordinators;
    private JSONArray coordinators;

    private JSONArray shg;
    private JSONArray fpc;
    private JSONArray farmer_group;


    private JSONObject jsonObject;
    public TrainingDetailModel(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public String getId() {
        return id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");
    }

    public String getRole_id() {
        return role_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "role_id");
    }


    public String getEvent_type() {
        return event_type = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_type");
    }

    public String getEvent_type_name() {
        return event_type_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_type_name");
    }

    public String getEvent_sub_type_id() {
        return event_sub_type_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_sub_type_id");
    }

    public String getEvent_sub_type_name() {
        return event_sub_type_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_sub_type_name");
    }

    public String getTitle() {
        return title = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "title");
    }

    public String getParticipints() {
        return participints = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "participints");
    }

    public String getStart_date() {
        return start_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "start_date");
    }

    public String getEnd_date() {
        return end_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "end_date");
    }

    public String getEvent_start_time() {
        return event_start_time = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_start_time");
    }

    public String getEvent_end_time() {
        return event_end_time = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_end_time");
    }

    public String getReporting_date() {
        return reporting_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "reporting_date");
    }

    public String getReporting_time() {
        return reporting_time = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "reporting_time");
    }

    public String getDistrict_id() {
        return district_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "district_id");
    }

    public String getDistrict_name() {
        return district_name  = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "district_name");
    }

    public String getCensus_code() {
        return census_code  = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "census_code");
    }

    public String getVillage_name() {
        return village_name  = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "village_name");
    }

    public String getVenue() {
        return venue = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "venue");
    }

    public String getVenue_name() {
        return venue_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "venue_name");
    }

    public String getOther_venue() {
        return other_venue = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "other_venue");
    }

    public JSONArray getVillage() {
        return village = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "village");
    }

    public JSONArray getFacilitator() {
        return facilitator = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "facilitator");
    }

    public JSONArray getOther() {
        return other  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "other");
    }
    public JSONArray getShg() {
        return shg  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "shg");
    }
    public JSONArray getFPC() {
        return fpc  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "fpc");
    }
    public JSONArray getFR() {
        return farmer_group  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "FR");
    }
    public JSONArray getSession() {
        return session = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "session");
    }

    public JSONArray getGp() {
        return gp = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "gp");
    }

    public JSONArray getCo_coordinators() {
        return co_coordinators = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "co_coordinators");
    }


    public JSONArray getResource_person() {
        return resource_person = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "resource_person");
    }


    public JSONArray getCoordinators() {
        return coordinators = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "coordinators");
    }


    public JSONArray getCa_resource_person() {
        return ca_resource_person = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "ca_resource_person");
    }

    public JSONArray getSHGEditMode() {
        return shg  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "SHG");
    }
    public JSONArray getFPCEditMode() {
        return fpc  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "FPC");
    }
    public JSONArray getFREditMode() {
        return farmer_group  = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "Farmers_group");
    }
}
