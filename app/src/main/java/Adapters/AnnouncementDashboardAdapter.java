package Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.Announcements;
import com.example.prototype.R;
import com.example.prototype.Trivia;

import java.util.List;

import HelperClasses.AnnouncementsItems;

public class AnnouncementDashboardAdapter extends RecyclerView.Adapter<AnnouncementDashboardAdapter.ViewHolder> {

    private final List<AnnouncementsItems> announcementsList;
    private Context context;
    public AnnouncementDashboardAdapter(Context context,List<AnnouncementsItems> announcementsList) {
        this.announcementsList = announcementsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnnouncementDashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view and create ViewHolder instance correctly
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcements_card, parent, false);
        return new ViewHolder(view); // Corrected to use ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementDashboardAdapter.ViewHolder holder, int position) {
        AnnouncementsItems item = announcementsList.get(position);
        holder.title.setText(item.getTitle());

        // Load image using Glide
        Glide.with(holder.image.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            // When an item is clicked, start the Trivia activity
            Intent intent = new Intent(context, Announcements.class);
            context.startActivity(intent); // Start the Trivia activity
        });
    }

    @Override
    public int getItemCount() {
        return announcementsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.announcementsTitle);
            image = itemView.findViewById(R.id.announcementsImage);
        }
    }
}