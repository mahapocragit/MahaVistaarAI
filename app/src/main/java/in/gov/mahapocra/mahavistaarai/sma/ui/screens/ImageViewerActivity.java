

package in.gov.mahapocra.mahavistaarai.sma.ui.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import in.gov.mahapocra.mahavistaarai.R;


public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        initComponents();
        setConfiguration();
    }

    private void initComponents() {

        imageView = findViewById(R.id.imageView);
    }

    private void setConfiguration() {

        findViewById(R.id.closeImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getStringExtra("mURL") != null) {
            String mURL = getIntent().getStringExtra("mURL");
            Picasso.get()
                    .load(mURL)
                    .fit()
                    .into(imageView);
        }

    }

}
