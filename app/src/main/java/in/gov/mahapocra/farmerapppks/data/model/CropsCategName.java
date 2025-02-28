package in.gov.mahapocra.farmerapppks.data.model;

public class CropsCategName {
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

    private int id;
    private String mName;
    private String mUrl;
    private String wotr_id;

    public CropsCategName(int id, String mName, String mUrl, String wotrId) {
        this.id = id;
        this.mName = mName;
        this.mUrl = mUrl;
        this.wotr_id = wotrId;

    }




    // Glide.with(context).load(bannerMoviesList.get(position).getImageUrl()).into(bannerImage)





}