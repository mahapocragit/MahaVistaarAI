package com.ai.ai_disc;

import com.ai.ai_disc.model.reportdata;

public class report_singleton {


    private report_singleton(){

    };

    private reportdata data;


    public reportdata getdata() {
        return data;
    }

    public void setdata(reportdata data) {
        this.data = data;
    }




    private static report_singleton instance;

      public static report_singleton getInstance(){


          if(instance==null){
              instance = new report_singleton();
          }

          return instance;
      }









}
