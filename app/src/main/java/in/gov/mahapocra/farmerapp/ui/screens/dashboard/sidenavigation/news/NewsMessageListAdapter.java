/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 31/8/19 2:48 PM
 */

package in.gov.mahapocra.farmerapp.ui.screens.dashboard.sidenavigation.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapp.R;



public class NewsMessageListAdapter extends RecyclerView.Adapter<NewsMessageListAdapter.ViewHolder> {


    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    public JSONArray mDataArray;


    public NewsMessageListAdapter(Context mContext, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
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
        View base = LayoutInflater.from(mContext).inflate(R.layout.recycler_tittle_message_noif, parent, false);
        return new ViewHolder(base);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(mDataArray.getJSONObject(position), position, listener);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tittleTextView;
        private TextView messageTextView,dateTextView,readTextView;
        private  ImageView checkImageView;
         String notificationstr,data,notification_data,is_read,created_at,createdatetime;
         String title,body;
         LinearLayout mainLinearLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            tittleTextView = itemView.findViewById(R.id.tittleTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            checkImageView = itemView.findViewById(R.id.checkImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            readTextView = itemView.findViewById(R.id.readTextView);
            mainLinearLayout= itemView.findViewById(R.id.mainLinearLayout);


        }

        private void onBind(final JSONObject jsonObject, final int position, final OnMultiRecyclerItemClickListener listener) throws ParseException {


            NewsModel model = new NewsModel(jsonObject);

            title=model.getDate();
            notificationstr=model.getNewsHeading();

            tittleTextView.setText(title);
            messageTextView.setText(notificationstr);
            dateTextView.setText(title);



            //  NotificationListmodel model = new NotificationListmodel(jsonObject);
//            notification_data =model.getNotification_data();
//            is_read =model.getIs_read();
//            String unixcreatedTimeStamp = model.getCreated_at();
//            if(!(unixcreatedTimeStamp == null)) {
//                try {
//                    Date date = new Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                    created_at = sdf.format(date);
//                    Log.d("createdatetime", created_at);
//                }catch (NumberFormatException e){
//                    System.out.println("not a number");
//                }
//            }

   //         try {
//                JSONObject json = new JSONObject(notification_data);
//                notificationstr=json.getString("notification");
//                JSONObject notification = new JSONObject(notificationstr);
//                  title=notification.getString("title");
//                  body=notification.getString("body");
//                   Log.d("dsfgdgfdg", is_read);

//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//                if(is_read.equalsIgnoreCase(""))
//                {
//                 tittleTextView.setText(title);
//                 messageTextView.setText("Message  :"+body);
//                 dateTextView.setText(created_at);
//                 readTextView.setText("Unread");
//               //  mainLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.app_bg));
//                }
//                else {
//                    tittleTextView.setText(title);
//                    messageTextView.setText("Message  :"+body);
//                    dateTextView.setText(created_at);
//                    readTextView.setText("Read");
//                  //  mainLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//                }



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMultiRecyclerViewItemClick(1, jsonObject);
                }
            });
        }
    }
}
