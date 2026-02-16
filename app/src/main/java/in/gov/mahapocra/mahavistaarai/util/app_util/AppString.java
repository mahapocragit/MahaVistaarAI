package in.gov.mahapocra.mahavistaarai.util.app_util;

import android.content.Context;

import in.gov.mahapocra.mahavistaarai.R;

public class AppString {

    private Context mContext = null;

    private String kMSG_WAIT;
    private String kNETWORK;
    private String kUNAUTHORISED;


    public AppString(Context mContext) {
        this.mContext = mContext;

    }

    public String getAppName() {
        return mContext.getResources().getString(R.string.app_name);
    }


    public String getkMSG_WAIT() {
        kMSG_WAIT = mContext.getResources().getString(R.string.please_wait);
        return kMSG_WAIT;
    }
    public String getYES() {
        return mContext.getResources().getString(R.string.yes);
    }


    public String getNO() {
        return mContext.getResources().getString(R.string.no);
    }
}
