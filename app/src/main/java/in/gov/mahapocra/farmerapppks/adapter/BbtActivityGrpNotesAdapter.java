package in.gov.mahapocra.farmerapppks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.mahapocra.farmerapppks.R;

public class BbtActivityGrpNotesAdapter extends RecyclerView.Adapter<BbtActivityGrpNotesAdapter.ViewHolder>  {

   // private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mJSONArray;

    public BbtActivityGrpNotesAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
       // this.listener = listener;
    }
    @NonNull
    @NotNull
    @Override
    public BbtActivityGrpNotesAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dbt_activity_list_deails, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BbtActivityGrpNotesAdapter.ViewHolder holder, int position) {
        Log.d("BbtActivityGrpNotes",mJSONArray.toString());
        try {
            holder.setDvata(mJSONArray.getJSONObject(position));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView =(TextView)  itemView.findViewById(R.id.nameTextView);
        }

        public void setDvata(JSONObject jsonObject) {
            try {
                nameTextView.setText(jsonObject.getString("Note"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
