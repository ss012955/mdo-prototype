package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class contactActivity extends BaseActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    public EditText contactNumber, address, guardianContact, guardianAddress;
    public Button buttonSave;
    String userEmail;
    boolean profileExists = false;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatImageView = findViewById(R.id.chat);
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
                startActivity(new Intent(contactActivity.this, home.class)
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

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "No email found");

        contactNumber = findViewById(R.id.etContact);
        address = findViewById(R.id.etAddress);
        guardianContact = findViewById(R.id.etGuardianContact);
        guardianAddress = findViewById(R.id.etGuardianAddress);
        buttonSave = findViewById(R.id.btnSave);
        fetchUserProfile(userEmail);


// Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        buttonSave.setOnClickListener(v -> {
            // Get the input data from the text fields

            String contactNumberText = contactNumber.getText().toString().trim();
            String addressText = address.getText().toString().trim();
            String guardianContactText = guardianContact.getText().toString().trim();
            String guardianAddressText = guardianAddress.getText().toString().trim();


            // Send the request using Volley
            sendProfileDataWithVolley(contactNumberText, addressText, guardianContactText, guardianAddressText);
        });


    }
    private void sendProfileDataWithVolley(String contactNumberText, String addressText, String guardianContactText, String guardianAddressText) {
        // Define the URL of your PHP script
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_userprofiledetails.php";  // Replace with your actual URL

        // Create the POST data string
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Assuming your response is a JSON string, let's parse it
                            JSONObject jsonResponse = new JSONObject(response);

                            // Handle the response from the server
                            if (jsonResponse.getBoolean("success")) {
                                // Operation was successful (insert or update)
                                Toast.makeText(contactActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                profileExists = true; // Update profileExists flag if needed
                            } else {
                                // Show error message
                                Toast.makeText(contactActivity.this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(contactActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors from the request (e.g., network issues)
                        Toast.makeText(contactActivity.this, "Request error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Create the data to send as a form
                Map<String, String> params = new HashMap<>();
                params.put("umak_email", userEmail); // Get the email from shared preferences
                params.put("contact_number", contactNumberText);
                params.put("address", addressText);
                params.put("guardian_contact_number", guardianContactText);
                params.put("guardian_address", guardianAddressText);
                params.put("operation", profileExists ? "update" : "insert");
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }

    private void fetchUserProfile(String userEmail) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String urlString = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_userprofile.php";  // Replace with your server URL
        urlString += "?umak_email=" + userEmail;  // Adding the email as a GET parameter

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");  // Change to GET method
            connection.setDoInput(true);
            connection.connect();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int charRead;
            while ((charRead = reader.read()) != -1) {
                response.append((char) charRead);
            }

            // Check if the response is a valid JSON
            try {
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getBoolean("exists")) {
                    profileExists = true; // Profile exists
                    contactNumber.setText(jsonResponse.getString("contact_number"));
                    address.setText(jsonResponse.getString("address"));
                    guardianContact.setText(jsonResponse.getString("guardian_contact_number"));
                    guardianAddress.setText(jsonResponse.getString("guardian_address"));
                } else {
                    profileExists = false; // Profile doesn't exist
                    //Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                // If it's not a JSON, handle the response as a plain text string
                String errorMessage = response.toString();
                Toast.makeText(this, "Error fetching profile: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        fetchUserProfile(userEmail);

    }
}