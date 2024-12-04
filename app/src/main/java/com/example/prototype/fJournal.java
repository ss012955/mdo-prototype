package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.DashboardAdapter;
import Adapters.journalAdapter;
import Adapters.contentJournal;
import HelperClasses.HistoryItem;
import HelperClasses.HistoryManager;
import HelperClasses.ItemClickListener;
import HelperClasses.Note;
import HelperClasses.NotesDatabaseHelper;

public class fJournal extends Fragment implements ItemClickListener {

    private RecyclerView recyclerView;
    private ImageView chatImageView;
    private List<contentJournal> contentList;
    private journalAdapter adapter;
    private SharedPreferences prefs;
    private String userEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_journal, container, false);

        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        Toast.makeText(requireContext(), userEmail, Toast.LENGTH_LONG).show();

        recyclerView = view.findViewById(R.id.recyclerView);
        chatImageView = view.findViewById(R.id.chat);

        // Populate content list
        // Initialize content list
        contentList = new ArrayList<>();

        // Create sample Notes
        loadLatestNotes();
        // Create sample History items
        List<HistoryItem> sampleHistory = new ArrayList<>();


        // Add sample content for History section
        contentList.add(new contentJournal("history", "This section contains user history.", "history", null, sampleHistory));

        // Set up RecyclerView with journalAdapter
        adapter = new journalAdapter(getContext(), contentList, userEmail, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Chat button click listener
        chatImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });

        return view;

    }
    private void loadLatestNotes() {
        // Fetch the latest 3 notes from your database (You can modify the query to get the most recent notes)
        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(getContext());
        List<Note> latestNotes = dbHelper.getLatestNotes(3); // Fetch the latest 3 notes

        // Add the notes section to contentList
        List<Note> notesList = new ArrayList<>(latestNotes);
        contentList.add(new contentJournal("notes", "This section contains user notes.", "notes", notesList, null));
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
        put("notes", Notes.class);
        put("history", History.class);
    }};
    public journalAdapter getAdapter() {
        return adapter;
    }

}