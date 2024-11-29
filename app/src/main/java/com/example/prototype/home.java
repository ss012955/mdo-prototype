package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import Adapters.viewPagerAdapter;
import HelperClasses.DashboardManager;
import Singleton.allAppointments;

public class home extends BaseActivity {

    ViewPager2 viewpager;
    viewPagerAdapter myAdapter;
    TabLayout tabLayout;
    private SharedPreferences prefs;
    DashboardManager dashboardManager;

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
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int tabPosition = getIntent().getIntExtra("tab_position", 0);
        checkLoginStatus();
        dashboardManager = new DashboardManager();

        tabLayout = findViewById(R.id.tablayout);
        myAdapter = new viewPagerAdapter(
                getSupportFragmentManager(),
                getLifecycle()
        );

        viewpager = findViewById(R.id.viewPager2);
        viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewpager.setAdapter(myAdapter);
        viewpager.setUserInputEnabled(false);
        fProfile profileFragment = new fProfile();

        myAdapter.addFragment(new fDashboard());
        myAdapter.addFragment(new fJournal());
        myAdapter.addFragment(profileFragment);


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
                                tab.setIcon(R.drawable.user_journal);
                                // Set icon for the second tab
                                break;
                            case 2:
                                tab.setIcon(R.drawable.profile);    // Set icon for the third tab
                                break;
                        }
                    }
                }
        ).attach();

        viewpager.setCurrentItem(tabPosition);

    }
    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();  // Re-check login status when activity is resumed
    }

    public void checkLoginStatus() {
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            String userEmail = prefs.getString("user_email", "No email found");
            int numberOfAppointments = allAppointments.getInstance().getNumberOfAppointments();
            //Toast.makeText(this, "Logged in as: " + userEmail + numberOfAppointments, Toast.LENGTH_SHORT).show();
        }else{
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        startActivity(new Intent(home.this, MainActivity.class));
        finish();  // Close home activity to prevent user from returning without logging in
    }

}