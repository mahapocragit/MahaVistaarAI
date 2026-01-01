package in.gov.mahapocra.mahavistaarai.sma;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import in.gov.mahapocra.mahavistaarai.R;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;

public class KTDashboardAdapter extends RecyclerView.Adapter<KTDashboardAdapter.ViewHolder> {


    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mDataArray;


    public KTDashboardAdapter(Context mContext, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = mContext;
        this.listener = listener;
        this.mDataArray = jsonArray;
    }


    @Override
    public int getItemCount() {
        if (mDataArray != null) {
            return mDataArray.length();
        } else {
            return 0;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View base = LayoutInflater.from(mContext).inflate(R.layout.recycler_dashboard, parent, false);
        return new ViewHolder(base);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(mDataArray.getJSONObject(position), listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageView iconImageView;


        public ViewHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.iconImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

        private void onBind(final JSONObject jsonObject, final OnMultiRecyclerItemClickListener listener) {

            try {
                nameTextView.setText(jsonObject.getString("name"));

                int resId = mContext.getResources().getIdentifier(jsonObject.getString("icon"), "mipmap", mContext.getPackageName());
                Log.d("tdsjhgfhdsj0", String.valueOf(resId));
                iconImageView.setImageResource(resId);

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
