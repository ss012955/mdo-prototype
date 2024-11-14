package com.example.prototype;

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

public class fProfile extends Fragment implements Profile_CustomAdapter.OnEditButtonClickListener {

    private RecyclerView profile_recycler;
    private Profile_CustomAdapter profileCustomAdapter;
    private List<ProfileClass> profileList;

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

        // Sample data
        profileList = new ArrayList<>();
        profileList.add(new ProfileClass(R.drawable.profile, "Student ID 1", "email1@domain.com", "Name 1"));
        profileList.add(new ProfileClass(R.drawable.profile, "Student ID 2", "email2@domain.com", "Name 2"));

        // Initialize adapter with listener (fProfile implements OnEditButtonClickListener)
        profileCustomAdapter = new Profile_CustomAdapter(profileList, this);
        profile_recycler.setAdapter(profileCustomAdapter);

        return view;
    }

    // Handle the click of the edit button
    @Override
    public void onEditButtonClick(int position) {
        ProfileClass profile = profileList.get(position);
        Toast.makeText(getContext(), "Edit button clicked for: " + profile.getName(), Toast.LENGTH_SHORT).show();
        // Add logic here to edit profile
    }
}
