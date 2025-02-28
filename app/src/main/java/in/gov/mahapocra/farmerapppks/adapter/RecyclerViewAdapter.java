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

import java.util.ArrayList;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.data.DataModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    ArrayList mValues;
    Context mContext;
    protected ItemListener mListener;

    public RecyclerViewAdapter(Context context, ArrayList values, ItemListener itemListener) {

        mValues = values;
        mContext = context;
        mListener=itemListener;
    }
    int weight = 6;
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);

        // view.getLayoutParams().height = parent.getWidth() / 3;

        return new ViewHolder(view);
    }

    @NonNull


    @Override
    public void onBindViewHolder( ViewHolder Vholder, int position) {
        Vholder.setData((DataModel)mValues.get(position));
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        //public RelativeLayout relativeLayout;
        DataModel item;

        public ViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);

            //   relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }

        public void setData(DataModel item) {
            this.item = item;
            Log.d("jvkdsjfslkdj", item.text);
            textView.setText(item.text);
            imageView.setImageResource(item.drawable);
            //   relativeLayout.setBackgroundColor(Color.parseColor(item.color));

        }
        @Override
        public void onClick(View view) {
//            if (mListener != null) {
//                mListener.onItemClick(item);
//
//            }
        }
    }
    public interface ItemListener {
        void onItemClick(DataModel item);
    }
}
