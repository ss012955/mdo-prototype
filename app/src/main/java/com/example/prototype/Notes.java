package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Adapters.contentJournal;
import Adapters.historyAdapter;
import Adapters.notesAdapter;
import HelperClasses.ItemClickListener;
import HelperClasses.Note;
import HelperClasses.NotesDatabaseHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Notes extends BaseActivity implements ItemClickListener {
    private ImageView chatImageView;
    private notesAdapter notesAdapter;
    private RecyclerView recyclerView;
    TabLayout tabLayout;
    private SharedPreferences prefs;
    String userEmail;
    String noteId, title, symptoms, mood, medicine;
    List<Note> notesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);


        FloatingActionButton addNoteButton = findViewById(R.id.add_note_button);

        addNoteButton.setOnClickListener(view -> {
            Intent intent = new Intent(Notes.this, addnotes.class);
            startActivity(intent);
        });

        loadNotes();


        recyclerView = findViewById(R.id.recyclerviewNotes);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);;
        notesList = new ArrayList<>();
        notesAdapter = new notesAdapter(notesList);
        recyclerView.setAdapter(notesAdapter);
        notesAdapter.setClickListener(this);


        SearchView searchView = findViewById(R.id.search_notes);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Ensure the adapter is initialized before calling getFilter
                if (notesAdapter != null) {
                    Log.e("Search", "Text changed: " + newText);
                    notesAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        chatImageView = findViewById(R.id.chat);
        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }
        tabLayout.selectTab(null);
        // Reset the tab icons to their unselected state
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(icons[i]); // Reset to the original icon (unselected)
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(Notes.this, home.class)
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
        loadNotes();
    }

    private void loadNotes() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_notes.php"; // URL to your PHP script
        String fullUrl = url + "?umak_email=" + userEmail;

        // Start a background thread to handle the network operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create the URL object
                    URL urlObj = new URL(fullUrl);

                    // Set up the connection
                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // Timeout 5 seconds
                    connection.setReadTimeout(5000); // Timeout 5 seconds

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    // Parse the response JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        JSONArray notesArray = jsonResponse.getJSONArray("notes");
                        notesList.clear(); // Clear the existing list before adding new notes

                        // Loop through the notes array and create Note objects
                        for (int i = 0; i < notesArray.length(); i++) {
                            JSONObject noteObj = notesArray.getJSONObject(i);
                            noteId = noteObj.getString("note_id");
                            title = noteObj.getString("title");
                            symptoms = noteObj.getString("symptoms");
                            mood = noteObj.getString("mood");
                            medicine = noteObj.getString("medicine");
                            String createdAt = noteObj.getString("created_at");

                            // Create Note object and add it to the list
                            Note note = new Note(noteId, title,createdAt, symptoms, mood, medicine);
                            notesList.add(note);
                        }

                        // Update the RecyclerView on the main thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                notesAdapter.notifyDataSetChanged();
                                notesAdapter.updateNotesList(notesList);// Refresh the adapter
                            }
                        });
                    } else {
                        // Handle error or empty notes list if success is false
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Notes.this, "No notes found!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Notes.this, "failed", Toast.LENGTH_LONG).show();// Update the adapter with new data
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v, int position) {
        if (notesList != null && notesList.size() > position) {
            Note clickedNote = notesList.get(position);

            Intent updatedelete = new Intent(Notes.this, addnotes.class);
            updatedelete.putExtra("noteId", clickedNote.getId());
            updatedelete.putExtra("title", clickedNote.getTitle());
            updatedelete.putExtra("symptoms", clickedNote.getSymptoms());
            updatedelete.putExtra("mood", clickedNote.getMood());
            updatedelete.putExtra("medicine", clickedNote.getMedicine());

            // Start the addnotes activity
            startActivity(updatedelete);

        } else {
            Toast.makeText(Notes.this, "Invalid item clicked." , Toast.LENGTH_SHORT).show();
        }

    }
}