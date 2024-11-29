package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import Adapters.ChatAdapter;
import Singleton.Message;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity {
    TabLayout tabLayout;
    ImageButton buttonSendMessage;
    private DatabaseReference messagesDatabase;
    EditText inputMessage;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    List<Message> messages = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");
        String token = prefs.getString("id_token", null);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        messagesDatabase = FirebaseDatabase.getInstance().getReference("chats");

        initializeFirebase();
        initializeViews();
        setupTabLayout();
        setupRecyclerView();
        fetchMessages();
        setupSendMessage();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        messagesDatabase = FirebaseDatabase.getInstance().getReference("chats");
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tablayout);
        buttonSendMessage = findViewById(R.id.btn_send_message);
        inputMessage = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recycler_view_messages);
    }

    private void setupTabLayout() {
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int icon : icons) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icon));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(ChatActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.setMessages(messages);
    }

    private void fetchMessages() {
        messagesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                        chatAdapter = new ChatAdapter(messages);
                        recyclerView.setAdapter(chatAdapter);

                    }
                }
                chatAdapter.setMessages(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                Toast.makeText(ChatActivity.this, "Error fetching messages." + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSendMessage() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "Unknown User");
        String token = prefs.getString("id_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication token is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        buttonSendMessage.setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String messageId = messagesDatabase.push().getKey();
                if (messageId == null) {
                    Toast.makeText(this, "Error creating message ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Message newMessage = new Message(messageText, userEmail, System.currentTimeMillis());
                messagesDatabase.child(messageId).setValue(newMessage)
                        .addOnSuccessListener(aVoid -> {
                            inputMessage.setText("");
                            Log.e("ChatActivity", "Message sent successfully.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ChatActivity", "Error sending message: " + e.getMessage());
                            Toast.makeText(this, "Failed to send message." + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}