package com.ai.ai_disc;


public class temp1 {


    private  temp1(){
    }

    private String  record_id;

    private static temp1 instance;
    public static temp1 getInstance(){

        if(instance==null){
            instance= new temp1();
        }
        return  instance;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }
}