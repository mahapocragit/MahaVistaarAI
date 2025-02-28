package in.gov.mahapocra.farmerapppks.ui.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.gov.mahapocra.farmerapppks.R;

public class DashboardAdapter extends BaseAdapter {
    private final Context context;
    private final String[] mobileValues;
    private final int[] mobileImg;
    private final String gridItemType;

    public DashboardAdapter(Context context, String[] mobileValues, int[] mobileImg,String gridItemType) {
        this.context = context;
        this.mobileValues = mobileValues;
        this.mobileImg = mobileImg;
        this.gridItemType = gridItemType;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.single_item_grid, parent, false);
            final TextView textView = gridView.findViewById(R.id.grid_item_label);
            textView.setText(mobileValues[position]);
            ImageView imageView = gridView.findViewById(R.id.grid_item_image);
            imageView.setImageResource(mobileImg[position]);
            notifyDataSetChanged();
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
