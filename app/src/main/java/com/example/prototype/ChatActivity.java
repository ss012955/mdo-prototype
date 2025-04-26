package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.ChatAdapter;
import Singleton.Message;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private static final String AGENT_EMAIL = "mdo.agent@system.com"; // You can use any email for the agent


    // Dialogflow integration components
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Initialize executor and handler for Dialogflow
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

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

        showPasswordGuidelinesDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMessagesFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service when activity is destroyed
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void fetchMessagesFromDatabase() {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/get_messages_for_user.php?user_email=" + userEmail;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Create a new JSONArray from the response
                        JSONArray messagesArray = new JSONArray(response);

                        // Clear previous messages before adding new ones
                        messages.clear();

                        // Create a SimpleDateFormat to parse the timestamp string
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Set Manila time zone

                        // Loop through each message in the response
                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObject = messagesArray.getJSONObject(i);

                            // Extract the data from the JSON object
                            String messageText = messageObject.getString("message");
                            String senderEmail = messageObject.getString("sender_email");
                            String receiverEmail = messageObject.getString("receiver_email");
                            String timestampString = messageObject.getString("timestamp");

                            // Convert timestamp string to long
                            long timestamp = 0;
                            try {
                                Date date = dateFormat.parse(timestampString);
                                if (date != null) {
                                    timestamp = date.getTime(); // Convert to Unix timestamp in milliseconds
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(ChatActivity.this, "Error parsing timestamp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

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

            // Check if the message starts with "agent" or "admin"
            if (messageText.toLowerCase().startsWith("agent")) {
                String dialogflowMessage = messageText.substring(5).trim(); // Remove "agent" prefix
                if (dialogflowMessage.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Message to agent cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the user message to the UI first
                addLocalMessage(messageText);

                // Process with Dialogflow
                sendToDialogflow(dialogflowMessage);

            } else if (messageText.toLowerCase().startsWith("admin")) {
                String adminMessage = messageText.substring(5).trim(); // Remove "admin" prefix
                if (adminMessage.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Message to admin cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the user message to the UI first
                addLocalMessage(messageText);

                // Send to admin
                sendMessageToAdmin(adminMessage);

            } else {
                // Default behavior - send to admin
                sendMessageToAdmin(messageText);
            }
        });
    }

    // Helper method to add message locally to UI
    private void addLocalMessage(String messageText) {
        // Get current timestamp
        Calendar calendar = Calendar.getInstance();
        TimeZone manilaTimeZone = TimeZone.getTimeZone("Asia/Manila");
        calendar.setTimeZone(manilaTimeZone);
        long timestamp = calendar.getTimeInMillis();

        // Add user message to UI
        Message userMessage = new Message(messageText, userEmail, ADMIN_EMAIL, timestamp);
        messages.add(userMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        inputMessage.setText(""); // Clear input field
    }

    private void sendMessageToAdmin(String messageText) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/send_message.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (!"Message sent successfully.".equals(response)) {
                        Toast.makeText(ChatActivity.this, "Failed to send message to admin", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ChatActivity.this, "Error sending message: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters to the POST request
                params.put("sender_email", userEmail);
                params.put("receiver_email", ADMIN_EMAIL);
                params.put("message_text", messageText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void sendToDialogflow(String message) {
        // Show loading indicator
        Calendar calendar = Calendar.getInstance();
        TimeZone manilaTimeZone = TimeZone.getTimeZone("Asia/Manila");
        calendar.setTimeZone(manilaTimeZone);
        long timestamp = calendar.getTimeInMillis();

        // Add "typing" message
        Message typingMessage = new Message("Typing...", AGENT_EMAIL, userEmail, timestamp);
        messages.add(typingMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        executor.execute(() -> {
            try {
                // Load credentials
                InputStream stream = getResources().openRawResource(R.raw.mdoagent);
                GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
                AccessToken token = credentials.refreshAccessToken();

                // Build JSON request
                JSONObject body = new JSONObject();
                body.put("queryInput", new JSONObject()
                        .put("text", new JSONObject()
                                .put("text", message)
                                .put("languageCode", "en")));

                // Build full URL
                String projectId = "mdoagent-yrqi"; // Update with your project ID
                URL url = new URL("https://dialogflow.googleapis.com/v2/projects/" + projectId + "/agent/sessions/123456789:detectIntent");

                // Build request
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + token.getTokenValue())
                        .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                final String result;

                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    JSONObject jsonResponse = new JSONObject(json);

                    // Parse JSON correctly based on actual response structure
                    JSONObject queryResult = jsonResponse.getJSONObject("queryResult");

                    // Handle the fulfillmentText first (simpler approach)
                    if (queryResult.has("fulfillmentText") && !queryResult.isNull("fulfillmentText")) {
                        result = queryResult.getString("fulfillmentText");
                    }
                    // If no fulfillmentText, try to parse fulfillmentMessages
                    else if (queryResult.has("fulfillmentMessages")) {
                        JSONArray messagesArray = queryResult.getJSONArray("fulfillmentMessages");
                        if (messagesArray.length() > 0) {
                            JSONObject firstMessage = messagesArray.getJSONObject(0);

                            // Check if it has a "text" field that's an object
                            if (firstMessage.has("text")) {
                                Object textField = firstMessage.get("text");

                                if (textField instanceof JSONObject) {
                                    // If text is a JSONObject with a "text" array
                                    JSONObject textObj = (JSONObject) textField;
                                    if (textObj.has("text")) {
                                        JSONArray textArray = textObj.getJSONArray("text");
                                        result = textArray.getString(0);
                                    } else {
                                        result = "Error: Could not find text array in response";
                                    }
                                }
                                else if (textField instanceof JSONArray) {
                                    // If text is directly a JSONArray
                                    JSONArray textArray = (JSONArray) textField;
                                    result = textArray.getString(0);
                                }
                                else {
                                    result = "Error: Unexpected format for text field";
                                }
                            } else {
                                result = "Error: No text field found in response";
                            }
                        } else {
                            result = "Error: Empty fulfillmentMessages array";
                        }
                    } else {
                        result = "Error: No fulfillmentText or fulfillmentMessages in response";
                    }
                } else {
                    result = "Error: " + (response.body() != null ? response.body().string() : response.message());
                }

                // Update UI on main thread
                handler.post(() -> {
                    // Remove the typing message
                    messages.remove(messages.size() - 1);  // Remove the typing indicator

                    // Add the actual response message
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
                    long responseTimestamp = cal.getTimeInMillis();

                    Message botResponse = new Message(result, AGENT_EMAIL, userEmail, responseTimestamp);
                    messages.add(botResponse);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                });

            } catch (Exception e) {
                e.printStackTrace();
                final String errorMessage = "Sorry, I couldn't connect to the agent. Please try again later.";

                // Update UI on main thread
                handler.post(() -> {
                    // Remove typing indicator
                    messages.remove(messages.size() - 1);

                    // Add error message
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
                    long errorTimestamp = cal.getTimeInMillis();

                    Message errorMsg = new Message(errorMessage, ADMIN_EMAIL, userEmail, errorTimestamp);
                    messages.add(errorMsg);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                });
            }
        });
    }

    private void showPasswordGuidelinesDialog() {
        // Create the custom dialog view from the layout file
        View dialogView = getLayoutInflater().inflate(R.layout.chatguide, null);

        // Find Button in dialog layout
        Button btnUnderstand = dialogView.findViewById(R.id.btnAccept);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true); // Allow dialog to be dismissed by clicking outside

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the button click listener to dismiss the dialog
        btnUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}