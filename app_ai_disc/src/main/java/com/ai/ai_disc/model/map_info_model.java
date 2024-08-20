package com.ai.ai_disc.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class map_info_model {



    private ArrayList<String> cropnamed;

    public ArrayList<String> getcrops() {
        return cropnamed;
    }

    public void setcrops(ArrayList<String>cropname) {
        this.cropnamed = cropname;
    }

    private ArrayList<String> crop_idd;

    public ArrayList<String> getcrop_id() {
        return crop_idd;
    }

    public void setcrop_id(ArrayList<String>crop_id) {
        this.crop_idd = crop_id;
    }

    //////
    private ArrayList<String> cropnamedp;

    public ArrayList<String> getcropsp() {
        return cropnamedp;
    }

    public void setcropsp(ArrayList<String>cropname) {
        this.cropnamedp = cropname;
    }

    private ArrayList<String> crop_iddp;

    public ArrayList<String> getcrop_idp() {
        return crop_iddp;
    }

    public void setcrop_idp(ArrayList<String>crop_id) {
        this.crop_iddp = crop_id;
    }








    private ArrayList<JSONArray> disname;

    public ArrayList<JSONArray> getdisname() {
        return disname;
    }

    public void setdisname(ArrayList<JSONArray>valdis) {
        this.disname = valdis;
    }

    private ArrayList<JSONArray> pestname;

    public ArrayList<JSONArray> getpestname() {
        return pestname;
    }

    public void setpestname(ArrayList<JSONArray>valdis) {
        this.pestname = valdis;
    }



    private ArrayList<JSONArray> disid;

    public ArrayList<JSONArray> getdisid() {
        return disid;
    }

    public void setdisid(ArrayList<JSONArray>valdis) {
        this.disid = valdis;
    }

    private ArrayList<JSONArray> pestid;

    public ArrayList<JSONArray> getpestid() {
        return pestid;
    }

    public void setpestid(ArrayList<JSONArray>valdis) {
        this.pestid = valdis;
    }

}

