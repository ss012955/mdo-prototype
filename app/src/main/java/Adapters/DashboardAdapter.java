package Adapters;



import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype.Announcements;
import com.example.prototype.BookingActivity;
import com.example.prototype.DashboardContent;
import com.example.prototype.R;
import com.example.prototype.Trivia;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import HelperClasses.EventDecoratorPending;
import HelperClasses.EventDecoratorApproved;
import HelperClasses.AnnouncementManager;
import HelperClasses.AnnouncementsItems;
import HelperClasses.AppointmentDaysClass;
import HelperClasses.AppointmentsClass;
import HelperClasses.AppointmentsManager;
import HelperClasses.ItemClickListener;
import HelperClasses.TriviaItem;
import HelperClasses.TriviaManager;
import Singleton.allAppointments;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private static final int TYPE_ANNOUNCEMENTS = 0;
    private static final int TYPE_APPOINTMENTS = 1;
    private static final int TYPE_TRIVIA = 2;
    public SharedPreferences prefs;
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
                ((AnnouncementsViewHolder) holder).bind(content, context);
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
        RecyclerView recyclerViewAnnouncement;
        AnnouncementDashboardAdapter announcementRecyclerViewAdapter;
        AnnouncementsViewHolder(View itemView) {
            super(itemView);
            recyclerViewAnnouncement= itemView.findViewById(R.id.recyclerViewAnnouncements);

            itemView.setOnClickListener(this);


        }

        void bind(DashboardContent content, Context context) {
            List<AnnouncementsItems> announcementsList = new ArrayList<>();  // Corrected name

            // Fetch announcements
            AnnouncementManager.fetchAnnouncements(context, new AnnouncementManager.AnnouncementsCallback() {
                @Override
                public void onSuccess(List<AnnouncementsItems> fetchedAnnouncementsItems) {

                    int limit = Math.min(fetchedAnnouncementsItems.size(), 3);
                    for (int i = 0; i < limit; i++) {
                        announcementsList.add(fetchedAnnouncementsItems.get(i));  // Corrected name here
                    }

                    announcementRecyclerViewAdapter = new AnnouncementDashboardAdapter(context,announcementsList); // Updated constructor
                    recyclerViewAnnouncement.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewAnnouncement.setAdapter(announcementRecyclerViewAdapter);
                    recyclerViewAnnouncement.setLayoutManager(
                            new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    );
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("AnnouncementsViewHolder", errorMessage);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), Announcements.class);
            itemView.getContext().startActivity(intent);
        }
    }


    // Define ViewHolder for Appointments
    static class AppointmentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialCalendarView calendarView;
        Button btnStartBooking;
        TextView tvNumberofAppointments;

        private Context context;
        private List<AppointmentsClass> appointmentsList = new ArrayList<>();
        private List<AppointmentDaysClass> appointmentsDays = new ArrayList<>();
        private final HashSet<CalendarDay> fetched = new HashSet<>();
        AppointmentsViewHolder(View itemView) {
            super(itemView);
            calendarView = itemView.findViewById(R.id.calendarView);
            btnStartBooking = itemView.findViewById(R.id.btnStartBooking);
            tvNumberofAppointments = itemView.findViewById(R.id.tvNumberofAppointments);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void bind(DashboardContent content){
            // Retrieve the user's email from SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String userEmail = prefs.getString("user_email", "No email found");

            // Call the new method to fetch and update the number of approved appointments
            fetchApprovedAppointments(userEmail);

            // Set up the button click listener for the booking activity
            btnStartBooking.setOnClickListener(v -> {
                // Start the BookingActivity
                Intent intent = new Intent(itemView.getContext(), BookingActivity.class);
                itemView.getContext().startActivity(intent);
            });

            itemView.setOnClickListener(this);
            fetchAppointments();

        }
        private void fetchAppointments() {
            String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_bookings.php";

            // Retrieve SharedPreferences locally
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String userEmail = prefs.getString("user_email", "No email found");
            AppointmentsManager manager = new AppointmentsManager(context);
            // Use the fetchAppointments() method defined earlier
            manager.fetchAppointments(url, userEmail, appointmentsList,appointmentsDays,null,context, new AppointmentsManager.AppointmentsCallback() {
                @Override
                public void onAppointmentsFetched(List<AppointmentsClass> fetchedAppointments, List<AppointmentDaysClass> fetchedDays) {
                    appointmentsList.clear();
                    appointmentsList.addAll(fetchedAppointments);
                    appointmentsDays.clear();
                    for (AppointmentDaysClass day : fetchedDays) {
                        // Assuming AppointmentDaysClass has getYear(), getMonth(), and getDay() methods
                        CalendarDay calendarDay = CalendarDay.from(day.getYear(), day.getMonth() - 1, day.getDay());
                        fetched.add(calendarDay);
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(context, "Error fetching appointments: " + errorMessage, Toast.LENGTH_LONG).show();
                }

            });
        }

        private void fetchApprovedAppointments(String userEmail) {
            String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_approvedbookings.php";

            AppointmentsManager manager = new AppointmentsManager(context);

            // Fetch appointments with status "Approved"
            manager.fetchAppointments(url, userEmail, new ArrayList<>(), new ArrayList<>(), null, context, new AppointmentsManager.AppointmentsCallback() {
                @Override
                public void onAppointmentsFetched(List<AppointmentsClass> fetchedAppointments, List<AppointmentDaysClass> fetchedDays) {
                    // Filter appointments that have the "Approved" status
                    List<AppointmentsClass> approvedAppointments = new ArrayList<>();
                    for (AppointmentsClass appointment : fetchedAppointments) {
                        if ("Approved".equals(appointment.getStatus())) {
                            approvedAppointments.add(appointment);
                        }
                    }

                    // Count the number of "Approved" bookings
                    int numberOfApprovedAppointments = approvedAppointments.size();

                    // Update the text view with the count of approved appointments
                    String appointmentText = "You have " + numberOfApprovedAppointments + " upcoming appointment";
                    if (numberOfApprovedAppointments != 1) {
                        appointmentText += "s"; // Add "s" if more than 1 appointment
                    }
                    tvNumberofAppointments.setText(appointmentText + ".");
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(context, "Error fetching appointments: " + errorMessage, Toast.LENGTH_LONG).show();
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


    // Define ViewHolder for Trivia
    static class TriviaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerView recyclerViewTrivia;
        TriviaDashboardAdapter triviaRecyclerViewAdapter;

        TriviaViewHolder(View itemView) {
            super(itemView);
            recyclerViewTrivia = itemView.findViewById(R.id.recyclerViewTrivia);
            itemView.setOnClickListener(this);
        }

        void bind(DashboardContent content, Context context) {
            List<TriviaItem> triviaItems = new ArrayList<>();

            // Fetch trivia and update RecyclerView
            TriviaManager.fetchTrivia(context, new TriviaManager.TriviaCallback() {
                @Override
                public void onSuccess(List<TriviaItem> fetchedTriviaItems) {
                    // Store the latest three trivia items
                    int limit = Math.min(fetchedTriviaItems.size(), 3); // Limit to 3 items
                    for (int i = 0; i < limit; i++) {
                        triviaItems.add(fetchedTriviaItems.get(i));
                    }

                    // Set up RecyclerView
                    triviaRecyclerViewAdapter = new TriviaDashboardAdapter(context, triviaItems);
                    recyclerViewTrivia.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewTrivia.setAdapter(triviaRecyclerViewAdapter);
                    recyclerViewTrivia.setLayoutManager(
                            new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    );
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle error (if needed)
                    Log.e("TriviaViewHolder", errorMessage);
                }
            });
        }



        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), Trivia.class);
            itemView.getContext().startActivity(intent);
        }
    }
    public void updateContentList(List<DashboardContent> newContentList) {
        this.contentList = newContentList;
        notifyDataSetChanged(); // Refresh the adapter
    }
}