package com.ai.ai_disc;

public class notify_singleton {


    private notify_singleton(){

    };

    private  String not;
    private  String msg;

    public String getnot() {
        return not;
    }

    public void setnot(String not) {
        this.not = not;
    }
    public String getmsg() {
        return msg;
    }

    public void setmsg(String msg) {
        this.msg = msg;
    }





    private static notify_singleton instance;

      public static notify_singleton getInstance(){


          if(instance==null){
              instance = new notify_singleton();
          }

          return instance;
      }









}
