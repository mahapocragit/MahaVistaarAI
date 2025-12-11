package in.gov.mahapocra.mahavistaarai.sma;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.gov.mahapocra.mahavistaarai.R;

public class KTProjectInformation extends AppCompatActivity {

    LinearLayout itemDocuments, itemGuidelines, itemVideos ,itemMahavistar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktproject_information);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        itemDocuments = findViewById(R.id.itemDocuments);
        itemGuidelines = findViewById(R.id.itemGuidelines);
        itemVideos = findViewById(R.id.itemVideos);
        itemMahavistar = findViewById(R.id.itemMahavistar);

        itemDocuments.setOnClickListener(v ->
//                openUrl("https://mahapocra.gov.in/mr/document")
                openInternalWebView("https://mahapocra.gov.in/mr/document"));
        itemMahavistar.setOnClickListener(v ->
//                openUrl("https://play.google.com/store/apps/details?id=in.gov.mahapocra.mahavistaarai&hl=mr"));
                openInternalWebView("https://play.google.com/store/apps/details?id=in.gov.mahapocra.mahavistaarai&hl=mr"));

        itemGuidelines.setOnClickListener(v ->
//                openUrl("https://mahapocra.gov.in/mr/project_guidelines"));
                 openInternalWebView("https://mahapocra.gov.in/mr/project_guidelines"));

        itemVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(KTProjectInformation.this, KTYoutubeView.class);
//                startActivity(intent);
                Toast.makeText(KTProjectInformation.this, "Comming Soon", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
    private void openInternalWebView(String url) {
        Intent intent = new Intent(KTProjectInformation.this, WebviewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}