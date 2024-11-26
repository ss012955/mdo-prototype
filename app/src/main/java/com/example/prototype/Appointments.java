package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import Adapters.AppointmentsAdapter;
import Adapters.DefaultAdapter;
import HelperClasses.AppointmentsClass;
import HelperClasses.AppointmentsManager;
import HelperClasses.ItemClickListener;

public class Appointments extends BaseActivity implements ItemClickListener {
    private RecyclerView recyclerView;
    private List<AppointmentsClass> appointmentsList;
    private Button button;
    private CalendarView calendarView;
    private AppointmentsAdapter adapter = new AppointmentsAdapter(appointmentsList);
    private SharedPreferences prefs;
    private String userEmail;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(Appointments.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        recyclerView = findViewById(R.id.recyclerView);
        button = findViewById(R.id.btnStartBooking);
        button.setOnClickListener(v->{
            Intent intent = new Intent(this, BookingActivity.class);
            startActivity(intent);
            finish();
        });

        appointmentsList = new ArrayList<>();


        adapter.setClickListener(this);
        adapter = new AppointmentsAdapter(appointmentsList);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (userEmail != null) {
            String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_bookings.php";
            AppointmentsManager manager = new AppointmentsManager(this);

            // Pass a callback to handle the appointment list after it is fetched
            manager.fetchAppointments(url, userEmail, appointmentsList, adapter, this, new AppointmentsManager.AppointmentsCallback() {
                @Override
                public void onAppointmentsFetched(List<AppointmentsClass> fetchedList) {
                    if (fetchedList.isEmpty()) {
                        // Use the DefaultAdapter if there are no appointments
                        DefaultAdapter defaultAdapter = new DefaultAdapter("You have no appointments");
                        recyclerView.setAdapter(defaultAdapter);
                    } else {
                        // Use the normal adapter if appointments exist
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        }

    }
    @Override
    public void onClick(View v, int position) {
        AppointmentsClass clickedAppointment = appointmentsList.get(position);
        // Get the bookingID of the clicked appointment
        String bookingID = clickedAppointment.getBookingID();
        String service = clickedAppointment.getService();
        String dateTime = clickedAppointment.getDateTime();
        String remarks = clickedAppointment.getRemarks();

        // Pass the values to the CancelReschedActivity
        Intent i = new Intent(Appointments.this, CancelReschedActivity.class);
        i.putExtra("bookingID", bookingID);
        i.putExtra("service", service);
        i.putExtra("dateTime", dateTime);
        i.putExtra("remarks", remarks);
        startActivity(i);
    }


}