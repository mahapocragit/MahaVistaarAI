package in.gov.mahapocra.farmerapppks.adapter;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import in.gov.mahapocra.farmerapppks.R;

public class DashboardAdapter extends BaseAdapter {
    private Context context;
    private String[] mobileValues;
    private int[] mobileImg;
    private  String gridItemType;
    public DashboardAdapter(Context context, String[] mobileValues, int[] mobileImg,String gridItemType) {
        this.context = context;
        this.mobileValues = mobileValues;
        this.mobileImg = mobileImg;
        this.gridItemType = gridItemType;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
           // gridView = inflater.inflate(R.layout.list_item, null);
            if(gridItemType.equalsIgnoreCase("stage_single_item_grid")){
                gridView = inflater.inflate(R.layout.stage_single_item_grid, parent, false);
            }else {
                gridView = inflater.inflate(R.layout.single_item_grid, parent, false);
            }


            // set value into textview
            final TextView textView = gridView
                    .findViewById(R.id.grid_item_label);
            textView.setText(mobileValues[position]);

            // set image based on selected text
            ImageView imageView = gridView
                    .findViewById(R.id.grid_item_image);
            imageView.setImageResource(mobileImg[position]);
            notifyDataSetChanged();
            //  RelativeLayout relLayoutGridItemSingle=gridView.findViewById(R.id.relLayoutGridItemSingle);

//            if(position==mobileValues.length-1)
//            {
//                relLayoutGridItemSingle.setBackground(
//                        ContextCompat.getDrawable(
//                                context,R.drawable.imgrb
//                        )
//                );
//            }
//            else if(position==mobileValues.length-4)
//            {
//                relLayoutGridItemSingle.setBackground(
//                        ContextCompat.getDrawable(
//                                context,R.drawable.imglb
//                        )
//                );
//            }
//            else if(position==0)
//            {
//                relLayoutGridItemSingle.setBackground(
//                        ContextCompat.getDrawable(
//                                context,R.drawable.imglt
//                        )
//                );
//            }
//            else if(position==3)
//            {
//                relLayoutGridItemSingle.setBackground(
//                        ContextCompat.getDrawable(
//                                context,R.drawable.imgrt
//                        )
//                );
//            }
//            else
//            {
//                relLayoutGridItemSingle.setBackground(
//                        ContextCompat.getDrawable(
//                                context,R.drawable.imgmid
//                        )
//                );
//            }

//            String mobile = mobileValues[position];
//
//            if (mobile.equals("Windows")) {
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//            } else if (mobile.equals("iOS")) {
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//            } else if (mobile.equals("Blackberry")) {
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//            } else {
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//            }

        } else {
            gridView = convertView;
        }
        return gridView;
    }

    @Override
    public int getCount() {
        return mobileValues.length;

    }

    @Override
    public Object getItem(int position) {
        return null;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
