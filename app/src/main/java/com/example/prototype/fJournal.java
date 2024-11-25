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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class fJournal extends Fragment {

    private RecyclerView recyclerView;
    private customAdapter adapter1;
    private List<content> contentList;
    private TextView txtviewHomeTitle;
    private ImageView chatImageView;
    public fJournal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_dashboard, container, false);
        txtviewHomeTitle = view.findViewById(R.id.txtviewHomeTitle);
        txtviewHomeTitle.setText("Journal");
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        chatImageView = view.findViewById(R.id.chat);

        // Set click listener for the chat image
        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });
        // Set up the content list
        contentList = new ArrayList<>();
        contentList.add(new content("Announcements"));
        contentList.add(new content("Appointments"));
        contentList.add(new content("Trivia"));

        // Set up the adapter and RecyclerView
        adapter1 = new customAdapter(contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter1);

        return view;
    }
}