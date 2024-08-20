package com.ai.ai_disc.model;

import java.util.ArrayList;

public class reportdata {


    public String user_name ;
    public boolean lk ;
    public boolean fl ;
    public String record_id ;
    public String user_id ;
    public String crop_name ;
    public String report_type ;

    public String dpname ;
    public String prio ;

    public String getprio() {
        return prio;
    }

    public void setprio(String prio) {
        this.prio = prio;
    }
    public String likednumber ;

    public String getlikednumber() {
        return likednumber;
    }

    public void setlikednumber(String likednumber) {
        this.likednumber = likednumber;
    }
    public String info ;

    public String getinfo() {
        return info;
    }

    public void setinfo(String info) {
        this.info = info;
    }

    public String dated ;

    public String head ;

    public ArrayList<String> images ;

    public String locate ;
    public String getuser_id() {
        return user_id;
    }

    public void setuser_id(String user_id) {
        this.user_id = user_id;
    }
    public String gethead() {
        return head;
    }

    public void sethead(String head) {
        this.head = head;
    }
    public boolean getlk() {
        return lk;
    }

    public void setlk(boolean lk) {
        this.lk = lk;
    }
    public boolean getfl() {
        return fl;
    }

    public void setfl(boolean fl) {
        this.fl = fl;
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

    public String getuser_name() {
        return user_name;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getrecord_id() {
        return record_id;
    }

    public void setrecord_id(String record_id) {
        this.record_id = record_id;
    }


    //////


    public String getdpname() {
        return dpname;
    }

    public void setdpname(String dp) {
        this.dpname = dp;
    }

    public ArrayList<String> getimages() {
        return images;
    }

    public void setimages(ArrayList<String> images) {
        this.images = images;
    }

    //////
    public String getcrop_name() {
        return crop_name;
    }

    public void setcrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getdated() {
        return dated;
    }

    public void setdated(String dated) {
        this.dated = dated;
    }
}
