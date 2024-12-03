package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.Note;

public class notesJournalAdapter extends RecyclerView.Adapter<notesJournalAdapter.NotesViewHolder> {

    private final List<Note> notesList;

    public notesJournalAdapter(List<Note> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes_item, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getDetails());
    }

    @Override
    public int getItemCount() {
        return notesList != null ? notesList.size() : 0;
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        RecyclerView historyRecyclerView;
        TextView titleTextView, contentTextView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            historyRecyclerView = itemView.findViewById(R.id.notesRecyclerView);
            titleTextView = itemView.findViewById(R.id.notesTitleTextView); // ID should match XML
            contentTextView = itemView.findViewById(R.id.notesDetailsTextView); // ID should match XML
        }

    }
}