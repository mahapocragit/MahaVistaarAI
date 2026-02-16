package in.gov.mahapocra.mahavistaarai.sma.ui.adapters;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.data.models.KTReportDetailsModel;

public class KTReportDetailsAdapter extends RecyclerView.Adapter<KTReportDetailsAdapter.ViewHolder> {

        ArrayList<KTReportDetailsModel> mValues;
        Context mContext;
        String mflag;
        OnItemClickListener listener;
    // Constructor with click listener
        public KTReportDetailsAdapter(Context context, ArrayList<KTReportDetailsModel> values, String flag, OnItemClickListener listener) {
        mValues = values;
        mContext = context;
        mflag = flag;
        this.listener = listener;
       }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_kt_single_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder Vholder, final int position) {
            final KTReportDetailsModel model = mValues.get(position);
            String strCategoryName= String.valueOf(model.getCategoryName());
            String strCategoryType= String.valueOf(model.getCategoryType());
            String strCategoryTypeMr= String.valueOf(model.getCategoryTypeMr());
            String strDate =String.valueOf(model.getDate());
            Log.d("MAYU222","onBindViewHolder strDate==="+strDate);
            Log.d("MAYU222","onBindViewHolder CategoryName==="+strCategoryName);
            Log.d("MAYU222","onBindViewHolder strCategoryType==="+strCategoryTypeMr);
            Vholder.tvCategoryType.setText("कामाचे स्वरूप : "+strCategoryTypeMr);
            Vholder.tvCategoryName.setText("कामाचा प्रकार : "+strCategoryName);
            Vholder.tvDate.setText("दिनांक : "+strDate);
            Vholder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(model);
                    }
                }
            });
            Vholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(model);
                    }
                }
            });
//            Log.d("MAYU222","onBindViewHolder Cost==="+model.getAbsentReasonName());
//            String strStorageCapacity = model.getStorage_capacity_unit();
//            Vholder.tvSrNo.setText(String.valueOf(position + 1));
//            if (strStorageCapacity.equals("हेक्टर")) {
//                Vholder.actId.setText(model.getTotal_cost());
//                Vholder.activityName.setText(model.getStructure_name_marathi());
//                Vholder.survayid.setText(model.getTotal_area());
//            }else if (strStorageCapacity.equals("संख्या")) {
//                Vholder.actId.setText(model.getTotal_cost());
//                Vholder.activityName.setText(model.getStructure_name_marathi());
//                Vholder.survayid.setText(model.getTotal_structures());
//            }
//            Log.d("MAYU222","onBindViewHolder Cost==="+model.getTotal_cost());

        }

        @Override
        public int getItemCount() {

            return mValues.size();
        }

        public interface RecyclerViewClickListener {

            void onClick(View view, int position);

        }
        public void removeItem(int position) {
            mValues.remove(position);
            notifyItemRemoved(position);
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvDate,tvCategoryType,tvCategoryName;
            public Button btnViewDetails;

            public ViewHolder(View v) {

                super(v);
//                activityName = (TextView) v.findViewById(R.id.work_name);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                tvCategoryType = (TextView) v.findViewById(R.id.tvCategoryType);
                tvCategoryName = (TextView) v.findViewById(R.id.tvCategoryName);
                btnViewDetails = (Button) v.findViewById(R.id.btnViewDetails);

            }
        }
    // 🔹 Define click listener interface
    public interface OnItemClickListener {
        void onItemClick(KTReportDetailsModel model);
    }
    }

