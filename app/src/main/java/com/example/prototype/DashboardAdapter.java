package com.example.prototype;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.List;

import HelperClasses.AppointmentsClass;
import HelperClasses.ItemClickListener;
import HelperClasses.TriviaItem;
import HelperClasses.TriviaManager;
import Singleton.allAppointments;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<AppointmentsClass> appointmentsList;
    private final Context context;

    private static final int TYPE_ANNOUNCEMENTS = 0;
    private static final int TYPE_APPOINTMENTS = 1;
    private static final int TYPE_TRIVIA = 2;

    private List<DashboardContent> contentList;
    public static ItemClickListener clickListener;
    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    public DashboardAdapter(Context context, List<DashboardContent> contentList) {
        this.context = context;
        this.contentList = contentList;
    }
    public DashboardAdapter(List<DashboardContent> contentList) {
        // If you need this constructor, you can either provide a default context or leave it for future use
        this.context = null;  // Or handle as appropriate
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
                if (holder instanceof TriviaViewHolder) {
                    ((TriviaViewHolder) holder).bind(content, context);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    // Define ViewHolder for Announcements
    static class AnnouncementsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewPager2 viewPagerAnnouncements;

        AnnouncementsViewHolder(View itemView) {
            super(itemView);
            viewPagerAnnouncements = itemView.findViewById(R.id.viewPagerAnnouncements);
            itemView.setOnClickListener(this);
        }

        void bind(DashboardContent content) {
            List<String> images = content.getImages();
            ImageAdapter imageAdapter = new ImageAdapter(images);
            viewPagerAnnouncements.setAdapter(imageAdapter);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.onClick(v, getBindingAdapterPosition());
            }
        }
    }


    // Define ViewHolder for Appointments
    static class AppointmentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CalendarView calendarView;
        Button btnStartBooking;
        TextView tvNumberofAppointments;

        AppointmentsViewHolder(View itemView) {
            super(itemView);
            calendarView = itemView.findViewById(R.id.calendarView);
            btnStartBooking = itemView.findViewById(R.id.btnStartBooking);
            tvNumberofAppointments = itemView.findViewById(R.id.tvNumberofAppointments);

            itemView.setOnClickListener(this);
        }

        void bind(DashboardContent content) {
            int numberOfAppointments = allAppointments.getInstance().getNumberOfAppointments();

            String appointmentText = "You have " + numberOfAppointments + " appointment";
            if (numberOfAppointments != 1) {
                appointmentText += "s"; // Add "s" if more than 1 appointment
            }
            tvNumberofAppointments.setText(appointmentText + ".");


            btnStartBooking.setOnClickListener(v -> {
                // Start the BookingActivity
                Intent intent = new Intent(itemView.getContext(), BookingActivity.class);
                itemView.getContext().startActivity(intent);
            });
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.onClick(v, getBindingAdapterPosition());
            }
        }
    }


    // Define ViewHolder for Trivia
    static class TriviaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView triviaContent, triviaContent1, triviaContent2, triviaContent3;
        TriviaManager triviaManager = new TriviaManager();

        TriviaViewHolder(View itemView) {
            super(itemView);
            triviaContent = itemView.findViewById(R.id.triviaContent);
            triviaContent1 = itemView.findViewById(R.id.triviaContent1);
            triviaContent2 = itemView.findViewById(R.id.triviaContent2);
            triviaContent3 = itemView.findViewById(R.id.triviaContent3);

            itemView.setOnClickListener(this);
        }

        void bind(DashboardContent content, Context context) {
            triviaContent.setText("Loading trivia...");

            // Fetch trivia and display in this ViewHolder
// Fetch trivia and display in this ViewHolder
            TriviaManager.fetchTrivia(context, new TriviaManager.TriviaCallback() {
                @Override
                public void onSuccess(List<TriviaItem> triviaItems) {
                    if (triviaItems.size() > 0) {
                        TriviaItem trivia1 = triviaItems.get(0);
                        triviaContent.setText(trivia1.getTitle());
                    } else {
                        triviaContent.setText("No trivia available");
                        return; // Exit early if there is no trivia
                    }

                    if (triviaItems.size() > 1) { // Check if there is a second item
                        TriviaItem trivia2 = triviaItems.get(1);
                        triviaContent1.setText(trivia2.getTitle());
                        triviaContent1.setVisibility(View.VISIBLE);
                    }

                    if (triviaItems.size() > 2) { // Check if there is a third item
                        TriviaItem trivia3 = triviaItems.get(2);
                        triviaContent2.setText(trivia3.getTitle());
                        triviaContent2.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

        }


        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.onClick(v, getBindingAdapterPosition());
            }
        }
    }


}