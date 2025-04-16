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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.mahavistaarai.R;

public class BbtActivityGrpDetailsAdapter extends RecyclerView.Adapter<BbtActivityGrpDetailsAdapter.ViewHolder>  {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;

    public BbtActivityGrpDetailsAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }
    @NonNull
    @NotNull
    @Override
    public BbtActivityGrpDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dbt_activity_list_deails, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BbtActivityGrpDetailsAdapter.ViewHolder holder, int position) {

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


        private TextView nameTextView;
        private TextView subsideyTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView =(TextView)  itemView.findViewById(R.id.nameTextView);
            subsideyTextView =(TextView)  itemView.findViewById(R.id.subsideyTextView);
        }

        public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
            try {
                nameTextView.setText(jsonObject.getString("ActivityName"));
                subsideyTextView.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMultiRecyclerViewItemClick(1, jsonObject);
                }
            });

        }
    }
}
