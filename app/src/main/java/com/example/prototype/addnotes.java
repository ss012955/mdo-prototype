package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import HelperClasses.NotesDatabaseHelper;

public class addnotes extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText titleInput = findViewById(R.id.journal_title);
        EditText dateTimeInput = findViewById(R.id.journal_date_time);
        EditText symptomsInput = findViewById(R.id.input_symptoms);
        EditText moodInput = findViewById(R.id.input_mood);
        EditText medicineInput = findViewById(R.id.input_medicine);
        Button saveButton = findViewById(R.id.btnSave);

        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(this);

        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString().trim();
            String dateTime = dateTimeInput.getText().toString().trim();
            String symptoms = symptomsInput.getText().toString().trim();
            String mood = moodInput.getText().toString().trim();
            String medicine = medicineInput.getText().toString().trim();

            if (!title.isEmpty()) {
                dbHelper.addNote(title, dateTime, symptoms, mood, medicine);
                Toast.makeText(addnotes.this, "Note saved!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the Notes screen
            } else {
                Toast.makeText(addnotes.this, "Title is required!", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(addnotes.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}