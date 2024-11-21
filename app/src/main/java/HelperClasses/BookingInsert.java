package HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.ConfirmationActivity;

public class BookingInsert {
    DashboardManager dashboardManager;
    BaseClass baseClass;

    public BookingInsert() {
        baseClass = new BaseClass(); // Initialize baseClass here
    }

    public void bookingInsert(
            Context context,
            String service,
            String serviceType,
            String date,
            String time,
            String remarks) {

        baseClass.showTwoButtonDialog(
                context,
                "Confirmation",
                "Confirm this booking?",
                "Yes",
                "No",
                v -> {
                    // Create an Intent to navigate to the ConfirmationActivity
                    Intent intent = new Intent(context, ConfirmationActivity.class);

                    // Add the extras to the Intent
                    intent.putExtra("Service", service);
                    intent.putExtra("ServiceType", serviceType);
                    intent.putExtra("ChosenDate", date);
                    intent.putExtra("ChosenTime", time);
                    intent.putExtra("Remarks", remarks);

                    // Start the ConfirmationActivity with the Intent
                    context.startActivity(intent);

                    // Call the insert method to log the booking data
                    insert(service, serviceType, date, time, remarks);
                },
                v -> {
                    // Handle "No" button click (optional)
                }
        );
    }


    // Insert method to handle booking data
    private void insert(String service, String serviceType, String date, String time, String remarks) {
        // Log or save the booking data
        Log.d("BookingInsert", "Service: " + service + ", ServiceType: " + serviceType +
                ", Date: " + date + ", Time: " + time + ", Remarks: " + remarks);

        // You can add logic here to save the booking info to a database or shared preferences
    }
}
