package com.ai.ai_disc.model;

public class getall_report_model {

    public String user_id ;

      public String record_id ;
    public String crop_id ;
        public String crop_name ;
    public String report_type ;

    public String dpname ;
        public String disease_id ;
        public String nut_id ;
        public String insect_id ;

        public String dated ;

        public String info ;
        public String head ;

        public String lat ;
        public String lon ;
    public String locate ;
    public String gethead() {
        return head;
    }

    public void sethead(String head) {
        this.head = head;
    }
    public String getcrop_id() {
        return crop_id;
    }

    public void setcrop_id(String crop_id) {
        this.crop_id = crop_id;
    }
    public String getlocate() {
        return locate;
    }

    public void setlocate(String locate) {
        this.locate = locate;
    }

    public String getreport_type() {
        return report_type;
    }

    public void setreport_type(String report_type) {
        this.report_type = report_type;
    }

    public String getuser_id() {
        return user_id;
    }

    public void setuser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getrecord_id() {
        return record_id;
    }

    public void setrecord_id(String record_id) {
        this.record_id = record_id;
    }


    public String getdisease_id() {
        return disease_id;
    }

    public void setdisease_id(String disease_name) {
        this.disease_id = disease_name;
    }

    //////
    public String getinsect_id() {
        return insect_id;
    }

    public void setinsect_id(String insect_name) {
        this.insect_id = insect_name;
    }

    //////


    public String getdpname() {
        return dpname;
    }

    public void setdpname(String dp) {
        this.dpname = dp;
    }

    public String getlat() {
        return lat;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    //////


    public String getlon() {
        return lon;
    }

    public void setlon(String lon) {
        this.lon = lon;
    }

    public String getnut_id() {
        return nut_id;
    }

    public void setnut_id(String nut) {
        this.nut_id = nut;
    }


    //////
    public String getcrop_name() {
        return crop_name;
    }

    public void setcrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getinfo() {
        return info;
    }

    public void setinfo(String info) {
        this.info = info;
    }

    public String getdated() {
        return dated;
    }

    public void setdated(String dated) {
        this.dated = dated;
    }


}

