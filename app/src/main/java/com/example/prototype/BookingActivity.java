package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import HelperClasses.BookingManager;
import HelperClasses.ItemClickListener;
import HelperClasses.ServiceType;

public class BookingActivity extends AppCompatActivity implements ItemClickListener {
    TabLayout tabLayout;
    ProgressBar progressBar;
    private RecyclerView recyclerView;

    private List<ServiceType> serviceType;
    ServiceTypeAdapter adapter = new ServiceTypeAdapter(serviceType);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(BookingActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        BookingManager.smoothProgressUpdate(progressBar);

        recyclerView = findViewById(R.id.recyclerView);

        serviceType = new ArrayList<>();

        ServiceType s1 = new ServiceType(R.drawable.medical_services, "Medical Services");
        ServiceType s2 = new ServiceType(R.drawable.dental_services, "Dental Services");
        serviceType.add(s1);
        serviceType.add(s2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ServiceTypeAdapter(serviceType);

        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

    }


    @Override
    public void onClick(View v, int position) {
            Toast.makeText(
                    this,
                    "You Chose " + serviceType.get(position).getServiceTitle(),
                    Toast.LENGTH_SHORT).show();
    }
}