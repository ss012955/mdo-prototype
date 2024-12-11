package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import HelperClasses.ProfileClass;
import HelperClasses.ProfileDatabaseHelper;
import HelperClasses.UpdateProfileManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class UpdateProfile extends BaseActivity implements Profile_CustomAdapter.OnEditButtonClickListener {
    private SharedPreferences prefs;
    private String userEmail;
    private RecyclerView profile_recycler;
    private Profile_CustomAdapter profileCustomAdapter;
    private List<ProfileClass> profileList;
    private ProfileDatabaseHelper profileDatabaseHelper;
    String firstname, lastname;
    EditText etFirstname, etLastname, etPassword, etConfirmPassword;
    ImageView eyePassword, eyeConfirmPassword;
    Button buttonSave;
    UpdateProfileManager updateProfileManager;
    TabLayout tabLayout;
    private int selectedPosition = 0; // Add this at the class level
    boolean profileExists = false;
    private boolean isImageClickable = true;  // Flag to control image clickability


    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize userEmail
        userEmail = getIntent().getStringExtra("user_email");
        fetchUserProfile(userEmail);

        // Show Toast using Lambda
        Runnable showToast = () -> {
            String message = (userEmail != null && !userEmail.isEmpty()) ? "User Email: " + userEmail : "No email found";
            Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_SHORT).show();
        };
        showToast.run(); // Display the toast message
        tabLayout = findViewById(R.id.tablayout);

        // Initialize RecyclerView
        profile_recycler = findViewById(R.id.profile_recyclerview);
        profile_recycler.setLayoutManager(new LinearLayoutManager(this));
        etFirstname = findViewById(R.id.etFirstName);
        etLastname = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        eyePassword= findViewById(R.id.eyePassword);
        eyeConfirmPassword= findViewById(R.id.eyeConfirmPassword);
        eyePassword.setOnClickListener(v -> {togglePasswordVisibility(etPassword, eyePassword);});
        eyeConfirmPassword.setOnClickListener(v -> { togglePasswordVisibility(etConfirmPassword, eyeConfirmPassword);});


        // Set up profile list and adapter
        profileList = new ArrayList<>();
        profileList.add(new ProfileClass(R.drawable.profile, "Loading...", "Loading...", "Loading..."));

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Use the adapter method to update the image
                            profileCustomAdapter.updateImageAtPosition(selectedImageUri, selectedPosition);
                        }
                    }
                }
        );

        // Set the adapter with the default data
        profileCustomAdapter = new Profile_CustomAdapter(profileList, UpdateProfile.this, this, imagePickerLauncher,isImageClickable);
        profile_recycler.setAdapter(profileCustomAdapter);

        // Initialize database helper
        profileDatabaseHelper = new ProfileDatabaseHelper(UpdateProfile.this);

        // Fetch user details from the database
        profileDatabaseHelper.getUserDetails(userEmail, new ProfileDatabaseHelper.UserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(ProfileDatabaseHelper.User user) {
                if (user != null) {
                    // Populate the profile list with fetched user details
                    profileList.clear();
                    profileList.add(new ProfileClass(R.drawable.profile, user.getStudentId(), user.getEmail(), user.getFirstName() + " " + user.getLastName()));
                    firstname = user.getFirstName();
                    lastname = user.getLastName();
                    etFirstname.setHint(firstname);
                    etLastname .setHint(lastname);

                    // Update the RecyclerView with the new data
                    profileCustomAdapter.notifyDataSetChanged();
                } else {
                    // If no user is found, show a Toast message
                    Toast.makeText(UpdateProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateProfileManager = new UpdateProfileManager();

        buttonSave = findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(v->{
            updateProfileManager.showSaveValidator(this, this, userEmail,
                    etFirstname, etLastname,
                    etPassword, etConfirmPassword);

            String firstname = etFirstname.getText().toString();
            String lastname = etLastname.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // Ensure that password and confirm password match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(UpdateProfile.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the selected image URI and convert to Base64 (if image exists)
            Uri selectedImageUri = profileCustomAdapter.getSelectedImageUri();
            String imageBase64 = null;
            if (selectedImageUri != null) {
                imageBase64 = convertImageToBase64(selectedImageUri);
            }

            // Determine the operation type: insert or update
            String operation = profileExists ? "update" : "insert";

            // Call a method to handle the operation (insert or update)
            handleProfileOperation(operation, userEmail, imageBase64);
        });

        tabLayouter();

    }
    private String convertImageToBase64(Uri imageUri) {
        try {
            FileInputStream fileInputStream = (FileInputStream) getContentResolver().openInputStream(imageUri);
            byte[] byteArray = new byte[fileInputStream.available()];
            fileInputStream.read(byteArray);
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleProfileOperation(String operation, String userEmail, String imageBase64) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_userprofileImage.php";  // Replace with your actual API URL
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("ServerResponse", response); // Log the raw server response
                    try {
                        // Try parsing response as JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        // Handle the case when the response is a plain string
                        Log.e("JSONError", "Response is not JSON: " + e.getMessage());
                        // Assuming the plain string response is the success message
                        //Toast.makeText(UpdateProfile.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(UpdateProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("operation", operation);
                params.put("umak_email", userEmail);
                params.put("profile_image", imageBase64 != null ? imageBase64 : "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    // Assuming you have the image URI fetched, here's how you modify the fetchProfileImage call


    public void openFileChooser(int position) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    public void onEditButtonClick(int position) {

    }
    private void togglePasswordVisibility(EditText editText, ImageView eyeIcon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            //eyeIcon.setImageResource(R.drawable.ic_eye_open); // Set an open eye icon
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_eye); // Set a closed eye icon
        }
    }

    public void tabLayouter(){
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
                startActivity(new Intent(UpdateProfile.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            //Local uploading of il
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    //Local uploading of il
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