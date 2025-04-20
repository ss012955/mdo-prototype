package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewHistorySingle extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    private SharedPreferences prefs;
    String userEmail;
    TextView tvBookingTitle, tvDetails, tvMedicalInfo, tvVitalInfo;
    private int bookingId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_history_single);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

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
                startActivity(new Intent(ViewHistorySingle.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        tvBookingTitle = findViewById(R.id.tvBookingTitle);
        tvDetails = findViewById(R.id.tvDetails);
        tvMedicalInfo = findViewById(R.id.tvMedical);
        tvVitalInfo = findViewById(R.id.tvVital);
        progressBar = findViewById(R.id.progress_bar);

        // Get data from intent
        Intent clickedHistory = getIntent();
        if (clickedHistory != null) {
            bookingId = clickedHistory.getIntExtra("historyID", 0);
            String historyTitle = clickedHistory.getStringExtra("historyTitle");
            String historyDetails = clickedHistory.getStringExtra("historyDetails");

            // Display basic info

            tvBookingTitle.setText(historyTitle);
            tvDetails.setText(historyDetails);
            // Show loading state
            progressBar.setVisibility(View.VISIBLE);

            // Fetch detailed medical information
            fetchMedicalDetails(bookingId);
        }
    }

    private void fetchMedicalDetails(int bookingId) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_medicaldetails.php?booking_id=" + bookingId;

        new Thread(() -> {
            try {
                // Open connection to the server
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                // Read the response from the server
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Format the medical records string
                String medicalInfoText = "No medical records found for this appointment.";
                if (!jsonResponse.isNull("medical_records")) {
                    JSONObject medicalRecords = jsonResponse.getJSONObject("medical_records");
                    StringBuilder medicalInfo = new StringBuilder();

                    medicalInfo.append("Diagnosis: ")
                            .append(medicalRecords.optString("diagnosis", "Not provided"))
                            .append("\n\n");

                    medicalInfo.append("Prescription: ")
                            .append(medicalRecords.optString("prescription", "Not provided"))
                            .append("\n\n");

                    medicalInfo.append("Attending Doctor: ")
                            .append(medicalRecords.optString("doctor", "Not provided"))
                            .append("\n\n");

                    medicalInfo.append("Notes: ")
                            .append(medicalRecords.optString("notes", "Not provided"));

                    medicalInfoText = medicalInfo.toString();
                }

                // Format the vital signs string
                String vitalInfoText = "No vital signs recorded for this appointment.";
                if (!jsonResponse.isNull("vital_signs")) {
                    JSONObject vitalSigns = jsonResponse.getJSONObject("vital_signs");
                    StringBuilder vitalInfo = new StringBuilder();

                    vitalInfo.append("Height: ")
                            .append(vitalSigns.optString("height_cm", "N/A"))
                            .append(" cm\n\n");

                    vitalInfo.append("Weight: ")
                            .append(vitalSigns.optString("weight_kg", "N/A"))
                            .append(" kg\n\n");

                    vitalInfo.append("Blood Pressure: ")
                            .append(vitalSigns.optString("blood_pressure", "N/A"))
                            .append("\n\n");

                    vitalInfo.append("Temperature: ")
                            .append(vitalSigns.optString("temperature_c", "N/A"))
                            .append(" °C\n\n");

                    vitalInfo.append("Attending Nurse: ")
                            .append(vitalSigns.optString("attending_nurse", "Not provided"))
                            .append("\n\n");

                    vitalInfo.append("Notes: ")
                            .append(vitalSigns.optString("notes", "Not provided"));

                    vitalInfoText = vitalInfo.toString();
                }

                // Store final strings to use in UI thread
                final String finalMedicalText = medicalInfoText;
                final String finalVitalText = vitalInfoText;

                // Update UI on the main thread
                runOnUiThread(() -> {
                    // Hide progress bar if you have one
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    // Update TextViews
                    tvMedicalInfo.setText(finalMedicalText);
                    tvVitalInfo.setText(finalVitalText);
                });

            } catch (Exception e) {
                // Handle error on the main thread
                runOnUiThread(() -> {
                    // Hide progress bar if you have one
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    // Show error messages
                    tvMedicalInfo.setText("Error loading medical records");
                    tvVitalInfo.setText("Error loading vital signs");

                    Toast.makeText(ViewHistorySingle.this,
                            "Error loading medical details: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("ViewHistorySingle", "Network error", e);
                });
            }
        }).start();
    }

}