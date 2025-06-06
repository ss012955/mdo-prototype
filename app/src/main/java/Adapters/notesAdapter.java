package Adapters;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import HelperClasses.ItemClickListener;
import HelperClasses.Note;

public class notesAdapter extends RecyclerView.Adapter<notesAdapter.NoteViewHolder> implements Filterable {
    private List<Note> notes;          // Current list of notes to be displayed
    private List<Note> notesFull;      // Full list of notes for filtering
    public static ItemClickListener clickListener;

    public notesAdapter(List<Note> notes) {
        this.notes = notes;
        this.notesFull = new ArrayList<>(notes);  // Create a copy of the original list for filtering
    }
    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }



    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes_real, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        String originalDateTime = note.getDateTime(); // Assuming your Note object contains dateTime as a String
        String formattedDate = formatDate(originalDateTime);

        holder.dateTime.setText(formattedDate);  // Display the formatted date and time


        holder.symptoms.setText(note.getSymptoms());
        holder.mood.setText(note.getMood());
        holder.medicine.setText(note.getMedicine());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position); // Pass the clicked item's position
            }
        });
    }

    private String formatDate(String dateTime) {
        try {
            // Define the original date format (use your actual date format here)
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Parse the original date string
            Date date = originalFormat.parse(dateTime);

            // Define the new format you want
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());  // Nov. 15, 2024 2:00 PM

            // Return the formatted date
            return newFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime; // Return the original if formatting fails
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    // Implementing the filtering logic
    @Override
    public android.widget.Filter getFilter() {
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Note> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(notesFull);  // No filter, return the full list
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Note note : notesFull) {
                        if (note.getTitle().toLowerCase().contains(filterPattern) ||
                                note.getSymptoms().toLowerCase().contains(filterPattern)) {
                            filteredList.add(note);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notes.clear();
                if (results.values != null) {
                    notes.addAll((List) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }


    static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, dateTime, symptoms, mood, medicine;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.journal_title);
            dateTime = itemView.findViewById(R.id.journal_date_time);
            symptoms = itemView.findViewById(R.id.journal_symptoms);
            mood = itemView.findViewById(R.id.journal_mood);
            medicine = itemView.findViewById(R.id.journal_medicine);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.onClick(v, getBindingAdapterPosition());
            }
        }
    }
    // Method to update the list of notes
    public void updateNotesList(List<Note> newNotes) {
        this.notes = notes;
        this.notesFull = new ArrayList<>(notes);  // Refresh the full list
        notifyDataSetChanged();
    }
}