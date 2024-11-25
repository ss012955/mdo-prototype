package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import HelperClasses.ItemClickListener;
import HelperClasses.NetworkChangeReceiver;

public class fDashboard extends Fragment implements ItemClickListener {

    private RecyclerView recyclerView;
    private DashboardAdapter adapter;
    private List<DashboardContent> contentList;
    private ImageView chatImageView;
    private NetworkChangeReceiver networkChangeReceiver;

    public fDashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_dashboard, container, false);
        chatImageView = view.findViewById(R.id.chat);

        // Set click listener for the chat image
        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });
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
        adapter.setClickListener(this);

        return view;

    }


    @Override
    public void onClick(View v, int position) {
        if (position >= 0 && position < contentList.size()) {
            String clickedItem = contentList.get(position).getType();

            // Map the type to the corresponding activity
            Class<?> activityClass = activityMap.get(clickedItem);

            if (activityClass != null) {
                Intent intent = new Intent(getContext(), activityClass);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Action not defined for this item", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Invalid item selected", Toast.LENGTH_SHORT).show();
        }
    }
    private final Map<String, Class<?>> activityMap = new HashMap<String, Class<?>>() {{
        put("Appointments", Appointments.class);
        put("Announcements", Announcements.class);
        put("Trivia", Trivia.class);
    }};

}