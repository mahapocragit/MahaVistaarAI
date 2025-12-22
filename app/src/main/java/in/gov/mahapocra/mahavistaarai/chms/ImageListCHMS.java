package in.gov.mahapocra.mahavistaarai.chms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.mahapocra.mahavistaarai.R;


public class ImageListCHMS extends RecyclerView.Adapter<ImageListCHMS.ViewHolder> {
        private OnMultiRecyclerItemClickListener listener;
        private Context mContext;
        private JSONArray mJSONArray;
       // private int flag,categeryFlag;

        public ImageListCHMS(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray ) {
            this.mContext = context;
            this.mJSONArray = jsonArray;
            this.listener = listener;
          //  this.flag = flag;
         //   this.categeryFlag = categeryFlag;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.single_chms_activity_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d("MAYU",mJSONArray.toString());
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

            private TextView imageSectionName,imageCropName,imageDiseaseName;
            private ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageSectionName =(TextView)  itemView.findViewById(R.id.imageSectionName);
                imageCropName =(TextView)  itemView.findViewById(R.id.imageCropName);
                imageDiseaseName =(TextView)  itemView.findViewById(R.id.imageDiseaseName);
                imageView =(ImageView)  itemView.findViewById(R.id.imageView);
            }

            public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
                try {
                    imageSectionName.setText("Section  :" +jsonObject.getString("section"));
                    imageCropName.setText("Crop  :" +jsonObject.getString("crop"));
//
                    String strPest= jsonObject.getString("pest");
                    String strDisease= jsonObject.getString("disease");
                    if(strPest.equals("null") && strDisease.equals("null"))
                    {
                        imageDiseaseName.setVisibility(View.GONE);

                    }else if(strPest.equals("null"))
                    {
                        imageDiseaseName.setText("Disease :" + jsonObject.getString("disease"));
                    }else if(strDisease.equals("null"))
                    {
                        imageDiseaseName.setText("Pest  :" + jsonObject.getString("pest"));

                    }
//
//
                    //imageView.setImageUrl(jsonObject.getString("image"));
                    Picasso.get().load(jsonObject.getString("image")).into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onMultiRecyclerViewItemClick(2, jsonObject);
//                    }
//                });
            }
        }
    }

