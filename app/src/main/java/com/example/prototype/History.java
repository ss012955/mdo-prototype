package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private SharedPreferences prefs;
    private String userEmail;


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
        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);


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
                String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_booking_completed.php";
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

                // Create SimpleDateFormat for the desired date and time formats
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // assuming the date format from the server is yyyy-MM-dd
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM. dd, yyyy", Locale.US); // desired format: Nov. 20, 2024
                SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm", Locale.US); // assuming the time format from the server is 24-hour (HH:mm)
                SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US); // desired format: 08:00 AM

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

                    // Parse and format the booking date
                    Date date = inputDateFormat.parse(bookingDate);
                    String formattedDate = outputDateFormat.format(date);

                    // Parse and format the booking time
                    Date time = inputTimeFormat.parse(bookingTime);
                    String formattedTime = outputTimeFormat.format(time);

                    // Combine details into a single string for display
                    String details = "Service: " + serviceType + "\n" +
                            "Date: " + formattedDate + "\n" +
                            "Time: " + formattedTime + "\n" +
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