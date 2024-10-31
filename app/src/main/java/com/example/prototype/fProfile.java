package com.example.prototype;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class fProfile extends Fragment {

    private RecyclerView recyclerView;
    private customAdapter adapter1;
    private List<content> contentList;
    private TextView txtviewHomeTitle;
    public fProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_dashboard, container, false);
        txtviewHomeTitle = view.findViewById(R.id.txtviewHomeTitle);
        txtviewHomeTitle.setText("Profile");
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up the content list
        contentList = new ArrayList<>();
        contentList.add(new content("aaaa"));
        contentList.add(new content("Aaa"));
        contentList.add(new content("Taa"));

        // Set up the adapter and RecyclerView
        adapter1 = new customAdapter(contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter1);

        return view;
    }
}