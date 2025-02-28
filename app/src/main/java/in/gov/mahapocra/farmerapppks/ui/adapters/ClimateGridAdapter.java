package in.gov.mahapocra.farmerapppks.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.DiseaseInformation;
import in.gov.mahapocra.farmerapppks.data.model.ClimateGridModel;

public class ClimateGridAdapter extends ArrayAdapter<ClimateGridModel> {

    String image_url;
    private Context mContext;
    String activity;



        public ClimateGridAdapter(@NonNull Context context, ArrayList<ClimateGridModel> ClimateGridModelArrayList, String activity) {
            super(context, 0, ClimateGridModelArrayList);
            this.mContext = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listitemView = convertView;
            if (listitemView == null) {
                // Layout Inflater inflates each item to be displayed in GridView.
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.single_climate_grid, parent, false);
            }
            ClimateGridModel ClimateGridModel = getItem(position);
            TextView climateName = listitemView.findViewById(R.id.climate_name);
            RelativeLayout gridItem = listitemView.findViewById(R.id.gridItem);
            ImageView climateImage = listitemView.findViewById(R.id.climate_image);
            climateName.setText(ClimateGridModel.getClimate_name());
            image_url= ClimateGridModel.getClimate_image();
            loadImage(climateImage,image_url);

            if(activity.equalsIgnoreCase("PestAndDiseasesAdpater")){
                gridItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ClimateGridAdapter.this.getContext(), DiseaseInformation.class);
                        //String Id = ClimateGridModel.getWebUrl();
                        intent.putExtra("id",Integer.parseInt(ClimateGridModel.getWebUrl()));
                        String currentString = ClimateGridModel.getClimate_name();
                        String[] separated = currentString.split("\n");
                        intent.putExtra("name",separated[1]);
                        mContext.startActivity(intent);
                    }
                });
            }
            return listitemView;

        }

    public static void loadImage(ImageView climateImage, String image_url) {
        try {
            Picasso.get().invalidate(image_url);
            Picasso.get()
                    .load(image_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_thumbnail)
                    .resize(500, 400)
                    .centerCrop()
                    .error(R.drawable.ic_thumbnail)
                    .into(climateImage);
        } catch (Exception ex) {
            ex.toString();
        }

    }
}
