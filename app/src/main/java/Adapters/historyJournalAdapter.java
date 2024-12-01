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

import HelperClasses.HistoryItem;

public class historyJournalAdapter extends RecyclerView.Adapter<historyJournalAdapter.HistoryViewHolder> {

    private final List<HistoryItem> historyList;

    public historyJournalAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyList.get(position);
        holder.titleTextView.setText(historyItem.getTitle());
        holder.contentTextView.setText(historyItem.getDetails());
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        RecyclerView historyRecyclerView;
        TextView titleTextView, contentTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyRecyclerView = itemView.findViewById(R.id.historyRecyclerView);
            titleTextView = itemView.findViewById(R.id.appointmentTitleTextView); // ID should match XML
            contentTextView = itemView.findViewById(R.id.appointmentDetailsTextView); // ID should match XML
        }

    }
}