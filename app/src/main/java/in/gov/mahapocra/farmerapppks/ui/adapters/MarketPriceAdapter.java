package in.gov.mahapocra.farmerapppks.ui.adapters;

import android.content.Context;
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
        private TextView unitForQuantity;

        public ViewHolder(@NonNull @NotNull View v) {
            super(v);
            tv_crop_name = v.findViewById(R.id.tv_crop_name);
            tv_crop_date = v.findViewById(R.id.tv_crop_date);
            tvMaxValue = v.findViewById(R.id.tvMaxValue);
            tvAvgValue = v.findViewById(R.id.tvAvgValue);
            tvMinValue = v.findViewById(R.id.tvMinValue);
            unitForQuantity = v.findViewById(R.id.unitForQuantity);
        }

        public void onBind(JSONObject jsonObject) throws JSONException {
            String commCropName = jsonObject.getString("comm_name");
            String variableCropName = jsonObject.getString("variety_name");
            tv_crop_name.setText(commCropName+" ("+variableCropName+")");
            tv_crop_date.setText(jsonObject.getString("date"));
            tvMaxValue.setText(jsonObject.getString("max_price"));
            tvAvgValue.setText(jsonObject.getString("avg_price"));
            tvMinValue.setText(jsonObject.getString("min_price"));
            unitForQuantity.setText(String.format(" %s", jsonObject.getString("unit")));
        }
    }
}
