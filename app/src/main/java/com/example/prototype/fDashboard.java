package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import HelperClasses.AnnouncementManager;
import HelperClasses.AnnouncementsItems;
import HelperClasses.AppointmentsManager;
import HelperClasses.ItemClickListener;
import HelperClasses.NetworkChangeReceiver;
import Singleton.allAppointments;

public class fDashboard extends Fragment implements ItemClickListener {

    private RecyclerView recyclerView;
    private DashboardAdapter adapter;
    private List<DashboardContent> contentList;
    private SharedPreferences prefs;
    private ImageView chatImageView;

    public fDashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_dashboard, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        chatImageView = view.findViewById(R.id.chat); //
        List<String> images = Arrays.asList("chat", "umaklogo" , "home");
        List<String> appointments = Arrays.asList("Appointment 1", "Appointment 2", "Appointment 3");
        // Set up the content list
        contentList = new ArrayList<>();

        contentList.add(new DashboardContent("Announcements", images, null, null));
        contentList.add(new DashboardContent("Appointments", null, appointments, null));
        contentList.add(new DashboardContent("Trivia", null, null, "Did you know..."));

        // Set up the adapter and RecyclerView
        adapter = new DashboardAdapter(getContext(), contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });

        prefs = getActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");

        AppointmentsManager appointmentsManager = new AppointmentsManager();
        appointmentsManager.fetchAllAppointments(userEmail, new Runnable() {
            @Override
            public void run() {
                // Update UI when data is fetched
                int numberOfAppointments = allAppointments.getInstance().getNumberOfAppointments();
                //Toast.makeText(getContext(), userEmail + " " + numberOfAppointments, Toast.LENGTH_SHORT).show();

                // Notify the adapter about the updated data
                adapter.notifyDataSetChanged(); // Update the RecyclerView
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAndUpdateAnnouncements();
    }
    private void fetchAndUpdateAnnouncements() {
        AnnouncementManager.fetchAnnouncements(getContext(), new AnnouncementManager.AnnouncementsCallback() {
            @Override
            public void onSuccess(List<AnnouncementsItems> announcements) {
                if (!announcements.isEmpty()) {
                    List<DashboardContent> updatedContentList = new ArrayList<>(contentList);

                    // Update the first item (Announcements) in the contentList
                    DashboardContent announcementsContent = updatedContentList.get(0);
                    List<String> announcementImages = new ArrayList<>();
                    for (AnnouncementsItems item : announcements) {
                        announcementImages.add(item.getImageUrl());

                    }

                    announcementsContent.setAnnouncementTitle(announcements.get(0).getTitle());
                    announcementsContent.setImageUrl(announcements.get(0).getImageUrl());
                    announcementsContent.setAnnouncementDescrip(announcements.get(0).getText());
                    // Update adapter
                    adapter.updateContentList(updatedContentList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Failed to load announcements: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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