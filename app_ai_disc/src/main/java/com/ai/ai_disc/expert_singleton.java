package com.ai.ai_disc;

public class expert_singleton {


    private expert_singleton(){

    };

    private  String crop;

    private  String user_id;
    private  String fullname;
    private  String designation;

    public String getcrop() {
        return crop;
    }
    public void setcrop(String crop) {
        this.crop = crop;
    }




    public void setuser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getuser_id() {
        return user_id;
    }

/*
        String name=response.getString("name");
                            String user_id=response.getString("user_id");
                            String email_id=response.getString("email_id");
                            String mobile_number=response.getString("mobile_number");
                            String user_type=response.getString("user_type");
       */



    public String getdesignation() {
        return designation;
    }
    public void setdesignation(String designation) {
        this.designation = designation;
    }


    public String getfullname() {
        return fullname;
    }
    public void setfullname(String fullname) {
        this.fullname = fullname;
    }

    private static expert_singleton instance;

      public static expert_singleton getInstance(){


          if(instance==null){
              instance = new expert_singleton();
          }

          return instance;
      }









}
