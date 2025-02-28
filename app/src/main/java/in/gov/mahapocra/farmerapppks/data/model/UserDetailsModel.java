package in.gov.mahapocra.farmerapppks.data.model;

import org.json.JSONObject;

public class UserDetailsModel {

           private String Name;
           private String MobileNo;
           private String EmailId;
           private String FAAPRegistrationID;
           private String DistrictName;
           private String  DistrictID;
           private String TalukaName;
           private String TalukaID;
           private JSONObject jsonObject;

    public String getName() {
//        Name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "Name");
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getMobileNo() {
//        MobileNo = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "MobileNo");
        return MobileNo;
    }
    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getEmailId() {
//        EmailId = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "EmailId");
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getFAAPRegistrationID() {
//        FAAPRegistrationID = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "FAAPRegistrationID");
        return FAAPRegistrationID;
    }

    public void setFAAPRegistrationID(String FAAPRegistrationID) {
        this.FAAPRegistrationID = FAAPRegistrationID;
    }

    public String getDistrictName() {
//        DistrictName = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "DistrictName");
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }

    public String getDistrictID() {
//        DistrictID = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "DistrictID");
        return DistrictID;
    }

    public void setDistrictID(String districtID) {
        DistrictID = districtID;
    }

    public String getTalukaName() {
//        TalukaName = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "TalukaName");
        return TalukaName;
    }

    public void setTalukaName(String talukaName) {
        TalukaName = talukaName;
    }

    public String getTalukaID() {
//        TalukaID = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "TalukaID");
        return TalukaID;
    }

    public void setTalukaID(String talukaID) {
        TalukaID = talukaID;
    }

}
