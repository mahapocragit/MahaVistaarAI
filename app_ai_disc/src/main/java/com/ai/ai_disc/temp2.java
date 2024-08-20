package com.ai.ai_disc;

import java.util.ArrayList;

public class temp2 {

    private  temp2(){
    }

    private ArrayList<String> latitude;
    private ArrayList<String> longitude;

    private static temp2 instance;
    public static temp2 getInstance(){

        if(instance==null){
            instance= new temp2();
        }
        return  instance;
    }

    public ArrayList<String> getLatitude() {
        return latitude;
    }

    public void setLatitude(ArrayList<String> latitude) {
        this.latitude = latitude;
    }

    public ArrayList<String> getLongitude() {
        return longitude;
    }

    public void setLongitude(ArrayList<String> longitude) {
        this.longitude = longitude;
    }
}
