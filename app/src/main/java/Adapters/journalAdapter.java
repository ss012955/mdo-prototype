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

import java.util.List;

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

    public static class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecyclerView historyRecyclerView;
        TextView titleTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyRecyclerView = itemView.findViewById(R.id.historyRecyclerView);
            titleTextView = itemView.findViewById(R.id.appointmentTitleTextView);
            itemView.setOnClickListener(this);
        }

        // bind() method to populate the History data
        public void bind(contentJournal content, Context context) {
            if (titleTextView != null) {
                titleTextView.setText(content.getTitle());
            } else {
                Log.e("HistoryViewHolder", "titleTextView is null");
            }


            if (historyRecyclerView != null) {
                historyJournalAdapter historyAdapter = new historyJournalAdapter(context,content.getHistoryList());
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                historyRecyclerView.setAdapter(historyAdapter);
            } else {
                Log.e("HistoryViewHolder", "historyRecyclerView is null");
            }
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), History.class);
            itemView.getContext().startActivity(intent);
        }
    }
}