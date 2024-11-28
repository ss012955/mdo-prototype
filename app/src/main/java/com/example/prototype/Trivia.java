package com.example.prototype;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.TriviaAdapter;
import HelperClasses.ItemClickListener;
import HelperClasses.TriviaItem;

public class Trivia extends BaseActivity implements ItemClickListener {
    private SharedPreferences prefs;
    String userEmail;
    private RecyclerView recyclerView;
    private TriviaAdapter triviaAdapter;
    private List<TriviaItem> triviaList = new ArrayList<>();
    TabLayout tabLayout;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trivia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        chatImageView = findViewById(R.id.chat);
        recyclerView = findViewById(R.id.recyclerViewTrivia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        triviaAdapter = new TriviaAdapter(this, triviaList);
        recyclerView.setAdapter(triviaAdapter);

        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(Trivia.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        createNotificationChannel();

    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchTrivia();
    }


    private void fetchTrivia() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/trivia.php?type=all";  // Your server URL

        SharedPreferences sharedPreferences = getSharedPreferences("TriviaPrefs", Context.MODE_PRIVATE);
        String lastFetchedTrivia = sharedPreferences.getString("lastFetchedTrivia", "");

        // Make a GET request to fetch trivia data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("Trivia Response", response);
                JSONArray jsonArray = new JSONArray(response);
                triviaList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject triviaObject = jsonArray.getJSONObject(i);
                    String title = triviaObject.getString("title");
                    String text = triviaObject.getString("details");
                    triviaList.add(0, new TriviaItem(title, text));

                    if (!jsonArray.equals(lastFetchedTrivia)) {
                        // New trivia, show the notification
                        showNewTriviaNotification(title, text);

                        // Save the new trivia in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastFetchedTrivia", response); // Store the new trivia
                        editor.apply();
                    }
                }
                triviaAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing trivia data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            // Use a safe fallback message
            String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            Log.d("Trivia Fetch Error", errorMessage);
        });
        // Make the network request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TRIVIA_CHANNEL",
                    "Trivia Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Trivia notifications");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNewTriviaNotification(String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                // Prompt the user for permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TRIVIA_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Trivia Added")
                .setContentText(title + "\n" + text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        runOnUiThread(() -> {
            NotificationManagerCompat.from(this).notify(1, builder.build());
        });

    }

    @Override
    public void onClick(View v, int position) {

    }
}