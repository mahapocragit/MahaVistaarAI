package in.gov.mahapocra.mahavistaarai.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.mahavistaarai.R;

public class SOPAdapter extends RecyclerView.Adapter<SOPAdapter.ViewHolder> {

    private final OnMultiRecyclerItemClickListener listener;
    private final Context mContext;
    private final JSONArray mJSONArray;

    public SOPAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SOPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_climate_resilient_tech, parent, false);
        return new SOPAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SOPAdapter.ViewHolder holder, int position) {
        try {
            holder.setData(mJSONArray.getJSONObject(position), listener, position);
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
        private final TextView nameTextView2;
        private final TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView2 = itemView.findViewById(R.id.nameTextView2);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
        public void setData(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener, int position) {
            try {
                nameTextView2.setText(position+". ");
                nameTextView.setText( jsonObject.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(v -> listener.onMultiRecyclerViewItemClick(2, jsonObject));
        }
    }
}
