package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import Adapters.ChatAdapter;
import Singleton.Message;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class ChatActivity extends BaseActivity {
    TabLayout tabLayout;
    ImageButton buttonSendMessage;
    EditText inputMessage;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    List<Message> messages = new ArrayList<>();
    private static final String ADMIN_EMAIL = "admin2@example.com";
    String userEmail;
    Button viewService, viewFaqs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            boolean isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            if (isKeyboardVisible) {
                tabLayout.setVisibility(View.GONE);
            } else {
                tabLayout.setVisibility(View.VISIBLE);
            }
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "No email found");

        View messageInputArea = findViewById(R.id.message_input_area);
        ViewCompat.setOnApplyWindowInsetsListener(messageInputArea, (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = imeInsets.bottom - 40;

            // Only apply bottom padding when keyboard is visible
            if (bottomPadding > 0) {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomPadding);
            } else {
                // Reset padding when keyboard is hidden
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), 0);
            }

            return WindowInsetsCompat.CONSUMED;
        });

        viewService = findViewById(R.id.btn_view_services);
        viewService.setOnClickListener(v -> {

            Intent chatIntent = new Intent(this, viewServicesActivity.class);
            startActivity(chatIntent);
        });

        viewFaqs = findViewById(R.id.btn_faq);
        viewFaqs.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, viewFAQsActivity.class);
            startActivity(chatIntent);
        });

        tabLayout = findViewById(R.id.tablayout);
        buttonSendMessage = findViewById(R.id.btn_send_message);
        inputMessage = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recycler_view_messages);


        setupTabLayout();
        setupSendMessage();

        fetchMessagesFromDatabase();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();

        fetchMessagesFromDatabase();
    }

    private void fetchMessagesFromDatabase() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/get_messages_for_user.php?user_email=" + userEmail;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Create a new JSONObject from the response
                        JSONArray messagesArray = new JSONArray(response);

                        // Clear previous messages before adding new ones
                        messages.clear();

                        // Create a SimpleDateFormat to parse the timestamp string
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        // Loop through each message in the response
                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObject = messagesArray.getJSONObject(i);

                            // Extract the data from the JSON object
                            String messageText = messageObject.getString("message");
                            String senderEmail = messageObject.getString("sender_email");
                            String receiverEmail = messageObject.getString("receiver_email");
                            String timestampString = messageObject.getString("timestamp");

                            Calendar calendar = Calendar.getInstance();
                            TimeZone manilaTimeZone = TimeZone.getTimeZone("Asia/Manila");
                            calendar.setTimeZone(manilaTimeZone);

                            // Get the timestamp in long format (milliseconds since epoch)
                            long timestamp = calendar.getTimeInMillis();

                            // Create a new Message object and add it to the list
                            Message newMessage = new Message(messageText, senderEmail, receiverEmail, timestamp);
                            messages.add(newMessage);
                        }

                        // Notify the adapter about the new data
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1); // Scroll to the last message

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ChatActivity.this, "Error parsing messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    // Optionally, clear the input field here if needed
                    inputMessage.setText("");
                },
                error -> {
                    Toast.makeText(ChatActivity.this, "Error fetching messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }


    private List<Message> parseMessages(String response) {
        List<Message> parsedMessages = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject messageObj = jsonArray.getJSONObject(i);
                String senderEmail = messageObj.getString("sender_email");
                String receiverEmail = messageObj.getString("receiver_email");
                String messageText = messageObj.getString("message_text");
                String timestampString = messageObj.getString("timestamp");

                // Convert the timestamp from String to long
                long timestamp = Long.parseLong(timestampString);  // Parse string to long

                // Create Message object and add to the list
                Message message = new Message(messageText, senderEmail, receiverEmail, timestamp);
                parsedMessages.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsedMessages;
    }



    private void setupTabLayout() {
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int icon : icons) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icon));
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
                startActivity(new Intent(ChatActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void setupSendMessage() {
        buttonSendMessage.setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();

            if (messageText.isEmpty()) {
                Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send message to the admin
            sendMessageToAdmin(messageText);
        });
    }

    private void sendMessageToAdmin(String messageText) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/send_message.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if ("Message sent successfully.".equals(response)) {
                        Calendar calendar = Calendar.getInstance();
                        TimeZone manilaTimeZone = TimeZone.getTimeZone("Asia/Manila");
                        calendar.setTimeZone(manilaTimeZone);

                        // Get the timestamp in long format (milliseconds since epoch)
                        long timestamp = calendar.getTimeInMillis();

                        // Create new message with Manila timestamp in long format
                        Message newMessage = new Message(messageText, userEmail, ADMIN_EMAIL, timestamp);


                        messages.add(newMessage);
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        recyclerView.scrollToPosition(messages.size() - 1);
                        inputMessage.setText(""); // Clear input here
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ChatActivity.this, "Error sending message: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_email", userEmail);
                params.put("receiver_email", ADMIN_EMAIL);
                params.put("message_text", messageText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

}