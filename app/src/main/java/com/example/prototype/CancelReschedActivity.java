package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import HelperClasses.UpdateAppointmentsManager;

public class CancelReschedActivity extends BaseActivity {
    private SharedPreferences prefs;
    private String userEmail;
    TabLayout tabLayout;
    TextView tvService, tvDate,tvRemarks;
    private Button buttonCancel, buttonReschedule;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cancel_resched);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        chatImageView = findViewById(R.id.chat);
        tabLayout = findViewById(R.id.tablayout);
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(CancelReschedActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        chatImageView.setOnClickListener(v -> {

            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });
        tvService = findViewById(R.id.tvService);
        tvDate = findViewById(R.id.tvDate);
        tvRemarks = findViewById(R.id.tvRemarks);

        Intent intent = getIntent();
        String bookingID = intent.getStringExtra("bookingID");
        String serviceR = intent.getStringExtra("service");
        String dateTime = intent.getStringExtra("dateTime");
        String remarksR = intent.getStringExtra("remarks");


        tvService.setText(String.format("%-15s %s", "Service:", serviceR));
        tvDate.setText(String.format("%-13s %s", "Date/Time:", dateTime));
        tvRemarks.setText(String.format("%-15s %s", "Remarks:", remarksR));


        buttonCancel = findViewById(R.id.buttonCancel);
        buttonReschedule = findViewById(R.id.buttonReschedule);


        buttonCancel.setOnClickListener(v->{
            UpdateAppointmentsManager delete = new UpdateAppointmentsManager();
            delete.bookingDelete(this, userEmail, bookingID);
        });


        buttonReschedule.setOnClickListener(v->{
            Intent i = new Intent(this, BookingActivityDate.class);
            i.putExtra("bookingID", bookingID);
            i.putExtra("ServiceResched", serviceR);
            i.putExtra("DateTime", dateTime);
            i.putExtra("RemarksResched", remarksR);
            startActivity(i);

        });



    }
}