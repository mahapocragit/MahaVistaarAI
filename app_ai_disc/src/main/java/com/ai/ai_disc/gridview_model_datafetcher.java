package com.ai.ai_disc;

public class gridview_model_datafetcher {


        // string course_name for storing course_name
        // and imgid for storing image id.
        private int reportid;
    private String cropname;
    private String dpname;
        private int numbs;
        private int whichtype;
        private String dates;
    private String lats;
    private String longs;
    private int imgid;
    private int dpid;
    private int cropid;



        public gridview_model_datafetcher(int imgid,String cropname, int reportid, int numbs, String dates,
                      String lats , String longs, int whichtype,  String dpname,int dpid, int cropid) {
            this.cropname = cropname;
            this.reportid = reportid;
            this.whichtype = whichtype;
            this.numbs = numbs;
            this.dates=dates;
            this.dpname=dpname;
            this.lats=lats;
            this.longs=longs;
            this.imgid=imgid;
            this.dpid=dpid;
            this.cropid=cropid;

        }

        public String getcropnames() {
            return cropname;
        }

        public void setcropname(String cropname) {
            this.cropname = cropname;
        }


    public int getdpid() {
        return dpid;
    }

    public void setdpid(int dpid) {
        this.dpid = dpid;
    }

    public int getcropid() {
        return cropid;
    }

    public void setcropid(int cropid) {
        this.cropid = cropid;
    }



    public int getimgid() {
        return imgid;
    }

    public void setimgid(int imgid) {
        this.imgid = imgid;
    }



    public int getreportid() {
        return reportid;
    }

    public void setreportid(int reportid) {
        this.reportid = reportid;
    }


    public int getwhichtype() {
        return whichtype;
    }

    public void setwhichtype(int whichtype) {
        this.whichtype = whichtype;
    }


        public int getnumbs() {
            return numbs;
        }

        public void setnumbs(int numbs) {
            this.numbs = numbs;
        }


    public String getdpname() {
        return dpname;
    }

    public void setdpname(String dpname) {
        this.dpname = dpname;
    }


    public String getlongs() {
        return longs;
    }

    public void setlongss(String longs) {
        this.longs = longs;
    }


    public String getlats() {
        return lats;
    }

    public void setlats(String lats) {
        this.lats = lats;
    }

    public String getdates() {
        return dates;
    }

    public void setdates(String dates) {
        this.dates = dates;
    }

    }


