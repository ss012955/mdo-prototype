package com.example.prototype;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import HelperClasses.BookingManager;

public class BookingActivityDate extends BaseActivity {
    BookingManager bookingManager;
    TabLayout tabLayout;
    Button buttomTime, buttonNext;
    Date chosenDate;
    String chosenTimeSlot;
    String service, serviceType;
    ProgressBar progressBar;
    CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_date);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");

        Toast.makeText(BookingActivityDate.this, userEmail, Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        service = intent.getStringExtra("Service");
        serviceType = intent.getStringExtra("ServiceType");
        if (service != null || serviceType != null) {
            Toast.makeText(this, "Selected Service: " + service, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No service selected.", Toast.LENGTH_SHORT).show();
        }

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(BookingActivityDate.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        bookingManager = new BookingManager();
        progressBar = findViewById(R.id.progressBar);
        bookingManager.smoothProgressUpdate(progressBar, 55);



        calendarView = findViewById(R.id.calendarView);

        buttomTime = findViewById(R.id.buttomTime);

        // Handle time slot selection
        buttomTime.setOnClickListener(v -> {
            bookingManager.showTimeSlotDialog(this, timeSlot -> {
                chosenTimeSlot = timeSlot; // Save selected time slot
                Toast.makeText(this, "Time selected: " + chosenTimeSlot, Toast.LENGTH_SHORT).show();
                buttomTime.setText(chosenTimeSlot);

            });
        });

        // Handle calendar date selection
        chosenDate = bookingManager.handleCalendarDateSelection(calendarView, this);

        findViewById(R.id.buttonNext).setOnClickListener(v -> {
            chosenDate = bookingManager.getSelectedDate();
            chosenTimeSlot = bookingManager.getSelectedTimeSlot();
            if (chosenDate == null || chosenTimeSlot == null) {
                Toast.makeText(this, "Please select a date and time first.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = dateFormat.format(chosenDate);
            // Convert chosen time to HH:MM AM/PM format
            String formattedTime = chosenTimeSlot;

            // Show the selected date and time in the desired format
            //Toast.makeText(this, "Date: " + formattedDate + " Time: " + formattedTime, Toast.LENGTH_SHORT).show();


            // Pass the data to the next activity
            Intent intentTime = new Intent(this, BookingValidate.class);
            intentTime.putExtra("CHOSEN_DATE", formattedDate);
            intentTime.putExtra("CHOSEN_TIME", formattedTime);
            intentTime.putExtra("Service", service);
            intentTime.putExtra("ServiceType", serviceType);
            startActivity(intentTime);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Retrieve the selected date from BookingManager
        chosenDate = bookingManager.getSelectedDate();

        if (chosenDate != null) {
            //Toast.makeText(this, "Chosen date: " + chosenDate.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}