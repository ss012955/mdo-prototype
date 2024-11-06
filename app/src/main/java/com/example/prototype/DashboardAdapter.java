package com.example.prototype;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ANNOUNCEMENTS = 0;
    private static final int TYPE_APPOINTMENTS = 1;
    private static final int TYPE_TRIVIA = 2;

    private List<DashboardContent> contentList;

    public DashboardAdapter(List<DashboardContent> contentList) {
        this.contentList = contentList;
    }

    @Override
    public int getItemViewType(int position) {
        DashboardContent content = contentList.get(position);
        switch (contentList.get(position).getType()) {
            case "Announcements":
                return TYPE_ANNOUNCEMENTS;
            case "Appointments":
                return TYPE_APPOINTMENTS;
            case "Trivia":
                return TYPE_TRIVIA;
            default:
                return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_ANNOUNCEMENTS:
                View viewAnnouncements = inflater.inflate(R.layout.item_announcements, parent, false);
                return new AnnouncementsViewHolder(viewAnnouncements);
            case TYPE_APPOINTMENTS:
                View viewAppointments = inflater.inflate(R.layout.item_appointments, parent, false);
                return new AppointmentsViewHolder(viewAppointments);
            case TYPE_TRIVIA:
                View viewTrivia = inflater.inflate(R.layout.item_trivia, parent, false);
                return new TriviaViewHolder(viewTrivia);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DashboardContent content = contentList.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_ANNOUNCEMENTS:
                ((AnnouncementsViewHolder) holder).bind(content);
                break;
            case TYPE_APPOINTMENTS:
                ((AppointmentsViewHolder) holder).bind(content);
                break;
            case TYPE_TRIVIA:
                ((TriviaViewHolder) holder).bind(content);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    // Define ViewHolder for Announcements
    static class AnnouncementsViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPagerAnnouncements;

        AnnouncementsViewHolder(View itemView) {
            super(itemView);
            viewPagerAnnouncements = itemView.findViewById(R.id.viewPagerAnnouncements);
        }

        void bind(DashboardContent content) {
            List<String> images = content.getImages();
            ImageAdapter imageAdapter = new ImageAdapter(images);
            viewPagerAnnouncements.setAdapter(imageAdapter);
        }
    }

    // Define ViewHolder for Appointments
    static class AppointmentsViewHolder extends RecyclerView.ViewHolder {
        CalendarView calendarView;
        Button btnStartBooking;
        TextView tvNoAppointments;

        AppointmentsViewHolder(View itemView) {
            super(itemView);
            calendarView = itemView.findViewById(R.id.calendarView);
            btnStartBooking = itemView.findViewById(R.id.btnStartBooking);
            tvNoAppointments = itemView.findViewById(R.id.tvNoAppointments);
        }

        void bind(DashboardContent content) {
            List<String> appointments = content.getAppointments();
            if (appointments != null && !appointments.isEmpty()) {
                // Join all appointments into a single string separated by new lines
                StringBuilder appointmentsString = new StringBuilder();
                for (String appointment : appointments) {
                    appointmentsString.append(appointment).append("\n");
                }
                // Set the text to the TextView
                tvNoAppointments.setText(appointmentsString.toString().trim()); // Remove the last newline
            } else {
                tvNoAppointments.setText("You have no appointments.");
            }

        }
    }

    // Define ViewHolder for Trivia
    static class TriviaViewHolder extends RecyclerView.ViewHolder {
        TextView triviaContent;

        TriviaViewHolder(View itemView) {
            super(itemView);
            triviaContent = itemView.findViewById(R.id.triviaContent);
        }

        void bind(DashboardContent content) {
            triviaContent.setText(content.getTrivia());
        }
    }
}