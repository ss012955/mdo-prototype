package com.example.prototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
        profile_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve the email from the fragment arguments
        if (getArguments() != null) {
            userEmail = getArguments().getString("user_email");

            // Show a Toast with the user email
            if (userEmail != null && !userEmail.isEmpty()) {
                Toast.makeText(getContext(), "User Email: " + userEmail, Toast.LENGTH_SHORT).show();
            }
        }

        profileDatabaseHelper = new ProfileDatabaseHelper(getContext());

        // Fetch user details from the server
        profileDatabaseHelper.getUserDetails(userEmail, new ProfileDatabaseHelper.UserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(ProfileDatabaseHelper.User user) {
                if (user != null) {
                    // Populate the profile list with fetched user details
                    profileList = new ArrayList<>();
                    profileList.add(new ProfileClass(R.drawable.profile, user.getStudentId(), user.getEmail(), user.getFirstName() + " " + user.getLastName()));

                    // Update the RecyclerView with the new data
                    profileCustomAdapter = new Profile_CustomAdapter(profileList, fProfile.this);
                    profile_recycler.setAdapter(profileCustomAdapter);
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
        Toast.makeText(getContext(), "Edit button clicked for: " + profile.getName(), Toast.LENGTH_SHORT).show();
        // Add logic here to edit profile
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

}
