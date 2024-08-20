package com.ai.ai_disc.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class report_info_model {



    private ArrayList<String> cropname;
    private ArrayList<String> cropnameid;
    private ArrayList<JSONArray> dpid;
    private ArrayList<JSONArray> dpnames;


    public ArrayList<String> getcropid() {
        return cropnameid;
    }

    public void setcropid(ArrayList<String>cropname_dis) {
        this.cropnameid = cropname_dis;
    }

    public ArrayList<String> getcropname() {
        return cropname;
    }

    public void setcropname(ArrayList<String>cropname_pest) {
        this.cropname = cropname_pest;
    }



    public ArrayList<JSONArray> getdpid() {
        return dpid;
    }

    public void setdpid(ArrayList<JSONArray>dpid) {
        this.dpid = dpid;
    }
    public ArrayList<JSONArray> getdpnames() {
        return dpnames;
    }

    public void setdpnames(ArrayList<JSONArray>cropname_pest) {
        this.dpnames = cropname_pest;
    }
}

