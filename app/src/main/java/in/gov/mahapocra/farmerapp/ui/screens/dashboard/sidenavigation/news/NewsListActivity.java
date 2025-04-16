package in.gov.mahapocra.farmerapp.ui.screens.dashboard.sidenavigation.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import in.gov.mahapocra.farmerapp.databinding.ActivityNotificationListBinding;

public class NewsListActivity extends AppCompatActivity {

    ActivityNotificationListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.farmNewsCardView.setOnClickListener(view -> {
            startActivity(new Intent(this, FarmNewsActivity.class));
        });

        binding.farmMagazineCardView.setOnClickListener(view -> {
            startActivity(new Intent(this, MagazineActivity.class));
        });
    }
}

