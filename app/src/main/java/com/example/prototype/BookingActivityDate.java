package com.example.prototype;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import HelperClasses.BookingManager;
import Singleton.AvailabilityChecker;

public class BookingActivityDate extends BaseActivity {
    BookingManager bookingManager;
    TabLayout tabLayout;
    Button buttomTime, buttonNext;
    Date chosenDate;
    String selectedTimeSlot;
    String chosenTimeSlot;
    String service, serviceType;
    ProgressBar progressBar;
    MaterialCalendarView calendarView;
    private ImageView chatImageView;
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
        chatImageView = findViewById(R.id.chat);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "No email found");

        //Toast.makeText(BookingActivityDate.this, userEmail, Toast.LENGTH_SHORT).show();
        chatImageView.setOnClickListener(v -> {

            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });
        Intent intent = getIntent();

        service = intent.getStringExtra("Service");
        serviceType = intent.getStringExtra("ServiceType");
        if (service != null || serviceType != null) {
            Toast.makeText(this, "Selected Service: " + service, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No service selected.", Toast.LENGTH_SHORT).show();
        }

        String serviceR = intent.getStringExtra("ServiceResched");
        String bookingID = intent.getStringExtra("bookingID");
        String remarksR = intent.getStringExtra("RemarksResched");

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }
        tabLayout.selectTab(null);
        // Reset the tab icons to their unselected state
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(icons[i]); // Reset to the original icon (unselected)
            }
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
        chosenDate = bookingManager.handleCalendarDateSelection(calendarView, this);
        bookingManager.fetchBookingsForUser(userEmail,calendarView, BookingActivityDate.this);
        // Handle time slot selection
        buttomTime.setOnClickListener(v -> {
            chosenDate = bookingManager.getSelectedDate();
            Log.d("BookingDebug", "chosenDate: " + chosenDate);
            Log.d("DEBUG", "Button clicked, chosenTimeSlot before dialog: " + selectedTimeSlot);

            if (chosenDate == null) {
                Toast.makeText(this, "Please select a date first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serviceR != null && !serviceR.isEmpty()) {
                // For rescheduling, use the ServiceResched as the main service
                service = serviceR;
                // You might need to set a default serviceType or derive it from the service
                if (serviceType == null || serviceType.isEmpty()) {
                    serviceType = "default"; // or derive from service
                }
            }

            if (service != null) {
                Toast.makeText(this, "Selected Service: " + service, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No service selected.", Toast.LENGTH_SHORT).show();
            }


            String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(chosenDate);
            Log.d("BookingDebug", "formattedDate: " + formattedDate);

            AvailabilityChecker checker = new AvailabilityChecker();
            checker.showAvailabilityAwareTimeSlotDialog(this, formattedDate, serviceR, serviceType,
                    new AvailabilityChecker.TimeSlotSelectionCallback() {
                        @Override
                        public void onTimeSlotSelected(String timeSlot) {
                            selectedTimeSlot = timeSlot;
                            chosenTimeSlot = convertTimeSlot(timeSlot);
                            buttomTime.setText(chosenTimeSlot);
                        }

                        @Override
                        public void onSelectionCancelled() {
                            Log.d("BookingDebug", "Time selection cancelled");
                            Toast.makeText(BookingActivityDate.this,
                                    "Time selection cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle calendar date selection
        findViewById(R.id.buttonNext).setOnClickListener(v -> {
            chosenDate = bookingManager.getSelectedDate();
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



            intentTime.putExtra("bookingID", bookingID);
            intentTime.putExtra("ServiceResched", serviceR);
            intentTime.putExtra("RemarksResched", remarksR);

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

    private String convertTimeSlot(String time) {
        String newTime = time; // Default to original time

        // Check and convert specific time slots to a time without a range
        switch (time) {
            case "8-9 AM":
                newTime = "8:00 AM";
                break;
            case "9-10 AM":
                newTime = "9:00 AM";
                break;
            case "10-11 AM":
                newTime = "10:00 AM";
                break;
            case "11-12 PM":
                newTime = "11:00 AM";
                break;
            case "1-2 PM":
                newTime = "1:00 PM";
                break;
            case "2-3 PM":
                newTime = "2:00 PM";
                break;
            case "3-4 PM":
                newTime = "3:00 PM";
                break;
            case "4-5 PM":
                newTime = "4:00 PM";
                break;
            default:
                // If the time doesn't match a specific slot, leave it unchanged
                Log.w("BookingInsert", "Unexpected time format: " + time);
                break;
        }

        return newTime;
    }
}