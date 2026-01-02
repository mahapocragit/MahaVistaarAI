package in.gov.mahapocra.mahavistaarai.sma.data.models;

import org.json.JSONObject;
import in.co.appinventor.services_api.app_util.AppUtility;
public class CalendarModelKT {

        private String date;
        private String category_type;
        private String category_type_mr;
        private int is_present;
        private int is_holiday;
        private String holiday_details;

        private JSONObject jsonObject;

        public CalendarModelKT(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public String getDate() {
            date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "date");
            return date;
        }

        public String getCategory_type() {
            category_type = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "category_type");
            return category_type;
        }

    public String getCategoryTypeMr() {
        category_type_mr = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "category_type_mr");
        return category_type_mr;
    }



        public int getIs_present() {
            is_present = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_present");
            return is_present;
        }

        public int getIs_holiday() {
            is_holiday = AppUtility.getInstance().sanitizeIntJSONObj(this.jsonObject, "is_holiday");
            return is_holiday;
        }

        public String getHoliday_details() {
            holiday_details = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "holiday_details");
            return holiday_details;
        }
    }

