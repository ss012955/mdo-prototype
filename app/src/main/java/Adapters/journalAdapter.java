package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

public class journalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NOTES = 0;
    private static final int VIEW_TYPE_HISTORY = 1;

    private final List<contentJournal> contentList;
    private final Context context;

    public journalAdapter(Context context, List<contentJournal> contentList) {
        this.context = context;
        this.contentList = contentList;
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
                ((NotesViewHolder) holder).bind(content);
                break;
            case VIEW_TYPE_HISTORY:
                ((HistoryViewHolder) holder).bind(content);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        RecyclerView notesRecyclerView;
        TextView titleTextView, contentTextView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notesRecyclerView = itemView.findViewById(R.id.notesRecyclerView);
            titleTextView = itemView.findViewById(R.id.notesTitleTextView);
            contentTextView = itemView.findViewById(R.id.notesDetailsTextView);
        }

        // bind() method to populate the Notes data
        public void bind(contentJournal content) {
            if (titleTextView != null) {
                titleTextView.setText(content.getTitle());
            } else {
                Log.e("HistoryViewHolder", "titleTextView is null");
            }

            if (contentTextView != null) {
                contentTextView.setText(content.getDescription());
            } else {
                Log.e("HistoryViewHolder", "contentTextView is null");
            }

            if (notesRecyclerView != null) {
                notesJournalAdapter historyAdapter = new notesJournalAdapter(content.getNotesList());
                notesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                notesRecyclerView.setAdapter(historyAdapter);
            } else {
                Log.e("HistoryViewHolder", "historyRecyclerView is null");
            }
        }
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        RecyclerView historyRecyclerView;
        TextView titleTextView,contentTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyRecyclerView = itemView.findViewById(R.id.historyRecyclerView);
            titleTextView = itemView.findViewById(R.id.appointmentTitleTextView);
            contentTextView = itemView.findViewById(R.id.notesDetailsTextView);
        }

        // bind() method to populate the History data
        public void bind(contentJournal content) {
            if (titleTextView != null) {
                titleTextView.setText(content.getTitle());
            } else {
                Log.e("HistoryViewHolder", "titleTextView is null");
            }

            if (contentTextView != null) {
                contentTextView.setText(content.getDescription());
            } else {
                Log.e("HistoryViewHolder", "contentTextView is null");
            }

            if (historyRecyclerView != null) {
                historyJournalAdapter historyAdapter = new historyJournalAdapter(content.getHistoryList());
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                historyRecyclerView.setAdapter(historyAdapter);
            } else {
                Log.e("HistoryViewHolder", "historyRecyclerView is null");
            }
        }
    }
}