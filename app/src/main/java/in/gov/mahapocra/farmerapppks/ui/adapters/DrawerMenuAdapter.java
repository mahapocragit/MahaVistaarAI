package in.gov.mahapocra.farmerapppks.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.mahapocra.farmerapppks.R;

public class DrawerMenuAdapter extends BaseAdapter  {

    private final Context mContext;
    private final JSONArray mDataArray;
    private final Integer farmerId;
    public DrawerMenuAdapter(Context mContext, JSONArray mDataArray,Integer farmerId) {
        Log.d("farmerId1212121221", farmerId.toString());
        this.mContext = mContext;
        this.mDataArray = mDataArray;
        this.farmerId = farmerId;
    }

    @Override
    public int getCount() {
        return mDataArray.length();
    }


    @Override
    public Object getItem(int position) {
        try {
            return mDataArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
          //  FirebaseCrashlytics.getInstance().recordException(e);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (mLayoutInflater != null) {
                convertView = mLayoutInflater.inflate(R.layout.list_menu_drawer, null);

                viewHolder.iconImageView =  convertView.findViewById(R.id.iconImageView);
                viewHolder.nameTextView =  convertView.findViewById(R.id.nameTextView);
                convertView.setTag(viewHolder);
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            JSONObject jsonObject = mDataArray.getJSONObject(position);

          // int farmerId = AppSettings.getInstance().getIntValue(mContext, AppConstants.fREGISTER_ID, 0);
           String logoutString = jsonObject.getString("name");
           String logInStatus = jsonObject.getString("name");
           String myProfile = jsonObject.getString("name");

            Log.d("farmerId::logoutString",farmerId+""+logoutString);



            viewHolder.nameTextView.setText(jsonObject.getString("name"));
            if (jsonObject.getString("icon") == null) {
                viewHolder.iconImageView.setImageDrawable(null);
            } else {
                int resId = mContext.getResources().getIdentifier(jsonObject.getString("icon"), "mipmap", mContext.getPackageName());
                viewHolder.iconImageView.setImageResource(resId);

                if (jsonObject.getString("icon").equalsIgnoreCase("ic_list_black")) {
                    viewHolder.iconImageView.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
                } else {
                    viewHolder.iconImageView.setColorFilter(null);
                }

            }
            if(farmerId ==0 && (logoutString.equalsIgnoreCase("Logout")|| logoutString.equalsIgnoreCase("बाहेर पडणे"))){
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.iconImageView.setVisibility(View.GONE);
            }
            if(farmerId ==0 && (myProfile.equalsIgnoreCase("My Profile")|| myProfile.equalsIgnoreCase("माझे प्रोफाईल"))){
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.iconImageView.setVisibility(View.GONE);
            }
            if(farmerId == 0 && (myProfile.equalsIgnoreCase("My DBT Application Status")|| myProfile.equalsIgnoreCase("डीबीटी अर्ज स्थिती"))){
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.iconImageView.setVisibility(View.GONE);
            }
            if(farmerId == 0 && (myProfile.equalsIgnoreCase("GIS")|| myProfile.equalsIgnoreCase("जीआयएस- भौगोलिक माहिती प्रणाली"))){
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.iconImageView.setVisibility(View.GONE);
            }
            if(farmerId > 0 && (logInStatus.equalsIgnoreCase("Login/Registration") ||logInStatus.equalsIgnoreCase("लॉगइन/नोंदणी"))){
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.iconImageView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
          //  FirebaseCrashlytics.getInstance().recordException(e);
        }
        return convertView;
    }


    private static class ViewHolder {
        private TextView nameTextView;
        private ImageView iconImageView;
    }
}
