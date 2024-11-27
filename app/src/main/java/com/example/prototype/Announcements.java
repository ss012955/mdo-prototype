package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.AnnouncementsAdapter;
import HelperClasses.AnnouncementsItems;
import HelperClasses.ItemClickListener;

public class Announcements extends BaseActivity implements ItemClickListener {
    private SharedPreferences prefs;
    private String userEmail;


    private RecyclerView recyclerView;
    private AnnouncementsAdapter announcementsAdapter;
    private List<AnnouncementsItems> announcementsList = new ArrayList<>();
    TabLayout tabLayout;
    private ImageView chatImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcements);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementsAdapter = new AnnouncementsAdapter(announcementsList);
        recyclerView.setAdapter(announcementsAdapter);
        chatImageView = findViewById(R.id.chat);

        // Set click listener for the chat image
        chatImageView.setOnClickListener(v -> {

        });

        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);


        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(Announcements.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });




    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchAnnouncements();
    }

    private void fetchAnnouncements() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/Admin/announcements.php?type=all"; // Update this URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                // Log the raw response
                Log.d("Announcements Response", response);

                // Parse the JSON array
                JSONArray jsonArray = new JSONArray(response);
                announcementsList.clear(); // Clear previous data

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject announcementObject = jsonArray.getJSONObject(i);

                    // Extract data for each announcement
                    String title = announcementObject.getString("title");
                    String text = announcementObject.getString("details");
                    String imageUrl = announcementObject.getString("image_url"); // Assuming there's an image URL

                    // Add the announcement item to the list
                    announcementsList.add(0,new AnnouncementsItems(title, text, imageUrl));
                }

                // Notify the adapter to update the RecyclerView
                announcementsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing announcements data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            // Handle network errors
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Announcements Fetch Error", error.getMessage());
        });

        // Add the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v, int position) {

    }



}

