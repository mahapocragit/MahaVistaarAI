package in.gov.mahapocra.farmerapppks.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.models.response.WareHouseModel;

public class WarehouseAvailabilityAdapterreport  extends RecyclerView.Adapter<WarehouseAvailabilityAdapterreport.ViewHolder> {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;
    private String warehouse_name;
    int N = 3;
    final TextView[] myTextViews = new TextView[N];

    public String[] mobileArray;

    public WarehouseAvailabilityAdapterreport(Context context,OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public WarehouseAvailabilityAdapterreport.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ware_house_availability, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {

        try {
                    viewHolder.onBind(mJSONArray.getJSONObject(position), listener);
                    viewHolder.tvContact_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String m= viewHolder.tvContact_number.getText().toString();
                   // Toast.makeText(mContext, m+position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+m));
                    mContext.startActivity(intent);
                }
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
        private TextView tvTotal_Capacity;
        private TextView tvTotal_available_Capacity;
        private TextView tvWare_House_Code;
        private RelativeLayout relme;

        public ViewHolder(@NonNull @NotNull View v) {
            super(v);
            ware_house_name = (TextView) v.findViewById(R.id.ware_house_name);
            record_date = (TextView) v.findViewById(R.id.record_date);
            tvAddress = (TextView) v.findViewById(R.id.tvAddress);
            tvContact_number = (TextView) v.findViewById(R.id.tvContact_number);
            tvTotal_Capacity = (TextView) v.findViewById(R.id.tvTotal_Capacity);
            tvTotal_available_Capacity = (TextView) v.findViewById(R.id.tvTotal_available_Capacity);
            tvWare_House_Code = (TextView) v.findViewById(R.id.tvWare_House_Code);
            relme = (RelativeLayout) v.findViewById(R.id.relm);

        }
        public void onBind(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
                    Log.d("jsonObject13123131",jsonObject.toString());

                WareHouseModel wareHouseModel = new WareHouseModel(jsonObject);

                warehouse_name = wareHouseModel.getWarehouseName();
                String recorded_date = wareHouseModel.getRecordedDate();
                String village = wareHouseModel.getVillage();
                String phone = wareHouseModel.getPhone();

//
//                   String[] res = phone.split("[,]", 0);
//                   String[] res1 = phone.split("[,]", 1);
////                   for(String myStr: res) {
////                       System.out.println(myStr);
//                       Log.d("Phone=",""+res1);
//                       for (int i = 0; i < N; i++) {
//                           // create a new textview
//                           myTextViews[i].setText(""+res1[i] );
//                           relme.addView(myTextViews[i]);
//                           //Log.d("N=",""+myStr);
//                           // add the textview to the linearlayout
//                           // save a reference to the textview for later
//
////                       }
//
//                  }


                String total_capacity =wareHouseModel.getTotalCapacity();
                String available_capacity =wareHouseModel.getAvailableCapacity();
                String warehouse_code = wareHouseModel.getWarehouseCode();
                String upperString = warehouse_name.substring(0, 1).toUpperCase() + warehouse_name.substring(1).toLowerCase();
                ware_house_name.setText(upperString);
                record_date.setText(recorded_date);
                tvAddress.setText(village);
                tvContact_number.setText(phone);
                tvTotal_Capacity.setText(total_capacity);
                tvTotal_available_Capacity.setText(available_capacity+ " MT");
                tvWare_House_Code.setText(warehouse_code);
        }
    }
    public static String capitalizeString(String str) {
        String retStr = str;
        try { // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
            Log.d("WarehouseAdpter","Warehouse_Name::"+retStr);
        }catch (Exception e){}
        return retStr;

    }
}
