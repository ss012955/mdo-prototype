package com.example.prototype;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import HelperClasses.ItemClickListener;

public class Trivia extends BaseActivity implements ItemClickListener {
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
        recyclerView = findViewById(R.id.recyclerViewTrivia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        triviaAdapter = new TriviaAdapter(triviaList);
        recyclerView.setAdapter(triviaAdapter);

        chatImageView = findViewById(R.id.chat);

        // Set click listener for the chat image
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch trivia data from the server
        fetchTrivia();
    }

    private void fetchTrivia() {
        String url = "http://192.168.100.4/MDOapp-main/Admin/trivia.php?type=all";  // Your server URL

        // Make a GET request to fetch trivia data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                // Log the raw response to see if it's as expected
                Log.d("Trivia Response", response);

                // Parse the JSON array from the response
                JSONArray jsonArray = new JSONArray(response);
                triviaList.clear();  // Clear previous trivia data

                // Loop through the JSON array to extract each trivia item
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject triviaObject = jsonArray.getJSONObject(i);

                    // Get the title and details from each trivia item
                    String title = triviaObject.getString("title");
                    String text = triviaObject.getString("details");

                    // Add the trivia item to your list
                    triviaList.add(0,new TriviaItem(title, "\t"+ text));
                }

                // Notify the adapter to update the UI with the new data
                triviaAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing trivia data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            // Handle error in case of network failure or other issues
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Trivia Fetch Error", error.getMessage());
        });

        // Make the network request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v, int position) {

    }
}