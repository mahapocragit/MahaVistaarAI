package in.gov.mahapocra.farmerapp.util.app_util;

import android.content.Context;

import in.co.appinventor.services_api.settings.AppSettings;

public class AppSession {
    private Context mContext;
    public AppSession(Context mContext) {
        this.mContext = mContext;
    }

    public String getToken() {
        return AppSettings.getInstance().getSavedValue(mContext, AppConstants.kTOKEN);
    }





//            Name
//            MobileNo
//            EmailId
//            FAAPRegistrationID
//            DistrictName
//            DistrictID
//            TalukaName
//            TalukaID
}
