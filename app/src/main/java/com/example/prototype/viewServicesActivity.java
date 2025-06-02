package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import Adapters.ServicesAdapter;
import Adapters.viewServiceAdapter;
import HelperClasses.Services;

public class viewServicesActivity extends BaseActivity {
    TabLayout tabLayout;
    private RecyclerView recyclerView;
    private List<Services> services = new ArrayList<>();
    private viewServiceAdapter viewServiceAdapter;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_services);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatImageView = findViewById(R.id.chat);
        tabLayout = findViewById(R.id.tablayout);
        recyclerView = findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }
        tabLayout.selectTab(null);
        // Reset the tab icons to their unselected state
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(icons[i]); // Reset to the original icon (unselected)
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(viewServicesActivity.this, home.class)
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


        // Populate the services list
        services = new ArrayList<>();
        services.add(new Services("General Consultation"));
        services.add(new Services("Physical Examination"));
        services.add(new Services("Treatment for Minor Illnesses"));

        services.add(new Services("Dental Consultation"));
        services.add(new Services("Tooth Extraction"));
        services.add(new Services("Teeth Cleaning"));
        services.add(new Services("Dental Fillings"));
        // Check if services are added
        Log.d("View Services", "Number of services: " + services.size());
        viewServiceAdapter = new viewServiceAdapter(services);
        recyclerView.setAdapter(viewServiceAdapter);
        viewServiceAdapter.notifyDataSetChanged();

    }
}