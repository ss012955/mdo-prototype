package com.example.prototype;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class fDashboard extends Fragment {

    private RecyclerView recyclerView;
    private DashboardAdapter adapter;
    private List<DashboardContent> contentList;

    public fDashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_dashboard, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        List<String> images = Arrays.asList("chat", "umaklogo" , "home");
        List<String> appointments = Arrays.asList("Appointment 1", "Appointment 2", "Appointment 3");
        // Set up the content list
        contentList = new ArrayList<>();

        contentList.add(new DashboardContent("Announcements", images, null, null));
        contentList.add(new DashboardContent("Appointments", null, appointments, null));
        contentList.add(new DashboardContent("Trivia", null, null, "Did you know..."));

        // Set up the adapter and RecyclerView
        adapter = new DashboardAdapter(contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}