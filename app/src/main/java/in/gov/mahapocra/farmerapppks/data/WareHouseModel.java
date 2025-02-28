package in.gov.mahapocra.farmerapppks.data;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class WareHouseModel {
    private String warehouse_name;
    private String recorded_date;
    private String village;
    private String phone;
    private String total_capacity;
    private String available_capacity;
    private String warehouse_code;
    private JSONObject jsonObject;


    public WareHouseModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getWarehouseName() {
        warehouse_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "warehouse_name");
        return warehouse_name;
    }
    public String getRecordedDate() {
        recorded_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "recorded_date");
        return recorded_date;
    }
    public String getVillage() {
        village = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "village");
        return village;
    }

    public String getPhone() {
        phone = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "phone");
        return phone;
    }

    public String getTotalCapacity() {
        total_capacity = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "total_capacity");
        return total_capacity;
    }

    public String getAvailableCapacity() {
        available_capacity = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "available_capacity");
        return available_capacity;
    }

    public String getWarehouseCode() {
        warehouse_code = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "warehouse_code");
        return warehouse_code;
    }
}
