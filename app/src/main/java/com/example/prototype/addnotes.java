package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import HelperClasses.NotesDatabaseHelper;
import HelperClasses.UpdateDeleteNotes;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addnotes extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    private SharedPreferences prefs;
    String userEmail;
    TextView txtPrediction, txtAccuracy, txtRemarks;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        EditText titleInput = findViewById(R.id.journal_title);
        EditText symptomsInput = findViewById(R.id.input_symptoms);
        EditText moodInput = findViewById(R.id.input_mood);
        EditText medicineInput = findViewById(R.id.input_medicine);
        Button saveButton = findViewById(R.id.btnSave);
        Button deleteButton = findViewById(R.id.btnDelete);

        Intent updatedelete = getIntent();
        String noteId = updatedelete.getStringExtra("noteId");
        String titleIntent = updatedelete.getStringExtra("title");
        String symptomsIntent =updatedelete.getStringExtra("symptoms");
        String moodIntent = updatedelete.getStringExtra("mood");
        String medicineIntent = updatedelete.getStringExtra("medicine");

        if(noteId != null){
            titleInput.setText(titleIntent);
            symptomsInput.setText(symptomsIntent);
            moodInput.setText(moodIntent);
            medicineInput.setText(medicineIntent);
            deleteButton.setVisibility(View.VISIBLE);
        }

        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper();
        UpdateDeleteNotes updateDeleteNotes = new UpdateDeleteNotes();

        saveButton.setOnClickListener(view -> {
            // Get user inputs
            String title = titleInput.getText().toString().trim();
            String symptoms = symptomsInput.getText().toString().trim();
            String mood = moodInput.getText().toString().trim();
            String medicine = medicineInput.getText().toString().trim();

            // Get current date and time in the Philippines
            String dateTime = LocalDateTime.now(ZoneId.of("Asia/Manila"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Validate title before saving
            if (!title.isEmpty()) {
                if (userEmail != null) {

                    if(noteId == null) {
                        // Insert the note
                        dbHelper.insertNote(userEmail, title, symptoms, mood, medicine, dateTime);
                        Toast.makeText(addnotes.this, "Note saved!", Toast.LENGTH_SHORT).show();
                        finish(); // Return to the previous screen
                    }else{
                        updateDeleteNotes.updateNote(noteId, userEmail, title, symptoms, mood, medicine);
                        finish();
                    }
                } else {
                    Toast.makeText(addnotes.this, "User email is not available!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(addnotes.this, "Title is required!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v->{
            updateDeleteNotes.deleteNote(noteId, userEmail);
            finish();
        });



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
                startActivity(new Intent(addnotes.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        txtPrediction = findViewById(R.id.tvPrediction);
        txtAccuracy = findViewById(R.id.tvAccuracy);
        txtRemarks = findViewById(R.id.tvRemarks);

        String symptoms = symptomsInput.getText().toString().trim();
        if (!symptoms.isEmpty()) {
            predictIllness(symptoms);
        } else {
//            Toast.makeText(addnotes.this, "Please enter symptoms first", Toast.LENGTH_SHORT).show();
        }

    }

    private void predictIllness(String symptomsText) {
        try {
            // Show loading state
            txtPrediction.setText("Predicting...");
            txtAccuracy.setText("");
            txtRemarks.setText("Please wait");

            // Create JSON payload
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("symptoms", symptomsText);

            // Create request body
            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            // Build the request
            Request request = new Request.Builder()
                    .url("https://illness-sight-dcc82adf1a6f.herokuapp.com/predict")
                    .post(body)
                    .build();

            // Execute the request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        txtPrediction.setText("Error");
                        txtAccuracy.setText("");
                        txtRemarks.setText("Network error: " + e.getMessage());
                        Toast.makeText(addnotes.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> {
                            txtPrediction.setText("Error");
                            txtAccuracy.setText("");
                            txtRemarks.setText("Server error: " + response.code());
                            Toast.makeText(addnotes.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);

                        // Extract values from response
                        final String predictedDisease = jsonResponse.getString("predicted_disease");
                        final double confidenceScore = jsonResponse.getDouble("confidence_score");

                        // Format confidence score as percentage
                        final int confidencePercentage = (int) (confidenceScore * 100);

                        // Generate remarks based on confidence
                        final String remarks;
                        if (confidencePercentage >= 90) {
                            remarks = "High confidence prediction. Consider booking and appointment and consult a healthcare professional.";
                        } else if (confidencePercentage >= 70) {
                            remarks = "Moderate confidence prediction. Monitor symptoms and consult a doctor if they worsen.";
                        } else {
                            remarks = "Low confidence prediction. Please provide more symptoms for better accuracy.";
                        }

                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            txtPrediction.setText("Possible Illness: " + predictedDisease);
                            txtAccuracy.setText("Confidence: "+confidencePercentage + "%");
                            txtRemarks.setText(remarks);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            txtPrediction.setText("Error");
                            txtAccuracy.setText("");
                            txtRemarks.setText("Error parsing response");
                            Toast.makeText(addnotes.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            txtPrediction.setText("Error");
            txtAccuracy.setText("");
            txtRemarks.setText("Error creating request");
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}