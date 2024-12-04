package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Adapters.AnnouncementsAdapter;
import Adapters.contentJournal;
import Adapters.historyAdapter;
import Adapters.journalAdapter;
import HelperClasses.HistoryItem;

public class History extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView chatImageView;
    private List<HistoryItem> historyList = new ArrayList<>();
    private historyAdapter historyAdapter;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatImageView = findViewById(R.id.chat);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new historyAdapter(historyList);
        recyclerView.setAdapter(historyAdapter);

        chatImageView = findViewById(R.id.chat);
        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

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
                startActivity(new Intent(History.this, home.class)
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
        fetchHistory();
    }

    private void fetchHistory() {
        new Thread(() -> {
            try {
                // Corrected URL assignment with fullUrl
                String url = "http://192.168.100.4/MDOapp-main/Admin/fetch_booking_completed.php";
                String userEmail = "john.doe@umak.edu.ph";
                String fullUrl = url + "?user_email=" + userEmail; // Concatenate user_email parameter

                // Open connection using fullUrl instead of url
                HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
                connection.setRequestMethod("GET");

                // Read the response from the input stream
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                historyList.clear();

                // Loop through the results and extract the required data
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject booking = jsonArray.getJSONObject(i);

                    // Extract all fields from JSON
                    int id = booking.getInt("booking_id");
                    String serviceType = booking.getString("service_type");
                    String bookingDate = booking.getString("booking_date");
                    String bookingTime = booking.getString("booking_time");
                    String remarks = booking.getString("remarks");
                    String status = booking.getString("status");

                    // Combine details into a single string for display
                    String details = "Service: " + serviceType + "\n" +
                            "Date: " + bookingDate + "\n" +
                            "Time: " + bookingTime + "\n" +
                            "Remarks: " + remarks + "\n" +
                            "Status: " + status;

                    // Add to the history list
                    historyList.add(new HistoryItem(serviceType, details));
                }

                // Update the UI with the new data
                runOnUiThread(() -> historyAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
