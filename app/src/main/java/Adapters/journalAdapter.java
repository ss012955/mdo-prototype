package Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.History;
import com.example.prototype.Notes;
import com.example.prototype.R;
import com.example.prototype.Trivia;

import java.util.ArrayList;
import java.util.List;

import HelperClasses.HistoryItem;
import HelperClasses.HistoryManager;
import HelperClasses.ItemClickListener;

public class journalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NOTES = 0;
    private static final int VIEW_TYPE_HISTORY = 1;

    private final List<contentJournal> contentList;
    private final Context context;
    public static ItemClickListener clickListener;
    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }
    public journalAdapter(Context context, List<contentJournal> contentList, ItemClickListener clickListener) {
        this.context = context;
        this.contentList = contentList;
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

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notesRecyclerView = itemView.findViewById(R.id.notesRecyclerView);
            titleTextView = itemView.findViewById(R.id.notesTitleTextView);

            itemView.setOnClickListener(this);
        }

        // bind() method to populate the Notes data
        public void bind(contentJournal content, Context context) {
            if (titleTextView != null) {
                titleTextView.setText(content.getTitle());
            } else {
                Log.e("HistoryViewHolder", "titleTextView is null");
            }

            if (notesRecyclerView != null) {
                notesJournalAdapter historyAdapter = new notesJournalAdapter(context,content.getNotesList());
                notesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                notesRecyclerView.setAdapter(historyAdapter);
            } else {
                Log.e("HistoryViewHolder", "historyRecyclerView is null");
            }
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), Notes.class);
            itemView.getContext().startActivity(intent);
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
            HistoryManager.fetchHistoryWithTitleAndDate("http://192.168.100.4/MDOapp-main/Admin/fetch_booking_completed.php", "userEmail@example.com", historyItems, historyRecyclerViewAdapter, context, new HistoryManager.HistoryCallback() {
                @Override
                public void onSuccess(List<HistoryItem> fetchedHistoryItems) {
                    // Store the latest three history items
                    int limit = Math.min(fetchedHistoryItems.size(), 3); // Limit to 3 items
                    for (int i = 0; i < limit; i++) {
                        historyItems.add(fetchedHistoryItems.get(i));
                    }

                    // Ensure recyclerViewHistory is not null
                    if (recyclerViewHistory != null) {
                        // Set up RecyclerView with the custom adapter
                        historyRecyclerViewAdapter = new historyJournalAdapter(context,historyItems);
                        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)); // Set layout manager
                        recyclerViewHistory.setAdapter(historyRecyclerViewAdapter); // Set adapter
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