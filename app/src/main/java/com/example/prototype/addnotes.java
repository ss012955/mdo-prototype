package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import HelperClasses.NotesDatabaseHelper;
import HelperClasses.UpdateDeleteNotes;

public class addnotes extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    private SharedPreferences prefs;
    String userEmail;

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
        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        EditText titleInput = findViewById(R.id.journal_title);
        EditText symptomsInput = findViewById(R.id.input_symptoms);
        EditText moodInput = findViewById(R.id.input_mood);
        EditText medicineInput = findViewById(R.id.input_medicine);
        Button saveButton = findViewById(R.id.btnSave);
        Button deleteButton = findViewById(R.id.btnDelete);

        Intent updatedelete = getIntent();
        String noteId = updatedelete.getStringExtra("noteId");
        String titleIntent = updatedelete.getStringExtra("title");
        String symptomsIntent =updatedelete.getStringExtra("symptoms");
        String moodIntent = updatedelete.getStringExtra("mood");
        String medicineIntent = updatedelete.getStringExtra("medicine");

        if(noteId != null){
            titleInput.setText(titleIntent);
            symptomsInput.setText(symptomsIntent);
            moodInput.setText(moodIntent);
            medicineInput.setText(medicineIntent);
            deleteButton.setVisibility(View.VISIBLE);
        }

        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper();
        UpdateDeleteNotes updateDeleteNotes = new UpdateDeleteNotes();

        saveButton.setOnClickListener(view -> {
            // Get user inputs
            String title = titleInput.getText().toString().trim();
            String symptoms = symptomsInput.getText().toString().trim();
            String mood = moodInput.getText().toString().trim();
            String medicine = medicineInput.getText().toString().trim();

            // Get current date and time in the Philippines
            String dateTime = LocalDateTime.now(ZoneId.of("Asia/Manila"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Validate title before saving
            if (!title.isEmpty()) {
                if (userEmail != null) {

                    if(noteId == null) {
                        // Insert the note
                        dbHelper.insertNote(userEmail, title, symptoms, mood, medicine, dateTime);
                        Toast.makeText(addnotes.this, "Note saved!", Toast.LENGTH_SHORT).show();
                        finish(); // Return to the previous screen
                    }else{
                        updateDeleteNotes.updateNote(noteId, userEmail, title, symptoms, mood, medicine);
                        finish();
                    }
                } else {
                    Toast.makeText(addnotes.this, "User email is not available!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(addnotes.this, "Title is required!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v->{
            updateDeleteNotes.deleteNote(noteId, userEmail);
            finish();
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