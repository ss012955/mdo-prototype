package Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.History;
import com.example.prototype.Notes;
import com.example.prototype.R;
import com.example.prototype.Trivia;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import HelperClasses.HistoryItem;
import HelperClasses.HistoryManager;
import HelperClasses.ItemClickListener;
import HelperClasses.Note;

public class journalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NOTES = 0;
    private static final int VIEW_TYPE_HISTORY = 1;

    private final List<contentJournal> contentList;
    private final Context context;
    private static String userEmail;
    public static ItemClickListener clickListener;
    public static String noteId, title, symptoms, mood, medicine;
    public static List<Note> notesList;


    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }
    public journalAdapter(Context context, List<contentJournal> contentList, String userEmail, ItemClickListener clickListener) {
        this.context = context;
        this.contentList = contentList;
        this.userEmail = userEmail;
        journalAdapter.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return contentList.get(position).getType().equals("notes") ? VIEW_TYPE_NOTES : VIEW_TYPE_HISTORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NOTES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes, parent, false);
            return new NotesViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history, parent, false);
            return new HistoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        contentJournal content = contentList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_NOTES:
                ((NotesViewHolder) holder).bind(content, context);
                break;
            case VIEW_TYPE_HISTORY:
                ((HistoryViewHolder) holder).bind(content, context);
                break;
        }

        // Set click listener for the item view
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position); // Trigger the `onClick()` in `fJournal`.
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerView notesRecyclerView;
        TextView titleTextView;
        List<Note> notesItem = new ArrayList<>(); // List to store notes items
        notesJournalAdapter notesAdapter;
        private Handler handler;
        private Runnable notesRunnable;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notesRecyclerView = itemView.findViewById(R.id.notesRecyclerView);
            titleTextView = itemView.findViewById(R.id.notesTitleTextView);
            itemView.setOnClickListener(this);

            // Set up a Handler for periodic polling
            handler = new Handler(Looper.getMainLooper());
            notesRunnable = new Runnable() {
                @Override
                public void run() {
                    loadNotesTitles(notesItem, itemView.getContext(), null); // Fetch and update notes
                    handler.postDelayed(this, 5000); // Poll every 5 seconds
                }
            };
        }

        // bind() method to populate the Notes data
        public void bind(contentJournal content, Context context) {
            // Fetch the notes list if not already available
            notesItem = content.getNotesList(); // Assuming notes are passed here

            // Fetch the notes titles and update the adapter once titles are fetched
            loadNotesTitles(notesItem, context, () -> {
                // Bind title text
                if (titleTextView != null) {
                    titleTextView.setText(content.getTitle());
                } else {
                    Log.e("NotesViewHolder", "titleTextView is null");
                }

                // Bind RecyclerView for notes list
                if (notesRecyclerView != null) {
                    notesJournalAdapter notesAdapter = new notesJournalAdapter(context, notesItem);
                    notesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    notesRecyclerView.setAdapter(notesAdapter);
                    notesAdapter.notifyDataSetChanged(); // Notify the adapter with the updated data
                } else {
                    Log.e("NotesViewHolder", "notesRecyclerView is null");
                }
            });
        }

        // Fetch the notes titles
        private void loadNotesTitles(List<Note> notesItem, Context context, Runnable callback) {
            String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_notes.php"; // URL to your PHP script
            String fullUrl = url + "?umak_email=" + userEmail;

            // Start a background thread to handle the network operation
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create the URL object
                        URL urlObj = new URL(fullUrl);

                        // Set up the connection
                        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000); // Timeout 5 seconds
                        connection.setReadTimeout(5000); // Timeout 5 seconds

                        // Read the response
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        connection.disconnect();

                        // Parse the response JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            JSONArray notesArray = jsonResponse.getJSONArray("notes");
                            notesItem.clear(); // Clear the existing list before adding new notes titles

                            // Loop through the notes array and fetch titles
                            for (int i = 0; i < notesArray.length(); i++) {
                                JSONObject noteObj = notesArray.getJSONObject(i);
                                String title = noteObj.getString("title");

                                // Add the title to the list
                                notesItem.add(new Note(title)); // Assuming Note class has a constructor that takes a title
                            }

                            // Once the titles are fetched, post to the main thread to update the UI
                            new Handler(Looper.getMainLooper()).post(callback);

                        } else {
                            // Handle error or empty notes list if success is false
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(context, "No notes found!", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(context, "Failed to fetch notes.", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }).start();
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), Notes.class);
            itemView.getContext().startActivity(intent);
        }

        // Start polling when the ViewHolder is bound
        public void startPolling() {
            handler.post(notesRunnable);
        }

        // Stop polling when the ViewHolder is no longer needed
        public void stopPolling() {
            handler.removeCallbacks(notesRunnable);
        }
    }
  // Define ViewHolder for History
    static class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerView recyclerViewHistory;
        historyJournalAdapter historyRecyclerViewAdapter;

        HistoryViewHolder(View itemView) {
            super(itemView);
            recyclerViewHistory = itemView.findViewById(R.id.historyRecyclerView);
            itemView.setOnClickListener(this);
        }

        void bind(contentJournal content, Context context) {
            List<HistoryItem> historyItems = new ArrayList<>();

            // Fetch history from HistoryManager
            HistoryManager.fetchHistoryWithTitleAndDate("https://umakmdo-91b845374d5b.herokuapp.com/fetch_booking_completed.php", userEmail, historyItems, historyRecyclerViewAdapter, context, new HistoryManager.HistoryCallback() {
                @Override
                public void onSuccess(List<HistoryItem> fetchedHistoryItems) {
                    // Store the latest three history items
                    int limit = Math.min(fetchedHistoryItems.size(), 3); // Limit to 3 items
                    for (int i = 0; i < limit; i++) {
                        historyItems.add(fetchedHistoryItems.get(i));
                    }

                    // Ensure recyclerViewHistory is not null
                    if (recyclerViewHistory != null) {
                        // Post the update to the main thread
                        recyclerViewHistory.post(() -> {
                            // Set up RecyclerView with the custom adapter
                            historyRecyclerViewAdapter = new historyJournalAdapter(context, historyItems);
                            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(context));
                            recyclerViewHistory.setAdapter(historyRecyclerViewAdapter);
                            historyRecyclerViewAdapter.notifyDataSetChanged();  // Notify the adapter on the main thread
                        });
                    } else {
                        Log.e("HistoryViewHolder", "recyclerViewHistory is null");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle error (if needed)
                    Log.e("HistoryViewHolder", "Error fetching history: " + errorMessage);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), History.class); // Navigate to History activity
            itemView.getContext().startActivity(intent);
        }


    }
}