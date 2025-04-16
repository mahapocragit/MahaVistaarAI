package in.gov.mahapocra.farmerapp.ui.adapters;

import android.content.Context;
import android.util.Log;
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
import in.gov.mahapocra.farmerapp.R;

public class FarmerStoryAdapter extends RecyclerView.Adapter<FarmerStoryAdapter.ViewHolder> {
    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;

    public FarmerStoryAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FarmerStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_dbt_activity_list, parent, false);
        return new FarmerStoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerStoryAdapter.ViewHolder holder, int position) {
        Log.d("mmhkjfhkdsajhf",mJSONArray.toString());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView =(TextView)  itemView.findViewById(R.id.nameTextView);
        }

        public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
            try {
                nameTextView.setText(jsonObject.getString("bandhar_description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMultiRecyclerViewItemClick(2, jsonObject);
                }
            });
        }
    }
}
