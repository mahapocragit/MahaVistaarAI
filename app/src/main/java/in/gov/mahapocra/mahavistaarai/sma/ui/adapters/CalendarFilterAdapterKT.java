package in.gov.mahapocra.mahavistaarai.sma.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.data.helper.AppHelper;
import in.gov.mahapocra.mahavistaarai.sma.data.models.CalendarFilterModel;

public class CalendarFilterAdapterKT extends RecyclerView.Adapter<CalendarFilterAdapterKT.ViewHolder> {


    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mDataArray;
    public int mSelectedIndex = -1;


    public CalendarFilterAdapterKT(Context mContext, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
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
        View base = LayoutInflater.from(mContext).inflate(R.layout.recycler_common, parent, false);
        return new ViewHolder(base);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(mDataArray.getJSONObject(position), position, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageView statusImageView;


        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusImageView = itemView.findViewById(R.id.statusImageView);

        }

        private void onBind(final JSONObject jsonObject, final int position, final OnMultiRecyclerItemClickListener listener) {

            final CalendarFilterModel model = new CalendarFilterModel(jsonObject);

            String outputMonthEn=model.getName();
            String outputMonthMr= AppHelper.getInstance().convertToMarathiMonth(outputMonthEn);
            nameTextView.setText(outputMonthMr);

            if (model.getIs_selected() == 1) {
                statusImageView.setVisibility(View.VISIBLE);
                statusImageView.setPadding(0,0,10,0);
                statusImageView.setImageResource(R.drawable.ic_done_green);
            } else {
                statusImageView.setVisibility(View.GONE);

            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateSelection(model, position);
                    listener.onMultiRecyclerViewItemClick(2, jsonObject);
                }
            });

        }



        private void updateSelection(CalendarFilterModel model, int position) {
            try {
                mSelectedIndex = position;

                for (int k = 0; k < mDataArray.length(); k++) {
                    JSONObject jsonObject = mDataArray.getJSONObject(k);
                    if(model.getIs_selected() == 0 && k == position ) {
                        jsonObject.put("is_selected", 1);
                    } else {

                        jsonObject.put("is_selected", 0);
                    }
                    mDataArray.put(k, jsonObject);
                }

                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
