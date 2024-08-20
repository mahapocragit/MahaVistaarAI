package com.ai.ai_disc;

import java.util.ArrayList;

public class temp {

   // static ArrayList<Image_Record> list ;
   private  temp(){

   }

    private ArrayList<Image_Record> list;



    private static temp instance;
    public static temp getInstance(){

        if(instance==null){
            instance= new temp();
        }
        return  instance;
    }

    public ArrayList<Image_Record> getList() {
        return list;
    }

    public void setList(ArrayList<Image_Record> list) {
        this.list = list;
    }
}
