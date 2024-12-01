package Adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.R;

import java.util.List;

import HelperClasses.AnnouncementsItems;

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder> {
    private final List<AnnouncementsItems> announcementsList;
    public AnnouncementsAdapter(List<AnnouncementsItems> announcementsList) {
        this.announcementsList = announcementsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementsItems item = announcementsList.get(position);
        holder.title.setText(item.getTitle());
        holder.text.setText(item.getText());

        // Load image using a library like Glide or Picasso
        Glide.with(holder.image.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);
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
            text = itemView.findViewById(R.id.announcementsText);
            image = itemView.findViewById(R.id.announcementsImage);
        }
    }
}