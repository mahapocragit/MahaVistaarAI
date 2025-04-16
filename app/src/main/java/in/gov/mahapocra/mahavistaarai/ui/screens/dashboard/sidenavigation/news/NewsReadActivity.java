package in.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen;
import in.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen;
import in.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppConstants;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;

public class NewsReadActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private TextView tittleTextView, textViewHeaderTitle;
    private TextView messageTextView, dateTextView, urlInfoTextView;
    private ImageView imageMenushow, climateImage;
    String created_at;
    String title, body, urlLink, userId;
    private int farmerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);
        initComponents();
        try {
            setConfiguration();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
                if (farmerId > 0) {
                    startActivity(new Intent(this, DashboardScreen.class));
                    finish();
                } else {
                    startActivity(new Intent(this, LoginScreen.class));
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initComponents() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
        imageMenushow = findViewById(R.id.imgBackArrow);
        climateImage = findViewById(R.id.climate_images);
        tittleTextView = findViewById(R.id.tittleTextView);
        messageTextView = findViewById(R.id.messageTextView);
        dateTextView = findViewById(R.id.dateTextView);
        urlInfoTextView = findViewById(R.id.urlInfoTextView);
    }

    private void setConfiguration() throws JSONException {
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.news_details);
        imageMenushow.setOnClickListener(v -> {
            Intent intent = new Intent(NewsReadActivity.this, NewsListActivity.class);
            startActivity(intent);
        });


        if (farmerId > 0) {
            if (getIntent() != null) {
                String mData = getIntent().getStringExtra("noticationData");
                if (!(null == mData)) {
                    JSONObject jsonObject = new JSONObject(mData);
                    NewsModel model = new NewsModel(jsonObject);
                    title = model.getNewsHeading();
                    body = model.getNews();
                    urlLink = model.getHeaderImage();
                } else {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        title = bundle.getString("title");
                        body = bundle.getString("body");
                        urlLink = bundle.getString("urlLink");
                        String unixcreatedTimeStamp = bundle.getString("createdate");
                        if (!(unixcreatedTimeStamp == null)) {
                            try {
                                Date date = new java.util.Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                                created_at = sdf.format(date);
                                Log.d("createdatetime", created_at);
                            } catch (NumberFormatException e) {
                                System.out.println("not a number");
                            }
                        }
                    }
                }
            }
            if (urlLink == null || urlLink.equalsIgnoreCase("")) {
                urlInfoTextView.setVisibility(View.GONE);
                climateImage.setVisibility(View.GONE);
                tittleTextView.setText("" + title);
                messageTextView.setText("Message   : " + body);
                dateTextView.setText(created_at);
            } else {
                tittleTextView.setText("" + title);
                messageTextView.setText("Message : " + body);
                dateTextView.setText(created_at);
                urlInfoTextView.setTextColor(Color.parseColor("#000080"));
                climateImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(urlLink)
                        .into(climateImage);
                urlInfoTextView.setOnClickListener(view -> {
                    if (urlLink.contains("http")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink));
                        startActivity(browserIntent);
                    }
                });
            }
            userId = "6289";
        } else {
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {
            ResponseModel response = new ResponseModel(jsonObject);
            if (i == 1) {
                if (response.getStatus()) {
                    //  UIToastMessage.show(this, response.getResponse());
                } else {
                    UIToastMessage.show(this, response.getResponse());
                }
            }
        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
    }
}