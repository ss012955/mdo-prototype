package HelperClasses;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prototype.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class BookingManager {

    private String selectedTimeSlot;
    private Date selectedDate;

    public void showTimeSlotDialog(Context context, OnTimeSlotSelectedListener listener, String selectedDate) {
        // Inflate the dialog layout
        View dialogView = View.inflate(context, R.layout.dialog_time, null);

        TextView[] timeSlots = {
                dialogView.findViewById(R.id.timeSlot1),
                dialogView.findViewById(R.id.timeSlot2),
                dialogView.findViewById(R.id.timeSlot3),
                dialogView.findViewById(R.id.timeSlot4),
                dialogView.findViewById(R.id.timeSlot5),
                dialogView.findViewById(R.id.timeSlot6),
                dialogView.findViewById(R.id.timeSlot7),
                dialogView.findViewById(R.id.timeSlot8)
        };

        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        final String[] selectedTime = {""};

        // Convert the time slots to 24-hour format (HH:mm) and check availability
        for (TextView timeSlot : timeSlots) {
            String time = timeSlot.getText().toString();

            // Convert the time slot to SQL compatible time format (HH:mm)
            String sqlTime = convertToSqlTimeFormat(time);

            // Pass the TextView and SQL formatted time to the availability checker
            checkTimeSlotAvailability(context, selectedDate, sqlTime, isAvailable -> {
                if (!isAvailable) {
                    timeSlot.setTextColor(ContextCompat.getColor(context, R.color.red));
                    timeSlot.setClickable(false);
                } else {
                    timeSlot.setBackgroundResource(android.R.color.transparent);
                    timeSlot.setClickable(true);
                }
            });
        }

        // Slot click listener
        View.OnClickListener slotClickListener = view -> {
            for (TextView slot : timeSlots) {
                slot.setBackgroundResource(android.R.color.transparent); // Reset background
            }
            view.setBackgroundResource(R.color.gray); // Highlight selected slot
            selectedTime[0] = ((TextView) view).getText().toString();
        };

        for (TextView timeSlot : timeSlots) {
            timeSlot.setOnClickListener(slotClickListener);
        }

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Confirm button action
        confirmButton.setOnClickListener(v -> {
            if (!selectedTime[0].isEmpty()) {
                selectedTimeSlot = selectedTime[0];
                listener.onTimeSlotSelected(selectedTimeSlot); // Notify listener
            } else {
                Toast.makeText(context, "Please select a time.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    private void checkTimeSlotAvailability(Context context, String date, String time, OnAvailabilityCheckListener listener) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/check_availability.php";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("booking_date", date);
            requestBody.put("booking_time", time); // Send time in SQL compatible format
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        Log.d("BookingManager", "Time slot: " + time + " | Status: " + status);
                        listener.onCheckComplete("available".equals(status));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onCheckComplete(false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onCheckComplete(false);
                });

        Volley.newRequestQueue(context).add(request);
    }

    // Convert AM/PM time to SQL-compatible HH:mm format
    private String convertToSqlTimeFormat(String time) {
        // Call the method to handle time slot conversion (e.g., 8:00-9:00 AM -> 08:00)
        String newTime = convertTimeSlot(time);

        // Parse the time string into a Date object using SimpleDateFormat
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        Date parsedTime;
        try {
            parsedTime = inputFormat.parse(newTime); // Parse the converted time (e.g., "8:00 AM")
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // If parsing fails, return an empty string
        }

        // Format the time back to the 24-hour format (HH:mm)
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

    public interface OnAvailabilityCheckListener {
        void onCheckComplete(boolean isAvailable);
    }



    // Method for handling date selection
    public Date handleCalendarDateSelection(MaterialCalendarView calendarView, Context context) {
        // Set the OnDateChangedListener for MaterialCalendarView
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = null; // Reset the selectedDate to null initially

                // Create a Calendar instance with the selected date
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(date.getYear(), date.getMonth() - 1, date.getDay()); // Month is 0-based

                // Get today's date to compare
                Calendar today = Calendar.getInstance();

                // Check if the selected date is in the past
                if (selectedCalendar.before(today)) {
                    Toast.makeText(context, "Please select a future date.", Toast.LENGTH_SHORT).show();
                    return; // Exit if the date is in the past
                }

                // Check if the selected date is today
                if (selectedCalendar.equals(today)) {
                    Toast.makeText(context, "Please select a future date.", Toast.LENGTH_SHORT).show();
                    return; // Exit if the date is today
                }

                // Get the user email from SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String userEmail = prefs.getString("user_email", "No email found");

                // Check if the user already has a booking on the selected date
                hasBookingOnDate(userEmail, selectedCalendar, context).thenAccept(hasBooking -> {
                    if (hasBooking) {
                        // If the user already has a booking, reset selectedDate and prompt for another date
                        Toast.makeText(context, "You already have a booking on this date. Choose another date or cancel/reschedule your current booking.", Toast.LENGTH_LONG).show();

                        // Reset the selectedDate to null
                        selectedDate = null;
                    }
                }).exceptionally(throwable -> {
                    // Handle errors when checking bookings
                    Toast.makeText(context, "Error checking bookings: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                });

                // Check if the selected date is a weekend (Saturday or Sunday)
                int selectedDayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);
                if (selectedDayOfWeek == Calendar.SATURDAY || selectedDayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Weekends are not available, please choose a weekday.", Toast.LENGTH_SHORT).show();
                    return; // Exit if the date is a weekend
                }

                // Update the selectedDate only if it's valid (i.e., not already booked and not a weekend)
                if (selectedDate == null) {
                    selectedDate = selectedCalendar.getTime();
                }
            }
        });

        return selectedDate; // Return the selected date (or null if no date is selected yet)
    }

    public CompletableFuture<Boolean> hasBookingOnDate(String userEmail, Calendar selectedCalendar, Context context) {
        // Format the selected date in YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(selectedCalendar.getTime()); // Format the selected date

        // Construct the URL
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/check_samebooking.php?date=" + selectedDate + "&email=" + userEmail;

        // Create a CompletableFuture to handle the result
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        // Run the network request asynchronously
        new Thread(() -> {
            try {
                // Create the URL object
                URL requestUrl = new URL(url);

                // Open the connection
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // Set timeout
                connection.setReadTimeout(5000);

                // Read the response
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse the response
                JSONObject responseJson = new JSONObject(response.toString());
                boolean bookingFound = responseJson.getBoolean("booking_found");


                // Show the Toast in the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (bookingFound) {
                        resultFuture.complete(bookingFound);
                    } else {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                resultFuture.completeExceptionally(e); // Handle any exceptions
            }
        }).start();

        return resultFuture; // Return the CompletableFuture
    }





    public void fetchBookingsForUser(String userEmail, MaterialCalendarView selectedCalendar, Context context) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_bookings.php";

        // Make the request to fetch the bookings
        AppointmentsManager manager = new AppointmentsManager(context);
        manager.fetchAppointments(url, userEmail, new ArrayList<>(), new ArrayList<>(), null, context, new AppointmentsManager.AppointmentsCallback() {
            @Override
            public void onAppointmentsFetched(List<AppointmentsClass> fetchedAppointments, List<AppointmentDaysClass> fetchedDays) {
                // Iterate through the fetched appointments and check if the selected date matches any booking
                for (AppointmentsClass appointment : fetchedAppointments) {
                    // Extract the date portion from the dateTime (assuming it's in "yyyy-MM-dd HH:mm:ss" format)
                    String bookingDateStr = appointment.getDateTime();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date bookingDate = sdf.parse(bookingDateStr);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Error fetching bookings: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Helper method to compare if two dates are the same day
    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).equals(sdf.format(date2));
    }


    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(String timeSlot);
    }

    public static void smoothProgressUpdate(ProgressBar progressBar, int targetProgress) {
        // Ensure the target progress is within valid bounds
        final int finalTargetProgress = Math.max(0, Math.min(targetProgress, progressBar.getMax()));

        final int[] currentProgress = {progressBar.getProgress()}; // Start from current progress

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentProgress[0] < finalTargetProgress) {
                    currentProgress[0] += 3; // Increment by 1 for smoother animation
                    progressBar.setProgress(currentProgress[0]); // Update progress
                    handler.postDelayed(this, 15); // Short delay for smooth effect
                } else {
                    // Ensure the progress stops exactly at target
                    progressBar.setProgress(finalTargetProgress);
                }
            }
        }, 15);
    }

    public void animateProgressBar(ProgressBar progressBar, int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(700); // Smooth animation duration
        animation.start();
    }
}