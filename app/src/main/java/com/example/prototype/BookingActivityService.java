package com.example.prototype;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Adapters.ServiceTypeAdapter;
import Adapters.ServicesAdapter;
import HelperClasses.ItemClickListener;
import HelperClasses.ServiceType;
import HelperClasses.Services;

public class BookingActivityService extends BaseActivity implements ItemClickListener {
    TabLayout tabLayout;
    private RecyclerView recyclerView;
    TextView title;
    private List<Services> services;
    ServicesAdapter adapter = new ServicesAdapter(services);
    String service;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");
        //Toast.makeText(BookingActivityService.this, userEmail, Toast.LENGTH_SHORT).show();
        chatImageView = findViewById(R.id.chat);
        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(BookingActivityService.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        chatImageView.setOnClickListener(v -> {

            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });
        Intent intent = getIntent();
        service = intent.getStringExtra("Service");

        if (service != null) {
            //Toast.makeText(this, "Selected Service: " + service, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No service selected.", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.serviceTitle);
        if (service != null) {
            //Toast.makeText(this, "Selected Service: " + service, Toast.LENGTH_SHORT).show();

            services = new ArrayList<>();

            if (service.equals("Medical")) {
                title.setText("Medical Services");
                services.add(new Services("General Consultation"));
                services.add(new Services("Health Screening"));
                services.add(new Services("Vaccination Services"));
                services.add(new Services("Referral to Specialists"));
                services.add(new Services("Health Education"));
                services.add(new Services("Physical Examination"));
                services.add(new Services("Treatment for Minor Illnesses"));
                services.add(new Services("Laboratory Services"));
                services.add(new Services("Medical Certificate for OJT"));
            } else if (service.equals("Dental")) {
                title.setText("Dental Services");
                services.add(new Services("Dental Consultation"));
                services.add(new Services("Tooth Extraction"));
                services.add(new Services("Teeth Cleaning"));
                services.add(new Services("Dental Fillings"));
                services.add(new Services("Dental Health Education"));
                services.add(new Services("Emergency Dental Care"));
                services.add(new Services("Referrals to Dental Specialists"));
            } else {
                //Toast.makeText(this, "Unknown service type selected.", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(this, "No service selected.", Toast.LENGTH_SHORT).show();
            services = new ArrayList<>(); // Initialize an empty list
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ServicesAdapter(services);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

    }

    @Override
    public void onClick(View v, int position) {
        if (position >= 0 && position < services.size()) { // Validate the position

            String serviceType = services.get(position).getServiceTitle();
            Intent intent = new Intent(this, BookingActivityDate.class);
            intent.putExtra("Service", service);
            intent.putExtra("ServiceType", serviceType);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Invalid service selection.", Toast.LENGTH_SHORT).show();
        }
    }
}