package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify;

public class user {

    private user(){

    };
    private  String lat;private  String lon;
    private  String loc;
    private  String user_name;
    private  String user_id;
    private  String name;
    private String email_id;
    private  String mobile_number;
    private  String user_type;
    private String password;
    private String fb_id;
    private String fb_token;
    private String fname,lname,mname;
    public String getfb_id() {
        return fb_id;
    }

    public void setfb_id(String fb_id) {
        this.fb_id = fb_id;
    }
    public String getfb_token() {
        return fb_token;
    }

    public void setfb_token(String fb_token) {
        this.fb_token = fb_token;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    public String getlat() {
        return lat;
    }

    public void setlon(String lon) {
        this.lon = lon;
    }

    public String getlon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }


    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }
    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public String getfname() {
        return fname;
    }

    public void setfname(String fname) {
        this.fname = fname;
    }

    public String getlname() {
        return lname;
    }

    public void setlname(String lname) {
        this.lname = lname;
    }

    public String getloct() {
        return loc;
    }

    public void setloct(String loc) {
        this.loc = loc;
    }


    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private static user instance;

    public static user getInstance(){

        if(instance==null){
            instance = new user();
        }

        return instance;
    }

}
