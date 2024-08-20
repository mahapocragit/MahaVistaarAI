package com.ai.ai_disc.model;

import java.util.ArrayList;

public class myreport_model {




    private ArrayList<String> lats ;
    private ArrayList<String> longs ;
    private ArrayList<String> dpname ;
    private ArrayList<String> cropname ;
    private ArrayList<Integer> whichtype ;
    private ArrayList<Integer> reportid ;
    private ArrayList<String> dates ;
    private ArrayList<Integer> numbs ;
    private ArrayList<Integer> cropid ;
    private ArrayList<Integer> dpids ;



    public ArrayList<String> getcropnames() {
        return cropname;
    }

    public void setcropname(ArrayList<String> cropname) {
        this.cropname = cropname;
    }

    public ArrayList<Integer> getcropid() {
        return cropid;
    }

    public void setcropid(ArrayList<Integer> cropid) {
        this.cropid = cropid;
    }


    public ArrayList<Integer> getdpids() {
        return dpids;
    }

    public void setdpids(ArrayList<Integer> dpids) {
        this.dpids = dpids;
    }


    public ArrayList<Integer> getreportid() {
        return reportid;
    }

    public void setreportid(ArrayList<Integer> reportid) {
        this.reportid = reportid;
    }


    public ArrayList<Integer> getwhichtype() {
        return whichtype;
    }

    public void setwhichtype(ArrayList<Integer> whichtype) {
        this.whichtype = whichtype;
    }


    public ArrayList<Integer> getnumbs() {
        return numbs;
    }

    public void setnumbs(ArrayList<Integer> numbs) {
        this.numbs = numbs;
    }


    public ArrayList<String> getdpname() {
        return dpname;
    }

    public void setdpname(ArrayList<String> dpname) {
        this.dpname = dpname;
    }


    public ArrayList<String> getlongs() {
        return longs;
    }

    public void setlongss(ArrayList<String> longs) {
        this.longs = longs;
    }


    public ArrayList<String> getlats() {
        return lats;
    }

    public void setlats(ArrayList<String> lats) {
        this.lats = lats;
    }

    public ArrayList<String> getdates() {
        return dates;
    }

    public void setdates(ArrayList<String> dates) {
        this.dates = dates;
    }
}

