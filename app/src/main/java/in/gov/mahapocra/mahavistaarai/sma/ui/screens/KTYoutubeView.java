package in.gov.mahapocra.mahavistaarai.sma.ui.screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.data.models.YoutubeVideo;
import in.gov.mahapocra.mahavistaarai.sma.ui.adapters.KTYoutubeAdapter;

public class KTYoutubeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktyoutube_view);
        RecyclerView rvVideos = findViewById(R.id.rvVideos);

        rvVideos.setLayoutManager(new GridLayoutManager(this, 2));

        List<YoutubeVideo> videoList = new ArrayList<>();

        videoList.add(new YoutubeVideo(
                "SCyIY40J808",
                "Maha Sarkari Yojana",
                "POCRA Official",
                "1.2M views · 2 years ago"
        ));

        videoList.add(new YoutubeVideo(
                "ajiQhxpfddo",
                "PoCRA Yojana",
                "Agriculture Dept",
                "820K views · 1 year ago"
        ));

// Add your YouTube video IDs here
//        videoList.add(new YoutubeVideo("SCyIY40J808"));
//        videoList.add(new YoutubeVideo("SCyIY40J808"));
//        videoList.add(new YoutubeVideo("SCyIY40J808"));
//        videoList.add(new YoutubeVideo("SCyIY40J808"));



// etc...

        KTYoutubeAdapter adapter = new KTYoutubeAdapter(this, videoList);
        rvVideos.setAdapter(adapter);
    }
}