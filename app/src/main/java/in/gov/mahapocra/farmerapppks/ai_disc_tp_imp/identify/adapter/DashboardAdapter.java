package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.adapter;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.Model_Dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private ArrayList<Model_Dashboard> model_dashboardContents;
    private Context context;

    public DashboardAdapter(Context context, ArrayList<Model_Dashboard> dashboardContents)
    {
        this.model_dashboardContents = dashboardContents;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_dashboard, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.gridText.setText(model_dashboardContents.get(i).getGetGridText());

        //viewHolder.gridImage.setColorFilter(Color.WHITE, PorterDuff.Mode.);
        try {
            if(!model_dashboardContents.get(i).getGridImage().matches("")){
                String imagePath = "https://nibpp.krishimegh.in/Content/reportingbase/crop_file/" + model_dashboardContents.get(i).getGridImage();
                //card_view1.setVisibility(View.VISIBLE);
                //Log.d("56565", imagePath);
                Glide.with(context)
                        .load(imagePath.trim())
                        .placeholder(R.drawable.facilities)
                        .error(R.drawable.gray)
                        .into(viewHolder.gridImage);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public int getItemCount() {
        return model_dashboardContents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView gridText;
        private final ImageView gridImage;
        public ViewHolder(View view)
        {
            super(view);
            gridText = (TextView)view.findViewById(R.id.grid_text);
            gridImage = (ImageView) view.findViewById(R.id.grid_image);
        }
    }

}
