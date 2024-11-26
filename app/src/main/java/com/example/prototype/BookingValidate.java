package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import HelperClasses.BaseClass;
import HelperClasses.BookingInsert;
import HelperClasses.BookingManager;

public class BookingValidate extends AppCompatActivity {
    BookingManager bookingManager;
    ProgressBar progressBar;
    TabLayout tabLayout;
    String service, serviceType, chosen_date, chosen_time, remarksInput;
    EditText remarks;
    Button buttonConfirm;
    TextView tvService, tvServiceType, tvDate;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_validate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "No email found");
        //Toast.makeText(BookingValidate.this, userEmail, Toast.LENGTH_SHORT).show();

        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(BookingValidate.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        bookingManager = new BookingManager();
        progressBar = findViewById(R.id.progressBar);
        bookingManager.smoothProgressUpdate(progressBar, 100);



        remarks = findViewById(R.id.remarksText);
        remarksInput = remarks.getText().toString();

        Intent intent = getIntent();
        service = intent.getStringExtra("Service");
        serviceType = intent.getStringExtra("ServiceType");
        chosen_date = intent.getStringExtra("CHOSEN_DATE");
        chosen_time = intent.getStringExtra("CHOSEN_TIME");


        tvService = findViewById(R.id.tvService);
        tvDate = findViewById(R.id.tvDate);

        try {
            // Assuming chosen_date is in "yyyy-MM-dd" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);

            Date date = inputFormat.parse(chosen_date);
            String formattedDate = outputFormat.format(date);

            String serviceText = String.format("%-16s %s", "Service:", serviceType);
            String dateText = String.format("%-14s %s / %s", "Date/Time:", formattedDate, chosen_time);

            tvService.setText(serviceText);
            tvDate.setText(dateText);
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v->{
            // Disable the button to prevent multiple clicks
            buttonConfirm.setEnabled(false);

            remarksInput = remarks.getText().toString();  // Get remarks input here, not before
            BookingInsert insert = new BookingInsert();

            // Call the insert method with a callback to re-enable the button
            insert.bookingInsert(this, service, serviceType, chosen_date, chosen_time, remarksInput, userEmail, () -> {
                // Re-enable the button after the insert operation is completed
                buttonConfirm.setEnabled(true);
            });
        });


    }
}