package in.gov.mahapocra.mahavistaarai.sma.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.data.helper.AppHelper;
import in.gov.mahapocra.mahavistaarai.sma.data.models.CalendarModelKT;

public class KTCalendarReportAdapter extends RecyclerView.Adapter<KTCalendarReportAdapter.ViewHolder> {

    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mDataArray;
    String result;

    public KTCalendarReportAdapter(Context mContext, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
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
        View base = LayoutInflater.from(mContext).inflate(R.layout.recycler_kt_caleder_xml, parent, false);
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

        private RelativeLayout relativeLayout;
        private TextView dayTextView;
        private TextView reasonTextView;
        private TextView inTextView;
        private TextView outTextView;
        private TextView hrTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            reasonTextView = itemView.findViewById(R.id.reasonTextView);
            inTextView = itemView.findViewById(R.id.inTextView);
            outTextView = itemView.findViewById(R.id.outTextView);
            hrTextView = itemView.findViewById(R.id.hrTextView);
        }

        private void onBind(final JSONObject jsonObject, final OnMultiRecyclerItemClickListener listener) {

            CalendarModelKT calendarModel = new CalendarModelKT(jsonObject);

            String date = AppHelper.getInstance().getAttendanceDate(calendarModel.getDate());

            dayTextView.setText(date);

            if (calendarModel.getIs_present() == 1) {

                if ((calendarModel.getIs_present() == 1) && (calendarModel.getIs_holiday() == 1) ) {
                    relativeLayout.setBackgroundResource(R.color.schedule_up);

                } else {
                    relativeLayout.setBackgroundResource(R.color.status_green);
                }

                if (calendarModel.getCategory_type().isEmpty()) {
                    reasonTextView.setVisibility(View.GONE);
                    inTextView.setPadding(0,10,0,0);
                } else {
                    reasonTextView.setVisibility(View.VISIBLE);
//                    reasonTextView.setText(calendarModel.getCategory_type());
                    reasonTextView.setText(calendarModel.getCategoryTypeMr());
                }

//                inTextView.setVisibility(View.VISIBLE);
//                outTextView.setVisibility(View.VISIBLE);
//                hrTextView.setVisibility(View.VISIBLE);

//                String inTime =  AppHelper.getInstance().getTime(calendarModel.getReport_time());
//                String outTime =  AppHelper.getInstance().getTime(calendarModel.getOut_time());
//                inTextView.setText("Meeting");
//                outTextView.setText("Visit");

//                inTextView.setText(inTime);
//                outTextView.setText(outTime);

//                String result = (String) DateUtils.getRelativeTimeSpanString(calendarModel.getReport_time(), calendarModel.getOut_time(), 0);

//                result = AppHelper.getInstance().calculateTotalHours(calendarModel.getReport_time(), calendarModel.getOut_time());
//                String totalArr[] = result.split("_");
//                if (totalArr[0].equalsIgnoreCase("0")) {
//                    String hr = "M="+totalArr[1];
//                    hrTextView.setText(hr);
//                } else {
//                    String hr = "H="+(totalArr[0]+":"+totalArr[1]);
//                    hrTextView.setText(hr);
//                }

//                DebugLog.getInstance().d("Total Hr="+result);

            } else {

                if (calendarModel.getIs_holiday() == 1) {
                    reasonTextView.setText("H");
                    reasonTextView.setTypeface(reasonTextView.getTypeface(), Typeface.BOLD);
                    reasonTextView.setTextColor(mContext.getResources().getColor(R.color.red));
                    relativeLayout.setBackgroundResource(R.color.white_dim_light);

                }
//                else if (calendarModel.getIs_on_leave() == 1) {
//                    reasonTextView.setText("L");
//                    reasonTextView.setTypeface(reasonTextView.getTypeface(), Typeface.BOLD);
//                    reasonTextView.setTextColor(mContext.getResources().getColor(R.color.red));
//                    relativeLayout.setBackgroundResource(R.color.white_dim_light);
//                }
                else {
                    if (calendarModel.getIs_present() == 2) { //2 Means future dates coming
                        reasonTextView.setText("");
                        relativeLayout.setBackgroundResource(R.color.gray);
                    }else if(calendarModel.getIs_present() == 3) { //2 Means future dates coming
                        reasonTextView.setText("");
                        relativeLayout.setBackgroundResource(R.color.white_light);
                    }else {
                        reasonTextView.setText("");
                        relativeLayout.setBackgroundResource(R.color.red);
                    }
                }

                reasonTextView.setTextSize(16);
                inTextView.setVisibility(View.GONE);
                outTextView.setVisibility(View.GONE);
                hrTextView.setVisibility(View.GONE);
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
