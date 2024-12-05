package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class feedbackActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    private List<ImageView> stars;
    private int currentRating = 0;
    private EditText feedbackInput;
    private Button sendFeedbackButton;
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
        // Initialize views
        feedbackInput = findViewById(R.id.feedback_input);
        sendFeedbackButton = findViewById(R.id.send_feedback);
        String userEmail = "john.doe@umak.edu.ph";
        // Add click listener for the "Send Feedback" button
        sendFeedbackButton.setOnClickListener(v -> {
            String feedback = feedbackInput.getText().toString().trim();
            if (!feedback.isEmpty()) {
                fetchLatestCompletedBookingAndSendFeedback(feedback, currentRating, userEmail);
            } else {
                Toast.makeText(this, "Please write some feedback!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void fetchLatestCompletedBookingAndSendFeedback(String feedback, int rating, String userEmail) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching latest booking...");
        progressDialog.show();
        String url = "http://192.168.100.4/MDOapp-main/feedback/fetch_latest_booking.php"; // Update with your actual server URL

        // Update the request with POST method
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("user_email", userEmail); // Ensure you're sending the correct email
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        Log.d("BookingResponse", response);
                        JSONObject responseObject = new JSONObject(response);
                        if (responseObject.getBoolean("success")) {
                            JSONObject booking = responseObject.getJSONObject("booking");
                            int bookingId = booking.getInt("booking_id");
                            String serviceType = booking.getString("service_type");
                            boolean hasFeedback = booking.getBoolean("has_feedback");

                            // Enable or disable the feedback button

                            if (!hasFeedback) {
                                sendFeedbackToServer(feedback, rating, userEmail, bookingId, serviceType);
                            } else {
                                Toast.makeText(this, "You have already submitted feedback for this booking.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, responseObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing booking response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to fetch booking: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                return postData.toString().getBytes();

            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(stringRequest);
    }
    private void sendFeedbackToServer(String feedback, int rating, String userEmail, int bookingId, String serviceType) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting feedback...");
        progressDialog.show();
        String url = "http://192.168.100.4/MDOapp-main/feedback/submit_feedback.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Prepare JSON object for sending feedback data
        JSONObject postData = new JSONObject();
        try {
            postData.put("feedback", feedback);
            postData.put("rating", rating);
            postData.put("user_email", userEmail); // User's email
            postData.put("booking_id", bookingId); // Booking ID// Service related to feedback
            postData.put("service_type", serviceType); // Type of service provided
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create POST request with the feedback data
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean success = responseObject.getBoolean("success");
                        String message = responseObject.getString("message");
                        Toast.makeText(this, success + message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to submit feedback: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public byte[] getBody() {
                return postData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(stringRequest);
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