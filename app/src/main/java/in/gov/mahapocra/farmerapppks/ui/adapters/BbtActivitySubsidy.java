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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapppks.R;

public class BbtActivitySubsidy extends RecyclerView.Adapter<BbtActivitySubsidy.ViewHolder>  {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;

    public BbtActivitySubsidy(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }
    @NonNull
    @NotNull
    @Override
    public BbtActivitySubsidy.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dbt_activity_subsidy_deails, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BbtActivitySubsidy.ViewHolder holder, int position) {

        try {
            holder.setDvata(mJSONArray.getJSONObject(position), listener);
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

        private TextView subsidyYearValue;
        private TextView maxSubsidyPercentageValue;
        private TextView maxSubsidyAmountValue;
        private TextView noteValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subsidyYearValue =(TextView)  itemView.findViewById(R.id.subsidyYearValue);
            maxSubsidyPercentageValue =(TextView)  itemView.findViewById(R.id.maxSubsidyPercentageValue);
            maxSubsidyAmountValue =(TextView)  itemView.findViewById(R.id.maxSubsidyAmountValue);
            noteValue =(TextView)  itemView.findViewById(R.id.noteValue);
        }

        public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
            try {
                subsidyYearValue.setText(jsonObject.getString("Term"));
                maxSubsidyPercentageValue.setText(jsonObject.getString("MaxSubsidyPercentage")+"%");
                maxSubsidyAmountValue.setText(jsonObject.getString("MaxSubsidyAmount")+"/-");
                noteValue.setText(jsonObject.getString("Note"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onMultiRecyclerViewItemClick(1, jsonObject);
//                }
//            });

        }
    }
}

