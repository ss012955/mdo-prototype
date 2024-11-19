package com.example.prototype;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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


        bookingManager = new BookingManager();
        calendarView = findViewById(R.id.calendarView);

        // Handle time slot selection
        findViewById(R.id.buttomTime).setOnClickListener(v -> {
            bookingManager.showTimeSlotDialog(this, timeSlot -> {
                chosenTimeSlot = timeSlot; // Save selected time slot
                Toast.makeText(this, "Time selected: " + chosenTimeSlot, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Date: " + formattedDate + " Time: " + formattedTime, Toast.LENGTH_SHORT).show();


            // Pass the data to the next activity
            Intent intent = new Intent(this, home.class);
            intent.putExtra("CHOSEN_DATE", formattedDate);
            intent.putExtra("CHOSEN_TIME", formattedTime);
            startActivity(intent);


        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Retrieve the selected date from BookingManager
        chosenDate = bookingManager.getSelectedDate();

        if (chosenDate != null) {
            Toast.makeText(this, "Chosen date: " + chosenDate.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}