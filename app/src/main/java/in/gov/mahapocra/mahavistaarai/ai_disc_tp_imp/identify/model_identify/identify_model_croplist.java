package in.gov.mahapocra.mahavistaarai.ai_disc_tp_imp.identify.model_identify;

import java.util.ArrayList;

public class identify_model_croplist {

    private ArrayList<String> cropname_dis;
    private ArrayList<String> cropname_pest;
    public ArrayList<String> getcropsd() {
        return cropname_dis;
    }

    public void setcropsd(ArrayList<String>cropname_dis) {
        this.cropname_dis = cropname_dis;
    }

    public ArrayList<String> getcropsp() {
        return cropname_pest;
    }

    public void setcropsp(ArrayList<String>cropname_pest) {
        this.cropname_pest = cropname_pest;
    }
}


