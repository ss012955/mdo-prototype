package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class ConfirmationActivity extends AppCompatActivity {
    TextView tvService, tvServiceType, tvDate, tvremarks;
    String service, serviceType, chosen_date, chosen_time, remarksInput;
    TabLayout tabLayout;
    Button buttonClose;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");
        Toast.makeText(ConfirmationActivity.this, userEmail, Toast.LENGTH_SHORT).show();

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(ConfirmationActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        service = intent.get().getStringExtra("Service");
        serviceType = intent.get().getStringExtra("ServiceType");
        chosen_date = intent.get().getStringExtra("ChosenDate"); // Corrected the key name
        chosen_time = intent.get().getStringExtra("ChosenTime"); // Corrected the key name
        remarksInput = intent.get().getStringExtra("Remarks");

        // Fallback if remarksInput is null or empty
        if (remarksInput == null || remarksInput.isEmpty()) {
            remarksInput = "No remarks provided"; // Default message
        }

        tvService = findViewById(R.id.tvService);
        tvDate = findViewById(R.id.tvDate);
        tvremarks = findViewById(R.id.tvremarks);

        try {
            // Assuming chosen_date is in "MM/dd/yyyy" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);

            Date date = inputFormat.parse(chosen_date);
            String formattedDate = outputFormat.format(date);

            String serviceText = String.format("%-16s %s", "Service:", serviceType);
            String dateText = String.format("%-14s %s / %s", "Date/Time:", formattedDate, chosen_time);
            String remarks = String.format("%-14s %s", "Remarks:", remarksInput);

            tvService.setText(serviceText);
            tvDate.setText(dateText);
            tvremarks.setText(remarks);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception better for debugging
        }

        buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(v->{
            intent.set(new Intent(ConfirmationActivity.this, home.class));
            startActivity(intent.get());
        });
    }
}