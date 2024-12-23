package HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prototype.ConfirmationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
            String remarks,String umakEmail,  Runnable onComplete) {

        baseClass.showTwoButtonDialog(
                context,
                "Confirmation",
                "Confirm this booking?",
                "Yes",
                "No",
                v -> {
                    String convertedTime = convertTimeSlot(time);

                    insert(context, service, serviceType, date, convertedTime, remarks, umakEmail, onComplete);

                    baseClass.showOneButtonDialogNotCancellable(context, "Confirmation", "Booking successful", "Okay", v1->{
                        Intent intent = new Intent(context, ConfirmationActivity.class);
                        intent.putExtra("Service", service);
                        intent.putExtra("ServiceType", serviceType);
                        intent.putExtra("ChosenDate", date);
                        intent.putExtra("ChosenTime", time);
                        intent.putExtra("Remarks", remarks);
                        context.startActivity(intent);

                        if (onComplete != null) onComplete.run();
                    });
                },
                v -> {
                    // Handle "No" button click (optional)
                }

        );


    }


    // Insert method to handle booking data
    private void insert(Context context, String service, String serviceType, String date, String time, String remarks, String umakEmail, Runnable onComplete) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/insert_booking.php"; // Replace with your PHP endpoint

        try {
            String formattedDate = convertToSqlDateFormat(date);
            String formattedTime = convertToSqlTimeFormat(time);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        // Response received
                        //Toast.makeText(context, "Response: " + response, Toast.LENGTH_SHORT).show();
                        // Notify completion (re-enable button)
                        if (onComplete != null) onComplete.run();
                    },
                    error -> {
                        // Error received
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        // Notify completion (re-enable button even on error)
                        if (onComplete != null) onComplete.run();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("umak_email", umakEmail); // Replace with the actual user email
                    params.put("service", service);
                    params.put("service_type", serviceType);
                    params.put("booking_date", formattedDate);
                    params.put("booking_time", formattedTime);
                    params.put("remarks", remarks);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

        } catch (ParseException e) {
            Toast.makeText(context, "Date/Time formatting error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Notify completion even if there was an error
            if (onComplete != null) onComplete.run();
        }
    }

    private String convertToSqlDateFormat(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date parsedDate = inputFormat.parse(date);
        return sqlFormat.format(parsedDate);
    }

    private String convertToSqlTimeFormat(String time) throws ParseException {
        // Call the method to handle time slot conversion
        String newTime = convertTimeSlot(time); // Get converted time, e.g., "8:00 AM"

        // Parse the time string into a Date object using SimpleDateFormat
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        Date parsedTime = inputFormat.parse(newTime); // Parse the converted time (e.g., "8:00 AM")

        // Format the time back to the 24-hour format (e.g., "08:00" or "16:00")
        SimpleDateFormat sqlFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sqlFormat.format(parsedTime); // Returns time in 24-hour format, e.g., "16:00"
    }

    private String convertTimeSlot(String time) {
        String newTime = time; // Default to original time

        // Check and convert specific time slots to a time without a range
        switch (time) {
            case "8:00-9:00 AM":
                newTime = "8:00 AM";
                break;
            case "9:00-10:00 AM":
                newTime = "9:00 AM";
                break;
            case "10:00-11:00 AM":
                newTime = "10:00 AM";
                break;
            case "11:00-12:00 PM":
                newTime = "11:00 AM";
                break;
            case "1:00-2:00 PM":
                newTime = "1:00 PM";
                break;
            case "2:00-3:00 PM":
                newTime = "2:00 PM";
                break;
            case "3:00-4:00 PM":
                newTime = "3:00 PM";
                break;
            case "4:00-5:00 PM":
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
