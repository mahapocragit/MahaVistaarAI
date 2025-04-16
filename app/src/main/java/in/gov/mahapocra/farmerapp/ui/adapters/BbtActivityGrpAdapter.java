package in.gov.mahapocra.farmerapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapp.R;

public class BbtActivityGrpAdapter extends RecyclerView.Adapter<BbtActivityGrpAdapter.ViewHolder> {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;
    private String calledActivituy;

    public BbtActivityGrpAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray, String calledActivituy) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
        this.calledActivituy = calledActivituy;
    }

    @NonNull
    @Override
    public BbtActivityGrpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_dbt_activity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.setDvata(position, mJSONArray.getJSONObject(position), listener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMultiRecyclerItemClickListener {

        private TextView nameTextView;
        private TextView data1_title;
        private TextView data1_value;
        private TextView data2_title;
        private TextView data2_value;
        private TextView data3_title;
        private TextView data3_value;
        private TextView data4_value;
        private LinearLayout main_linr_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            data1_title = itemView.findViewById(R.id.data1_title);
            data1_value = itemView.findViewById(R.id.data1_value);
            data2_title = itemView.findViewById(R.id.data2_title);
            data2_value = itemView.findViewById(R.id.data2_value);
            data3_title = itemView.findViewById(R.id.data3_title);
            data3_value = itemView.findViewById(R.id.data3_value);
            data4_value = itemView.findViewById(R.id.data4_value);
            main_linr_layout = itemView.findViewById(R.id.main_linr_layout);
        }

        public void setDvata(int position, JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
            try {
                if (calledActivituy.equalsIgnoreCase("dbtSchemes")) {
                    main_linr_layout.setVisibility(View.GONE);
                    nameTextView.setText(jsonObject.getString("ActivityGroupName"));
                } else if (calledActivituy.equalsIgnoreCase("OptonRclAdapter")) {
                    main_linr_layout.setVisibility(View.VISIBLE);
                    // nameTextView.setText(jsonObject.getString("sowing_date"));
                    JSONArray mJSONArray = jsonObject.getJSONArray("fertilizer");
                    // String total_Estimated_Cost =jsonObject.getString("total Estimated Cost");
                    String cropAgeDays = "";
                    String targetDate = "";
                    int price = 0;
                    for (int i = 0; i < mJSONArray.length(); i++) {
                        String tittle = mJSONArray.getJSONObject(i).getString("FertilizerName");
                        String quantity = mJSONArray.getJSONObject(i).getString("Quantity");
                        int priceValue = mJSONArray.getJSONObject(i).getInt("Price");
                        cropAgeDays = mJSONArray.getJSONObject(i).getString("CropAgeDays");
                        targetDate = mJSONArray.getJSONObject(i).getString("TargetDate");

                        price = price + priceValue;
                        if (i == 0) {
                            data1_title.setText(tittle);
                            data1_value.setText(quantity);
                        }
                        if (i == 1) {
                            data2_title.setText(tittle);
                            data2_value.setText(quantity);
                        }
                        if (i == 2) {
                            data3_title.setText(tittle);
                            data3_value.setText(quantity);
                        }
                    }
                    data4_value.setText("" + price);
                    if (cropAgeDays.equalsIgnoreCase("0")) {
                        nameTextView.setText(targetDate + " | " + mContext.getString(R.string.At_Sowing));
                    } else {
                        nameTextView.setText(targetDate + " | " + cropAgeDays + " " + mContext.getString(R.string.Days_After_Sowing));
                    }
                } else {
                    nameTextView.setText(jsonObject.getString("advisory"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(v -> {
                if (calledActivituy.equalsIgnoreCase("dbtSchemes")) {
                    listener.onMultiRecyclerViewItemClick(2, jsonObject);
                }
            });

        }

        @Override
        public void onMultiRecyclerViewItemClick(int i, Object obj) {

        }
    }
}
