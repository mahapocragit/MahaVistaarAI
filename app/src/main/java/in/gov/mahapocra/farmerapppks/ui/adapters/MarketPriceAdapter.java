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

import in.gov.mahapocra.farmerapppks.R;

public class MarketPriceAdapter  extends RecyclerView.Adapter<MarketPriceAdapter.ViewHolder> {

    private Context mContext;
    private JSONArray mJSONArray;

    public MarketPriceAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @NonNull
    @NotNull
    @Override
    public MarketPriceAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_market_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(position));
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

        private TextView tv_crop_name;
        private TextView tv_crop_date;
        private TextView tvMaxValue;
        private TextView tvAvgValue;
        private TextView tvMinValue;
//        private TextView tv_humidity2;
//        private TextView tv_wind_speed;
//        private TextView tv_wind_direct;
//        private TextView tv_cloud_covr;

        public ViewHolder(@NonNull @NotNull View v) {
            super(v);
            tv_crop_name = (TextView) v.findViewById(R.id.tv_crop_name);
            tv_crop_date = (TextView) v.findViewById(R.id.tv_crop_date);
            tvMaxValue = (TextView) v.findViewById(R.id.tvMaxValue);
            tvAvgValue = (TextView) v.findViewById(R.id.tvAvgValue);
            tvMinValue = (TextView) v.findViewById(R.id.tvMinValue);
//            tv_humidity2 = (TextView) v.findViewById(R.id.tv_humidity2);
//            tv_wind_speed = (TextView) v.findViewById(R.id.tv_wind_speed);
//            tv_wind_direct = (TextView) v.findViewById(R.id.tv_wind_direct);
//            tv_cloud_covr = (TextView) v.findViewById(R.id.tv_cloud_covr);
        }

        public void onBind(JSONObject jsonObject) throws JSONException {

            Log.d("jsonObject13123131",jsonObject.toString());
            String commCropName = jsonObject.getString("comm_name");
            String variableCropName = jsonObject.getString("variety_name");
            tv_crop_name.setText(commCropName+" ("+variableCropName+")");
            tv_crop_date.setText(jsonObject.getString("date"));
            tvMaxValue.setText(jsonObject.getString("max_price")+" / "+jsonObject.getString("unit"));
            tvAvgValue.setText(jsonObject.getString("avg_price")+" / "+jsonObject.getString("unit"));
            tvMinValue.setText(jsonObject.getString("min_price")+" / "+jsonObject.getString("unit"));
//            tv_humidity2.setText(jsonObject.getString("humidity_2"));
//            tv_wind_speed.setText(jsonObject.getString("wind_speed"));

        }
    }
}
