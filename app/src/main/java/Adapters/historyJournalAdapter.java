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

import com.example.prototype.Announcements;
import com.example.prototype.History;
import com.example.prototype.R;

import java.util.List;

import HelperClasses.HistoryItem;

public class historyJournalAdapter extends RecyclerView.Adapter<historyJournalAdapter.ViewHolderHistory> {

    private final List<HistoryItem> historyList;
    private Context context;
    public historyJournalAdapter(Context context,List<HistoryItem> historyList) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public historyJournalAdapter.ViewHolderHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_item, parent, false);
        return new ViewHolderHistory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyJournalAdapter.ViewHolderHistory holder, int position) {
        HistoryItem historyItem = historyList.get(position);
        holder.bind(historyItem);
        holder.itemView.setOnClickListener(v -> {
            // When an item is clicked, start the Trivia activity
            Intent intent = new Intent(context, History.class);
            context.startActivity(intent); // Start the Trivia activity
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolderHistory extends RecyclerView.ViewHolder {
        TextView appointmentTitleTextView;

        public ViewHolderHistory(@NonNull View itemView) {
            super(itemView);
            appointmentTitleTextView = itemView.findViewById(R.id.appointmentTitleTextView);  // Ensure this matches the ID in your card_history_item.xml
        }

        public void bind(HistoryItem historyItem) {
            // Set the title from the historyItem object
            if (appointmentTitleTextView != null) {
                String formattedTitle = historyItem.getTitle() + "(" + historyItem.getDetails() + ")" ;
                appointmentTitleTextView.setText(formattedTitle);
            } else {
                Log.e("HistoryViewHolder", "appointmentTitleTextView is null");
            }
        }
    }
}