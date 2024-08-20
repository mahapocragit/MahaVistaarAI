package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify;

public class detection_instance {


    private detection_instance(){

    };

    private  String crop;
    private  String type;
    private  String url;
    private  String detection;
    private  String path;
    private  int numb;

    public String getcrop() {
        return crop;
    }

    public String getpath() {
        return path;
    }

    public void setpath(String path) {
        this.path = path;
    }
    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public void setcrop(String crop) {
        this.crop = crop;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String getdetection() {
        return detection;
    }

    public void setdetection(String detection) {
        this.detection = detection;
    }
    public int getnumb() {
        return numb;
    }

    public void setnumb(int numb) {
        this.numb = numb;
    }


    private static detection_instance instance;

      public static detection_instance getInstance(){


          if(instance==null){
              instance = new detection_instance();
          }

          return instance;
      }









}
