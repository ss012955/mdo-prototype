package HelperClasses;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prototype.ConfirmationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingUpdate {
    private BaseClass baseClass;

    public BookingUpdate() {
        baseClass = new BaseClass(); // Initialize BaseClass here
    }

    public void bookingUpdate(
            Context context,
            String service,
            String bookingID,
            String userEmail,
            String date,
            String time,
            String remarks,
            Runnable onComplete) {

        baseClass.showTwoButtonDialog(
                context,
                "Confirmation",
                "Confirm updating this booking?",
                "Yes",
                "No",
                v -> {
                    String newTime = convertTimeSlot(time);
                    update(context, bookingID, userEmail, date, newTime, remarks,()->{
                        // Only navigate to the ConfirmationActivity if the insert is successful
                        Intent intent = new Intent(context, ConfirmationActivity.class);
                        intent.putExtra("ServiceType", service);
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

    private void update(Context context, String bookingID, String userEmail, String date, String time, String remarks, Runnable onComplete) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_booking.php";


        try {
            String formattedDate = convertToSqlDateFormat(date);
            String formattedTime = convertToSqlTimeFormat(time);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(context, "Booking updated successfully!", Toast.LENGTH_SHORT).show();
                        if (onComplete != null) onComplete.run();
                    },
                    error -> {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        if (onComplete != null) onComplete.run();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("booking_id", bookingID);
                    params.put("umak_email", userEmail);
                    params.put("booking_date", formattedDate);
                    params.put("booking_time", formattedTime); // Pass the time as is
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
        // Parse the time string into a Date object using SimpleDateFormat
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        Date parsedTime = inputFormat.parse(time); // Parse the converted time (e.g., "4:00 PM")

        // Format the time back to the 24-hour format (e.g., "16:00")
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
                newTime = "4:00 PM";  // Ensure this conversion is correct
                break;
            default:
                // If the time doesn't match a specific slot, leave it unchanged
                Log.w("BookingInsert", "Unexpected time format: " + time);
                break;
        }

        return newTime;
    }


}
