package com.example.prototype;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import Adapters.journalAdapter;
import Adapters.contentJournal;
import HelperClasses.HistoryItem;
import HelperClasses.Note;

public class fJournal extends Fragment {

    private RecyclerView recyclerView;
    private ImageView chatImageView;
    private List<contentJournal> contentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_journal, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        chatImageView = view.findViewById(R.id.chat);

        // Populate content list
        // Initialize content list
        contentList = new ArrayList<>();

        // Create sample Notes
        List<Note> sampleNotes = new ArrayList<>();
        sampleNotes.add(new Note("Sample Note 1", "This is the content of the first note."));
        sampleNotes.add(new Note("Sample Note 2", "This is the content of the second note."));
        sampleNotes.add(new Note("Sample Note 3", "This is the content of the third note."));

        // Create sample History items
        List<HistoryItem> sampleHistory = new ArrayList<>();
        sampleHistory.add(new HistoryItem("History Entry 1", "Details of the first history entry."));
        sampleHistory.add(new HistoryItem("History Entry 2", "Details of the second history entry."));
        sampleHistory.add(new HistoryItem("History Entry 3", "Details of the third history entry."));

        // Add sample content for Notes section
        contentList.add(new contentJournal("notes", "This section contains user notes.", "notes", sampleNotes, null));

        // Add sample content for History section
        contentList.add(new contentJournal("history", "This section contains user history.", "history", null, sampleHistory));

        // Set up RecyclerView with journalAdapter
        journalAdapter adapter = new journalAdapter(getContext(), contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Chat button click listener
        chatImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });

        return view;
    }

}