package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import HelperClasses.ProfileClass;
import HelperClasses.ProfileDatabaseHelper;
import HelperClasses.UpdateProfileManager;

public class UpdateProfile extends AppCompatActivity implements Profile_CustomAdapter.OnEditButtonClickListener {
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

        // Show Toast using Lambda
        Runnable showToast = () -> {
            String message = (userEmail != null && !userEmail.isEmpty()) ? "User Email: " + userEmail : "No email found";
            Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_SHORT).show();
        };
        showToast.run(); // Display the toast message

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

        // Set the adapter with the default data
        profileCustomAdapter = new Profile_CustomAdapter(profileList, UpdateProfile.this);
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
        });
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


}