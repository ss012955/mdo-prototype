package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import HelperClasses.BookingInsert;
import HelperClasses.BookingManager;
import HelperClasses.BookingUpdate;

public class BookingValidate extends AppCompatActivity {
    BookingManager bookingManager;
    ProgressBar progressBar;
    TabLayout tabLayout;
    String service, serviceType, chosen_date, chosen_time, remarksInput;
    EditText remarks;
    Button buttonConfirm;
    TextView tvService, tvServiceType, tvDate;
    String userEmail;
    String bookingID;
    private ImageView chatImageView;
    String serviceResched, remarksResched;
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
        chatImageView = findViewById(R.id.chat);
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
        chatImageView.setOnClickListener(v -> {

            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });
        Intent intent = getIntent();
        bookingID = intent.getStringExtra("bookingID");
        chosen_date = intent.getStringExtra("CHOSEN_DATE");
        chosen_time = intent.getStringExtra("CHOSEN_TIME");
        tvService = findViewById(R.id.tvService);
        tvDate = findViewById(R.id.tvDate);

        serviceResched = intent.getStringExtra("ServiceResched");
        remarksResched = intent.getStringExtra("RemarksResched");


        try {
            // Assuming chosen_date is in "yyyy-MM-dd" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);

            Date date = inputFormat.parse(chosen_date);
            String formattedDate = outputFormat.format(date);

            String dateText = String.format("%-14s %s / %s", "Date/Time:", formattedDate, chosen_time);
            String serviceText = "";

            if (bookingID != null) {
                serviceText = String.format("%-14s %s", "Service:", serviceResched);
                remarks.setText(remarksResched);
            } else {
                service = intent.getStringExtra("Service");
                serviceType = intent.getStringExtra("ServiceType");
                serviceText = String.format("%-14s %s", "Service:", serviceType);
            }

            remarksInput = remarks.getText().toString();
           tvDate.setText(dateText);
           remarks.setText(remarksResched);
           tvService.setText(serviceText);

        } catch (Exception e) {
            e.fillInStackTrace();
        }

            buttonConfirm = findViewById(R.id.buttonConfirm);
            buttonConfirm.setOnClickListener(v->{
                // Disable the button to prevent multiple clicks
                buttonConfirm.setEnabled(false);

                remarksInput = remarks.getText().toString();  // Get remarks input here, not before
                BookingInsert insert = new BookingInsert();
                BookingUpdate update = new BookingUpdate();

                    if(bookingID != null ){
                            update.bookingUpdate(this,serviceResched, bookingID, userEmail, chosen_date, chosen_time, remarksInput, () -> {
                                buttonConfirm.setEnabled(true);
                            });
                    }else{
                        insert.bookingInsert(this, service, serviceType, chosen_date, chosen_time, remarksInput, userEmail, () -> {
                            // Re-enable the button after the insert operation is completed
                            buttonConfirm.setEnabled(true);
                        });

                    }
            });


        }

}