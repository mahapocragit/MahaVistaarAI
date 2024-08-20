package com.ai.ai_disc.model;

import java.util.ArrayList;

public class identifier_model_croplist {



    private ArrayList<String> cropname_dis;
    private ArrayList<String> cropname_pest;
    public ArrayList<String> getcropsd() {
        return cropname_dis;
    }

    public void setcropsd(ArrayList<String>cropname_dis) {
        this.cropname_dis = cropname_dis;
    }

    public ArrayList<String> getcropsp() {
        return cropname_pest;
    }

    public void setcropsp(ArrayList<String>cropname_pest) {
        this.cropname_pest = cropname_pest;
    }
}

