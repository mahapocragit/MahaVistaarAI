package in.gov.mahapocra.mahavistaarai.ui.screens.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import in.gov.mahapocra.mahavistaarai.R;

public class TempDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageViewHeaderBack;
    private TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_dashboard);

        headerTextView = findViewById(R.id.textViewHeaderTitle);
        imageViewHeaderBack = findViewById(R.id.imageViewHeaderBack);
        imageViewHeaderBack.setVisibility(View.VISIBLE);
        imageViewHeaderBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        headerTextView.setText("Agro Assistant");
        loadFragment(new ChatbotFragment());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Toast.makeText(TempDashboardActivity.this, "nav_home", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.nav_about:
                    Toast.makeText(TempDashboardActivity.this, "nav_about", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.nav_about2:
                    Toast.makeText(TempDashboardActivity.this, "nav_help", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.nav_chat:
                    loadFragment(new ChatbotFragment());
                    return true;

                default:
                    return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}