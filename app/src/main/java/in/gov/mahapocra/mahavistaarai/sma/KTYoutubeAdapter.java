package in.gov.mahapocra.mahavistaarai.sma;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.gov.mahapocra.mahavistaarai.R;

public class KTYoutubeAdapter extends RecyclerView.Adapter<KTYoutubeAdapter.ViewHolder> {

    Context context;
    List<YoutubeVideo> list;

    public KTYoutubeAdapter(Context context, List<YoutubeVideo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YoutubeVideo video = list.get(position);

        // Load thumbnail
        String thumbnailUrl = "https://img.youtube.com/vi/" + video.getVideoId() + "/0.jpg";
        Glide.with(context).load(thumbnailUrl).into(holder.imgThumb);

        holder.txtTitle.setText(video.getTitle());
        holder.txtDetails.setText(video.getChannel() + " · " + video.getViews());

        holder.itemView.setOnClickListener(v -> {
            String url = "https://www.youtube.com/watch?v=" + video.getVideoId();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumb;
        TextView txtTitle, txtDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDetails = itemView.findViewById(R.id.txtDetails);
        }
    }
}



