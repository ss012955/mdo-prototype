package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Adapters.contentJournal;
import Adapters.historyAdapter;
import Adapters.notesAdapter;
import HelperClasses.Note;
import HelperClasses.NotesDatabaseHelper;

public class Notes extends AppCompatActivity {
    private ImageView chatImageView;
    private notesAdapter notesAdapter;
    private RecyclerView recyclerView;
    TabLayout tabLayout;
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
        FloatingActionButton addNoteButton = findViewById(R.id.add_note_button);

        addNoteButton.setOnClickListener(view -> {
            Intent intent = new Intent(Notes.this, addnotes.class);
            startActivity(intent);
        });
        recyclerView = findViewById(R.id.recyclerviewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Note> notesList = new ArrayList<>();
        notesAdapter = new notesAdapter(notesList);
        recyclerView.setAdapter(notesAdapter);
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
        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(this);
        Cursor cursor = dbHelper.getAllNotes();

        List<Note> notesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            notesList.add(new Note(
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date_time")),
                    cursor.getString(cursor.getColumnIndexOrThrow("symptoms")),
                    cursor.getString(cursor.getColumnIndexOrThrow("mood")),
                    cursor.getString(cursor.getColumnIndexOrThrow("medicine"))
            ));
        }
        cursor.close();




       notesAdapter.updateNotesList(notesList);
    }

}