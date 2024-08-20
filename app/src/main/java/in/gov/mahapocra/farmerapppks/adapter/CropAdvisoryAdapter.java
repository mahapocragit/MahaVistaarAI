package in.gov.mahapocra.farmerapppks.adapter;

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
import in.gov.mahapocra.farmerapppks.R;

public class CropAdvisoryAdapter extends RecyclerView.Adapter<CropAdvisoryAdapter.ViewHolder> {
    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;
    private String lang;

    public CropAdvisoryAdapter(Context context, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray, String lang) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.listener = listener;
        this.lang = lang;
    }

    @NonNull
    @Override
    public CropAdvisoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_crop_list, parent, false);
        return new CropAdvisoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropAdvisoryAdapter.ViewHolder holder, int position) {
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
        private ImageView imgCrop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView =(TextView)  itemView.findViewById(R.id.nameTextView);
            imgCrop =(ImageView)  itemView.findViewById(R.id.imageviewCrop);
        }

        public void setDvata(JSONObject jsonObject, OnMultiRecyclerItemClickListener listener) {
            try {
                String strNameEn= jsonObject.getString("name");
                String strNameMr=jsonObject.getString("name_mr");
                if(lang.equalsIgnoreCase("mr"))
                {
                    nameTextView.setText(strNameMr);
                }
                else
                {
                    nameTextView.setText(strNameEn);
                }
                String imgUrl= jsonObject.getString("image");
                Log.d("IMG","imgurl="+imgUrl);
               // imgCrop.setImageDrawable(Drawable.createFromPath(jsonObject.getString("image")));
                    Picasso.get().load(imgUrl).into(imgCrop);

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
