package in.gov.mahapocra.farmerapppks.ui.adapters;

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
import in.gov.mahapocra.farmerapppks.R;

public class CropDetailsAdpter extends RecyclerView.Adapter<CropDetailsAdpter.ViewHolder> {

        private OnMultiRecyclerItemClickListener listener;
        private Context mContext;
        private JSONArray mJSONArray;
        private Boolean cropsap;

        public CropDetailsAdpter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray,Boolean cropsap) {
            this.mContext = context;
            this.mJSONArray = jsonArray;
            this.listener = listener;
            this.cropsap = cropsap;
        }

        @NonNull
        @Override
        public CropDetailsAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.single_cropadvisory_details, parent, false);
            return new CropDetailsAdpter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CropDetailsAdpter.ViewHolder holder, int position) {
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

            private TextView nameTextView2;
            private TextView nameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView =(TextView)  itemView.findViewById(R.id.nameTextView);
            }

            public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
                try {
                    //nameTextView.setText(jsonObject.getString("MainGroupSRNo")+" - " + jsonObject.getString("MainGroupName"));
                    String strCrop1= jsonObject.getString("advisory");
                    String strCrop2= jsonObject.getString("cropsap_advisory");
                    String strCropDate= jsonObject.getString("generated_at");
                    String strSubCropDate= jsonObject.getString("cropsap_advisory_date");

                    String currentString = strSubCropDate;
                    String[] separated = currentString.split(" ");
                    String str1=separated[0];
                    if(cropsap.equals(true))
                    {
                        nameTextView.setText( strCrop2 + "-" + str1);
                    }else {
                        nameTextView.setText(strCrop1 + "-" + strCropDate + "\n" + strCrop2 + "-" + str1);
                    }
                   // nameTextView.setText( jsonObject.getString("advisory"));
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
