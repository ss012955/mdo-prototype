package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;

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

import Adapters.FAQsAdapter;
import HelperClasses.FAQsItem;

public class viewFAQsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FAQsAdapter faqsAdapter;
    private List<FAQsItem> faqsList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_faqs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerViewFAQs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tabLayout = findViewById(R.id.tablayout);
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
                startActivity(new Intent(viewFAQsActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        faqsList =new ArrayList<>();
        populateFAQS();

        // Set Adapter
        faqsAdapter = new FAQsAdapter(faqsList);
        recyclerView.setAdapter(faqsAdapter);
    }

    public void populateFAQS(){
        faqsList.add(new FAQsItem("What are the clinic hours?", "Clinic hours are 8 AM to 5 PM on weekdays."));
        faqsList.add(new FAQsItem("Do I need an appointment?", "Appointments are recommended but not required."));
        faqsList.add(new FAQsItem("Are vaccinations available?", "Yes, vaccinations are available upon request."));
        faqsList.add(new FAQsItem("Is emergency care provided?", "Emergency care is not provided. Please visit a hospital."));
        faqsList.add(new FAQsItem("Do you offer health screening services?", "Yes, we offer a range of health screening services."));
        faqsList.add(new FAQsItem("Is dental care available?", "Yes, dental care is available, including cleaning and extractions."));}
}