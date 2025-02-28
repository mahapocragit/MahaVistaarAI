package in.gov.mahapocra.farmerapppks.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapppks.R;

public class WeatherAdapter  extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;
    private String activityname;

    public WeatherAdapter(Context context,OnMultiRecyclerItemClickListener listener, JSONArray jsonArray,String activityname) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
        this.activityname = activityname;
    }

    @NonNull
    @NotNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_dapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {

        try {

            if(!activityname.equalsIgnoreCase("forecastWeather") && !activityname.equalsIgnoreCase("previousWeather")) {
                viewHolder.dataBind(mJSONArray, listener);
            }else{
                viewHolder.onBind(mJSONArray.getJSONObject(position), listener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (mJSONArray != null) {
            return mJSONArray.length();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView datetitle;
        private TextView rainfall;
        private TextView tvMaxTemp;
        private TextView tv_min_temp;
        private TextView tv_humidity;
        private TextView tv_humidity2;
        private TextView tv_wind_speed;
        private TextView tv_wind_direct;
        private TextView tv_cloud_covr;

        public ViewHolder(@NonNull @NotNull View v) {
            super(v);
            datetitle = (TextView) v.findViewById(R.id.date_title);
            rainfall = (TextView) v.findViewById(R.id.tv_rainfall);
            tvMaxTemp = (TextView) v.findViewById(R.id.tv_max_temp);
            tv_min_temp = (TextView) v.findViewById(R.id.tv_min_temp);
            tv_humidity = (TextView) v.findViewById(R.id.tv_humidity);
            tv_humidity2 = (TextView) v.findViewById(R.id.tv_humidity2);
            tv_wind_speed = (TextView) v.findViewById(R.id.tv_wind_speed);
            tv_wind_direct = (TextView) v.findViewById(R.id.tv_wind_direct);
            tv_cloud_covr = (TextView) v.findViewById(R.id.tv_cloud_covr);
        }
        public void onBind(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) throws JSONException {
            Log.d("jsonObject13123131",jsonObject.toString());
            if(!activityname.equalsIgnoreCase("forecastWeather") && !activityname.equalsIgnoreCase("previousWeather")) {
                datetitle.setText(jsonObject.getString("name"));
                rainfall.setText(jsonObject.getString("value"));
            }
            else{
                tvMaxTemp.setVisibility(View.VISIBLE);
                tv_min_temp.setVisibility(View.VISIBLE);
                tv_humidity.setVisibility(View.VISIBLE);
                tv_humidity2.setVisibility(View.VISIBLE);
                tv_wind_speed.setVisibility(View.VISIBLE);
                datetitle.setText(jsonObject.getString("date"));
                rainfall.setText(jsonObject.getString("rain"));
                tvMaxTemp.setText(jsonObject.getString("temp_max"));
                tv_min_temp.setText(jsonObject.getString("temp_min"));
                tv_humidity.setText(jsonObject.getString("humidity_1"));
                tv_humidity2.setText(jsonObject.getString("humidity_2"));
                tv_wind_speed.setText(jsonObject.getString("wind_speed"));
                if(activityname.equalsIgnoreCase("forecastWeather")) {
                    tv_wind_direct.setVisibility(View.VISIBLE);
                    tv_cloud_covr.setVisibility(View.VISIBLE);
                    tv_wind_direct.setText(jsonObject.getString("wind_direction"));
                    tv_cloud_covr.setText(jsonObject.getString("cloud_cover"));

                }else if(activityname.equalsIgnoreCase("previousWeather")) {
                    tv_wind_direct.setVisibility(View.GONE);
                    tv_cloud_covr.setVisibility(View.GONE);
                    // tv_wind_direct.setText(jsonObject.getString("wind_direction"));
                    // tv_cloud_covr.setText(jsonObject.getString("cloud_cover"));
                }
            }

        }

        public void dataBind(JSONArray mJSONArray, OnMultiRecyclerItemClickListener listener) {

                int length = mJSONArray.length();
                    for (int i=0;  i<mJSONArray.length(); i++) {
                        try {
                            datetitle.setText(mJSONArray.getJSONObject(i).getString(("FertilizerName")));
                            rainfall.setText(mJSONArray.getJSONObject(i).getString(("Quantity")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
        }
    }
}
