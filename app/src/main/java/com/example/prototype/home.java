package com.example.prototype;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    ViewPager2 viewpager;
    viewPagerAdapter myAdapter;
    TabLayout tabLayout;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tabLayout = findViewById(R.id.tablayout);
        title = findViewById(R.id.txtviewHomeTitle);
        myAdapter = new viewPagerAdapter(
                getSupportFragmentManager(),
                getLifecycle()
        );

        viewpager = findViewById(R.id.viewPager2);
        viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewpager.setAdapter(myAdapter);

        myAdapter.addFragment(new fDashboard());
        myAdapter.addFragment(new fJournal());
        myAdapter.addFragment(new fProfile());

        new TabLayoutMediator(
                tabLayout,
                viewpager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setIcon(R.drawable.home);  // Set icon for the first tab
                                break;
                            case 1:
                                tab.setIcon(R.drawable.user_journal);    // Set icon for the second tab
                                break;
                            case 2:
                                tab.setIcon(R.drawable.profile);    // Set icon for the third tab
                                break;
                        }
                    }
                }
        ).attach();

        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        title.setText("Dashboard");
                        break;
                    case 1:
                        title.setText("User Journal");
                        break;
                    case 2:
                        title.setText("Profile");
                        break;
                }
            }
        });


    }
}