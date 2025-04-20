package com.example.prototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import HelperClasses.DashboardManager;
import HelperClasses.ProfileClass;
import HelperClasses.ProfileDatabaseHelper;

public class fProfile extends Fragment implements Profile_CustomAdapter.OnEditButtonClickListener {

    private RecyclerView profile_recycler;
    private Profile_CustomAdapter profileCustomAdapter;
    private List<ProfileClass> profileList;
    private SharedPreferences prefs;
    private String userEmail;
    private ProfileDatabaseHelper profileDatabaseHelper;
    public home dashboard;
    public TextView emergency_details, settings, send_feedback, logout, medical_details;
    public DashboardManager dashboardManager;
    public Intent intent;
    private ImageView chatImageView;
    private boolean isImageClickable = false;  // Flag to control image clickability
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int selectedPosition = 0; // Add this at the class level


    public fProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_profile, container, false);

        // Initialize RecyclerView
        profile_recycler = view.findViewById(R.id.profile_recyclerview);
        emergency_details = view.findViewById(R.id.tvEmergencyContactDetails);
        medical_details = view.findViewById(R.id.tvMedicalInfo);
        settings = view.findViewById(R.id.tvSettings);
        send_feedback = view.findViewById(R.id.tvSendFeedback);
        logout = view.findViewById(R.id.tvLogOut);
        chatImageView = view.findViewById(R.id.chat);
        dashboardManager = new DashboardManager();

        logout.setOnClickListener(v->{
            dashboardManager.showLogoutValidator(getActivity(), fProfile.this);
        });

        settings.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        emergency_details.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), contactActivity.class);
            startActivity(intent);
        });
        medical_details.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), MedInfoActivity.class);
            startActivity(intent);
        });

        send_feedback.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), feedbackActivity.class);
            startActivity(intent);
        });


        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });

        profile_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        profileList = new ArrayList<>();
        profileList.add(new ProfileClass(R.drawable.profile, "Loading...", "Loading...", "Loading..."));

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                // Handle the result here (e.g., update the image in the adapter)
                if (profileCustomAdapter != null) {
                    profileCustomAdapter.updateImageAtPosition(imageUri, selectedPosition);
                }
            }
        });

        profileCustomAdapter = new Profile_CustomAdapter(profileList, fProfile.this, this, imagePickerLauncher);
        profile_recycler.setAdapter(profileCustomAdapter);


        prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);


        profileDatabaseHelper = new ProfileDatabaseHelper(getContext());

        // Fetch user details from the server
        profileDatabaseHelper.getUserDetails(userEmail, new ProfileDatabaseHelper.UserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(ProfileDatabaseHelper.User user) {
                if (user != null) {
                    // Populate the profile list with fetched user details
                    profileList.clear();
                    profileList.add(new ProfileClass(R.drawable.profile, user.getStudentId(), user.getEmail(), user.getFirstName() + " " + user.getLastName()));

                    // Update the RecyclerView with the new data
                    profileCustomAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;



    }

    // Handle the click of the edit button
    @Override
    public void onEditButtonClick(int position) {
        ProfileClass profile = profileList.get(position);

        // Fetch the email from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");

        // Create an Intent to start UpdateProfile Activity
        Intent intent = new Intent(getContext(), UpdateProfile.class);
        intent.putExtra("user_email", userEmail); // Send the email as extra data
        startActivity(intent);

        // Close the fragment
        getParentFragmentManager().popBackStack();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

}
