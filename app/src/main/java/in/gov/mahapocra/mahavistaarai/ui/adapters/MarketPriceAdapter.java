package in.gov.mahapocra.mahavistaarai.ui.adapters;

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

import in.gov.mahapocra.mahavistaarai.R;

public class MarketPriceAdapter  extends RecyclerView.Adapter<MarketPriceAdapter.ViewHolder> {

    private Context mContext;
    private JSONArray mOriginalArray;
    private JSONArray mFilteredArray;

    public MarketPriceAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mOriginalArray = jsonArray;
        this.mFilteredArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mFilteredArray.put(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public MarketPriceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_market_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {
            viewHolder.onBind(mFilteredArray.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredArray.length();
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

    public void filter(String query) {
        mFilteredArray = new JSONArray();
        if (query == null || query.trim().isEmpty()) {
            mFilteredArray = mOriginalArray;
        } else {
            query = query.toLowerCase();
            for (int i = 0; i < mOriginalArray.length(); i++) {
                try {
                    JSONObject obj = mOriginalArray.getJSONObject(i);
                    if (obj.getString("comm_name").toLowerCase().contains(query)) {
                        mFilteredArray.put(obj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }
}
