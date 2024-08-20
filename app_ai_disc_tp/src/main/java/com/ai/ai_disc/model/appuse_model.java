package com.ai.ai_disc.model;

import com.ai.ai_disc.user_singleton;

import org.json.JSONArray;

import java.util.ArrayList;

public class appuse_model {

private appuse_model(){};

    private int user_id;

    public int getuser_id() {
        return user_id;
    }

    public void setuser_id(int user_id) {
        this.user_id = user_id;
    }

    private String lat;

    public String getlat() {
        return lat;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    //////
    private String lon;

    public String getlon() {
        return lon;
    }

    public void setlon(String lon) {
        this.lon = lon;
    }

    private static appuse_model instance;

    public static appuse_model getInstance(){


        if(instance==null){
            instance = new appuse_model();
        }

        return instance;
    }

}

