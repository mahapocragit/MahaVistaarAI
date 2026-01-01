package in.gov.mahapocra.mahavistaarai.sma;

import com.google.gson.annotations.SerializedName;

public class KTReportDetailsModel {

    private String id;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("subcategory_name")
    private String subcategoryName;

    @SerializedName("farmer_type")
    private String farmerType;

    @SerializedName("farmer_id")
    private String farmerId;

    @SerializedName("technology_type")
    private String technologyType;

    @SerializedName("farmer_group_name")
    private String farmerGroupName;

    @SerializedName("discussion_name")
    private String discussionName;

    private String description;

    private String image;

    @SerializedName("category_type")
    private String categoryType;
    @SerializedName("category_type_mr")
    private String categoryTypeMr;

    @SerializedName("date")
    private String date;

    @SerializedName("register_name")
    private String registerName;
    @SerializedName("mobile_number")
    private String mobileNumber;

    public KTReportDetailsModel(String id, String categoryId, String categoryName,
                                String subcategoryName, String farmerType, String farmerId,
                                String technologyType, String farmerGroupName,
                                String discussionName, String description, String image,
                                String categoryType, String categoryTypeMr,String date) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.farmerType = farmerType;
        this.farmerId = farmerId;
        this.technologyType = technologyType;
        this.farmerGroupName = farmerGroupName;
        this.discussionName = discussionName;
        this.description = description;
        this.image = image;
        this.categoryType = categoryType;
        this.categoryTypeMr = categoryTypeMr;
        this.date = date;
    }
    // Constructor
    public KTReportDetailsModel(String id, String categoryId, String categoryName,
                                String subcategoryName, String farmerType, String farmerId,
                                String technologyType, String farmerGroupName,
                                String discussionName, String description, String image,
                                String categoryType, String registerName,
                                String mobileNumber, String categoryTypeMr,String date) {

        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.farmerType = farmerType;
        this.farmerId = farmerId;
        this.technologyType = technologyType;
        this.farmerGroupName = farmerGroupName;
        this.discussionName = discussionName;
        this.description = description;
        this.image = image;
        this.categoryType = categoryType;
        this.registerName = registerName;
        this.mobileNumber = mobileNumber;
        this.categoryTypeMr = categoryTypeMr;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public String getFarmerType() {
        return farmerType;
    }

    public void setFarmerType(String farmerType) {
        this.farmerType = farmerType;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getTechnologyType() {
        return technologyType;
    }

    public void setTechnologyType(String technologyType) {
        this.technologyType = technologyType;
    }

    public String getFarmerGroupName() {
        return farmerGroupName;
    }

    public void setFarmerGroupName(String farmerGroupName) {
        this.farmerGroupName = farmerGroupName;
    }

    public String getDiscussionName() {
        return discussionName;
    }

    public void setDiscussionName(String discussionName) {
        this.discussionName = discussionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCategoryTypeMr() {
        return categoryTypeMr;
    }

    public void setCategoryTypeMr(String categoryTypeMr) {
        this.categoryTypeMr = categoryTypeMr;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
