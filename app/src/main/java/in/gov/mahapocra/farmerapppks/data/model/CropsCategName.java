package in.gov.mahapocra.farmerapppks.data.model;

public class CropsCategName {
    private int id;
    private String mName;
    private String mUrl;
    private String wotr_id;
    private String sowing_date_general;

    public CropsCategName(int id, String mName, String mUrl, String wotrId, String sowing_date_general) {
        this.id = id;
        this.mName = mName;
        this.mUrl = mUrl;
        this.wotr_id = wotrId;
        this.sowing_date_general = sowing_date_general;
    }

    public CropsCategName(int id, String mName, String mUrl, String wotrId) {
        this.id = id;
        this.mName = mName;
        this.mUrl = mUrl;
        this.wotr_id = wotrId;
    }

    public String getSowing_date_general() {
        return sowing_date_general;
    }

    public void setSowing_date_general(String sowing_date_general) {
        this.sowing_date_general = sowing_date_general;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getWotr_id() {
        return wotr_id;
    }

    public void setWotr_id(String wotr_id) {
        this.wotr_id = wotr_id;
    }
}