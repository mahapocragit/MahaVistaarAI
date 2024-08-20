package com.ai.ai_disc.Farmer;

public class Model_expert_query_content
{
    private String Query_id;
    private String farmer_name;
    private String farmer_address;
    private String expert_nmae;
    private String expert_address;
    private String farmer_query;
    private String img_path;
    private String query_resolution;
    private String query_status;
    private String  cropID;
    private String farmerId;
    private String userType;
    private String crop_name;
    private String times;
    private String status_run;

    public String getstatus_run() {
        return status_run;
    }
    public void setQuery_id(String Query_) {
        Query_id = Query_;
    }

    public String getQuery_id() {
        return Query_id;
    }
    public void setstatus_run(String status) {
        status_run = status;
    }

    public String getcrop_name() {
        return crop_name;
    }

    public void setcrop_name(String crop_) {
        crop_name = crop_;
    }

    public String gettimes() {
        return times;
    }

    public void settimes(String query_id) {
        times = query_id;
    }


    public String getFarmer_name() {
        return farmer_name;
    }

    public void setFarmer_name(String farmer_name) {
        this.farmer_name = farmer_name;
    }

    public String getFarmer_address() {
        return farmer_address;
    }

    public void setFarmer_address(String farmer_address) {
        this.farmer_address = farmer_address;
    }

    public String getExpert_nmae() {
        return expert_nmae;
    }

    public void setExpert_nmae(String expert_nmae) {
        this.expert_nmae = expert_nmae;
    }

    public String getExpert_address() {
        return expert_address;
    }

    public void setExpert_address(String expert_address) {
        this.expert_address = expert_address;
    }

    public String getFarmer_query() {
        return farmer_query;
    }

    public void setFarmer_query(String farmer_query) {
        this.farmer_query = farmer_query;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getQuery_resolution() {
        return query_resolution;
    }

    public void setQuery_resolution(String query_resolution) {
        this.query_resolution = query_resolution;
    }

    public String getQuery_status() {
        return query_status;
    }

    public void setQuery_status(String query_status) {
        this.query_status = query_status;
    }
    public String getCropID() {
        return cropID;
    }

    public void setCropID(String cropID) {
        this.cropID = cropID;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


}
