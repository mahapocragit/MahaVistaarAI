package in.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationListBinding;

public class NewsListActivity extends AppCompatActivity {

    ActivityNotificationListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(getString(R.string.news));
        binding.relativeLayoutTopBar.imgBackArrow.setVisibility(View.VISIBLE);
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener(view-> getOnBackPressedDispatcher().onBackPressed());

        binding.farmNewsCardView.setOnClickListener(view -> {
            startActivity(new Intent(this, FarmNewsActivity.class));
        });

        binding.farmMagazineCardView.setOnClickListener(view -> {
            startActivity(new Intent(this, MagazineActivity.class));
        });
    }
}

