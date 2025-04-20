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

import com.example.prototype.Announcements;
import com.example.prototype.History;
import com.example.prototype.R;

import java.util.List;

import HelperClasses.HistoryItem;
import HelperClasses.ItemClickListener;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.HistoryViewHolder> {

    private final List<HistoryItem> historyList;
    public static ItemClickListener clickListener;

    public historyAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }
    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_real, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyList.get(position);
        holder.titleTextView.setText(historyItem.getTitle());
        holder.contentTextView.setText(historyItem.getDetails());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position); // Pass the clicked item's position
            }
        });
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
            titleTextView = itemView.findViewById(R.id.appointmentTitleTextView);
            contentTextView = itemView.findViewById(R.id.appointmentDetailsTextView);// ID should match XML// ID should match XML
        }

    }
}