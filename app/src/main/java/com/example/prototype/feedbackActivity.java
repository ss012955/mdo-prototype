package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class feedbackActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    private List<ImageView> stars;
    private int currentRating = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize stars
        stars = new ArrayList<>();
        stars.add(findViewById(R.id.star1));
        stars.add(findViewById(R.id.star2));
        stars.add(findViewById(R.id.star3));
        stars.add(findViewById(R.id.star4));
        stars.add(findViewById(R.id.star5));

        chatImageView = findViewById(R.id.chat);
        tabLayout = findViewById(R.id.tablayout);

        // Set click listeners for each star
        for (int i = 0; i < stars.size(); i++) {
            final int index = i; // Needed because variables in Java lambdas must be final or effectively final
            stars.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRating(index + 1);
                }
            });
        }


        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

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
                startActivity(new Intent(feedbackActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


    }
    private void updateRating(int rating) {
        currentRating = rating;
        for (int i = 0; i < stars.size(); i++) {
            if (i < rating) {
                stars.get(i).setImageResource(R.drawable.star_filled); // Use the filled star drawable
            } else {
                stars.get(i).setImageResource(R.drawable.star_empty); // Use the empty star drawable
            }
        }
    }
}