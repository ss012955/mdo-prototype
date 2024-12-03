package Adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import HelperClasses.Note;

public class notesJournalAdapter extends RecyclerView.Adapter<notesJournalAdapter.NotesViewHolder> {

    private final List<Note> notesList;
    private Context context;
    public notesJournalAdapter(Context context, List<Note> notesList) {
        this.notesList = notesList;
        this.context = context;
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
        holder.itemView.setOnClickListener(v -> {
            // When an item is clicked, start the Trivia activity
            Intent intent = new Intent(context, Notes.class);
            context.startActivity(intent); // Start the Trivia activity
        });
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
            titleTextView = itemView.findViewById(R.id.notesTitleTextView); // ID should match XML// ID should match XML
        }

    }
}