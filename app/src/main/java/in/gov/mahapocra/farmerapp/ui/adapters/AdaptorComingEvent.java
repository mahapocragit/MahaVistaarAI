package in.gov.mahapocra.farmerapp.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.farmerapp.R;

public class AdaptorComingEvent extends RecyclerView.Adapter<AdaptorComingEvent.ViewHolder> {

    private Context mContext;
    private JSONArray mJsonArray;
    public JSONArray mPJSONArray;
    private Boolean mOptToShow;
    private OnMultiRecyclerItemClickListener mListener;

    public AdaptorComingEvent(Context context, JSONArray jsonArray, boolean optionToShow, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mPJSONArray = jsonArray;
        this.mOptToShow = optionToShow;
        this.mListener = listener;
    }
    @Override
    public int getItemCount() {
        if (mJsonArray != null && mJsonArray.length() != 0) {
            Log.d("mJsonArray.length()", String.valueOf(mJsonArray.length()));
            return mJsonArray.length();
        } else {
            return 0;
        }
    }

    @NonNull
    @NotNull
    @Override
    public AdaptorComingEvent.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_event_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdaptorComingEvent.ViewHolder holder, int i) {

        try {
            holder.onBind(mJsonArray.getJSONObject(i), i,mListener);

            JSONObject dataJson = mJsonArray.getJSONObject(i);

            // Schedule Start Date
            String sTime = dataJson.getString("start_date");

            // Schedule End Date
            String eTime = dataJson.getString("end_date");


            if (mOptToShow){


            }else {
                holder.optMoreImageView.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout scheduleLLayout;
        private final TextView eventSDateTView;
        private final TextView eventEDateTView;
        private final TextView eventVenueTView;
        private final TextView eventTypeTView;
        private final TextView participantTView;
        private final TextView eventTitleTView;
        private final ImageView optMoreImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scheduleLLayout = (LinearLayout) itemView.findViewById(R.id.scheduleLLayout);
            eventSDateTView = (TextView) itemView.findViewById(R.id.eventSDateTView);
            eventEDateTView = (TextView) itemView.findViewById(R.id.eventEDateTView);
            eventVenueTView = (TextView) itemView.findViewById(R.id.eventVenueTView);
            eventTypeTView = (TextView) itemView.findViewById(R.id.eventTypeTView);
            participantTView = (TextView) itemView.findViewById(R.id.participantTView);
            eventTitleTView = (TextView) itemView.findViewById(R.id.eventTitleTView);
            optMoreImageView = (ImageView) itemView.findViewById(R.id.optMoreImageView);

        }

        public void onBind(final JSONObject jsonObject, final int i,  OnMultiRecyclerItemClickListener mListener) {

            try {

                String sTime = jsonObject.getString("start_date");

                String eTime = jsonObject.getString("end_date");



                String other_venue = "";

                if (jsonObject.isNull("other_venue")) {
                    other_venue = "";
                } else {
                    other_venue = jsonObject.getString("other_venue");
                }

                String eVenue = jsonObject.getString("venue");
                String venueDetails= "";
                String eType = jsonObject.getString("event_type");
                String participants = jsonObject.getString("participints");

                String eTitle = "";
                String subType = jsonObject.getString("event_sub_type_name");
                if (!subType.equalsIgnoreCase("Others")){
                    if (jsonObject.isNull("event_sub_type_name")) {
                        eTitle = jsonObject.getString("title");
                    } else {
                        eTitle = subType;
                    }
                }else {
                    eTitle = jsonObject.getString("title");
                }


                if (other_venue != null && !other_venue.isEmpty()) {
                    venueDetails = other_venue;
                } else {
                    venueDetails = eVenue;
                }
                int serialno = i+1;
                eventSDateTView.setText(""+serialno+". "+"From:"+sTime+" ");
                eventEDateTView.setText("To:"+eTime);
                eventVenueTView.setText(venueDetails);
                eventTypeTView.setText(eType);
                participantTView.setText(participants);
                eventTitleTView.setText(eTitle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMultiRecyclerViewItemClick(2, jsonObject);
                }
            });


        }
    }


}
