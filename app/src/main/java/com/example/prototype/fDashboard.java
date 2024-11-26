package com.example.prototype;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String formattedText;
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
        contentList.add(new DashboardContent("Trivia", null, null, formattedText));

        // Set up the adapter and RecyclerView
        adapter = new DashboardAdapter(contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);
        createNotificationChannel();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTrivia();
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
    private void fetchTrivia() {
        String url = "http://192.168.100.4/MDOapp-main/Admin/trivia.php?type=latest"; // URL for latest trivia

        // Get saved trivia from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TriviaPrefs", Context.MODE_PRIVATE);
        String lastFetchedTrivia = sharedPreferences.getString("lastFetchedTrivia", "");

        // Make the GET request to fetch trivia data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                // Log the response for debugging
                Log.d("Trivia Response", response);

                // Parse the JSON response for the latest trivia
                JSONObject triviaObject = new JSONObject(response);
                String title = triviaObject.getString("title");
                String text = triviaObject.getString("details");

                // Combine title and details into the formatted text
                formattedText = "\t" + title + "\n" + "\t"+ text;

                // Check if the fetched trivia is different from the last fetched trivia
                // show the notif only once
                if (!formattedText.equals(lastFetchedTrivia)) {
                    // New trivia, show the notification
                    showNewTriviaNotification(title, text);

                    // Save the new trivia in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lastFetchedTrivia", formattedText); // Store the new trivia
                    editor.apply();
                }

                // Update the trivia section in the contentList
                for (DashboardContent content : contentList) {
                    if ("Trivia".equals(content.getType())) {
                        content.setTriviaTitle(formattedText); // Update the trivia data
                        break;
                    }
                }

                // Notify the adapter to update the trivia item
                adapter.notifyItemChanged(contentList.indexOf(contentList.stream()
                        .filter(c -> "Trivia".equals(c.getType()))
                        .findFirst()
                        .orElse(null)));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error parsing trivia data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            // Handle error in case of a network failure or other issues
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Trivia Fetch Error", error.getMessage());
        });

        // Make the network request
        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Trivia Channel";
            String description = "Channel for Trivia notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TRIVIA_CHANNEL", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNewTriviaNotification(String title, String text) {
        // Check and request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return; // Do not proceed until permission is granted
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "TRIVIA_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification) // Add your icon here
                .setContentTitle("New Trivia Added")
                .setContentText(title + "\n" + text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Dismiss the notification when clicked

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build()); // Unique ID for notification
    }
}