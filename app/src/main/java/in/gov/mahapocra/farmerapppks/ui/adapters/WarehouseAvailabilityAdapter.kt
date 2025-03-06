package in.gov.mahapocra.farmerapppks.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import in.gov.mahapocra.farmerapppks.data.model.WareHouseModel;

public class WarehouseAvailabilityAdapter extends RecyclerView.Adapter<WarehouseAvailabilityAdapter.ViewHolder> {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;
    private String warehouse_name;

    public WarehouseAvailabilityAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public WarehouseAvailabilityAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ware_house_availability, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {

        try {
                    viewHolder.onBind(mJSONArray.getJSONObject(position), listener);
                    viewHolder.tvContact_number.setOnClickListener(v -> {
                        String m= viewHolder.tvContact_number.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+m));
                        mContext.startActivity(intent);
                    });
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

        private TextView ware_house_name;
        private TextView record_date;
        private TextView tvAddress;
        private TextView tvContact_number;
        private TextView tvTotal_available_Capacity;

        public ViewHolder(@NonNull @NotNull View v) {
            super(v);
            ware_house_name = v.findViewById(R.id.ware_house_name);
            record_date = v.findViewById(R.id.record_date);
            tvAddress = v.findViewById(R.id.tvAddress);
            tvContact_number = v.findViewById(R.id.tvContact_number);
            tvTotal_available_Capacity = v.findViewById(R.id.tvTotal_available_Capacity);

        }
        public void onBind(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
                WareHouseModel wareHouseModel = new WareHouseModel(jsonObject);
                warehouse_name = wareHouseModel.getWarehouseName();
                String recorded_date = wareHouseModel.getRecordedDate();
                String village = wareHouseModel.getVillage();
                String phone = wareHouseModel.getPhone();
                String available_capacity =wareHouseModel.getAvailableCapacity();
                String upperString = warehouse_name.substring(0, 1).toUpperCase() + warehouse_name.substring(1).toLowerCase();
                ware_house_name.setText(upperString);
                record_date.setText(recorded_date);
                tvAddress.setText(village);
                tvContact_number.setText(phone);
                tvTotal_available_Capacity.setText(available_capacity+ " MT");
        }
    }
}
