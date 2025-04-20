package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
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


public class MedInfoActivity extends BaseActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    public EditText sexEditText, bloodType, allergies, medicalConditions, medications;
    public Button buttonSave;
    String userEmail;
    boolean profileExists = false;
    RequestQueue requestQueue;
    Spinner sexSpinner, blood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_med_info);
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
                startActivity(new Intent(MedInfoActivity.this, home.class)
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

        sexSpinner = findViewById(R.id.spinnerSex);
        sexEditText = findViewById(R.id.etSex);
        ArrayAdapter<String> adapterSex = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Male", "Female", "Not Specify"});
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(adapterSex);
        // Listener to detect selection
        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = parent.getItemAtPosition(position).toString();
                sexEditText.setText(selectedGender);  // Set it to the EditText
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sexEditText.setText("");  // Optional: clear if nothing is selected
            }
        });

        blood = findViewById(R.id.spinnerBlood);
        bloodType = findViewById(R.id.etBlood);

        ArrayAdapter<String> adapterBlood = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        adapterBlood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blood.setAdapter(adapterBlood);
        // Listener to detect selection
        blood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBloodType = parent.getItemAtPosition(position).toString();
                bloodType.setText(selectedBloodType);  // Set it to the EditText
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bloodType.setText("");  // Optional: clear if nothing is selected
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        allergies = findViewById(R.id.etAllergies);
        medicalConditions = findViewById(R.id.etMedicalConditions);
        medications = findViewById(R.id.etMedications);
        buttonSave = findViewById(R.id.btnSave);
        fetchMedicalInfo(userEmail);


        buttonSave.setOnClickListener(v -> sendMedicalInfo());


    }

    private void fetchMedicalInfo(String email) {
        // Update the parameter name to match what the PHP script expects
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_medicalinfo.php?umak_email=" + email;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("exists")) {  // Changed from "success" to "exists"
                            // Updated to match the keys from your PHP response
                            sexEditText.setText(jsonObject.getString("sex"));
                            bloodType.setText(jsonObject.getString("blood_type"));  // Changed from "bloodType"
                            allergies.setText(jsonObject.getString("allergies"));
                            medicalConditions.setText(jsonObject.getString("medical_conditions"));  // Changed from "medicalConditions"
                            medications.setText(jsonObject.getString("medications"));
                            profileExists = true;

                            // Set spinners to match the fetched values
                            setSpinnerToValue(sexSpinner, jsonObject.getString("sex"));
                            setSpinnerToValue(blood, jsonObject.getString("blood_type"));

                            Toast.makeText(this, "Medical info loaded successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            profileExists = false;
                            String message = jsonObject.optString("message", "No medical info found.");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Network error";
                    Toast.makeText(this, "Failed to fetch medical info: " + errorMessage, Toast.LENGTH_SHORT).show();
                });

        // Add a timeout for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,  // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    // Helper method to set spinner to specific value
    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equals(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private void sendMedicalInfo() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_medicalinfo.php";

        String sexValue = sexEditText.getText().toString().trim();
        String bloodValue = bloodType.getText().toString().trim();
        String allergiesValue = allergies.getText().toString().trim();
        String conditionsValue = medicalConditions.getText().toString().trim();
        String medicationsValue = medications.getText().toString().trim();

        if (sexValue.isEmpty() || bloodValue.isEmpty()) {
            Toast.makeText(this, "Sex and blood type are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a loading indicator
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving medical information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.optString("message", "Operation completed.");

                        if (success) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            profileExists = true;
                        } else {
                            Toast.makeText(this, "Save failed: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Network error";
                    Toast.makeText(this, "Failed to save medical info: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("umak_email", userEmail);  // Changed from "email" to "umak_email"
                params.put("sex", sexValue);
                params.put("blood_type", bloodValue);  // Changed from "bloodType"
                params.put("allergies", allergiesValue);
                params.put("medical_conditions", conditionsValue);  // Changed from "medicalConditions"
                params.put("medications", medicationsValue);

                // Add operation parameter based on whether the profile exists
                params.put("operation", profileExists ? "update" : "insert");

                return params;
            }
        };

        // Add timeout for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,  // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}