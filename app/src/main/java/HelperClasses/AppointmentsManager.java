package HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Singleton.allAppointments;

public class AppointmentsManager {
    private Context context;
    private HashSet<CalendarDay> appointmentsDays = new HashSet<>();
    private HashSet<CalendarDay> approvedDays = new HashSet<>();
    private HashSet<CalendarDay> pendingDays = new HashSet<>();
    CalendarDay calendarDay;

    public AppointmentsManager() {
    }

    public AppointmentsManager(Context context) {

    }

    public void fetchAppointments(String url, String userEmail, List<AppointmentsClass> appointmentsList,List<AppointmentDaysClass> appointmentDaysClasses,
                                  RecyclerView.Adapter adapter, Context context, AppointmentsCallback callback) {
        // Build the full URL with query parameter
        String fullUrl = url + "?user_email=" + userEmail;

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                StringBuilder response = new StringBuilder();

                try {
                    // Open the connection
                    URL requestUrl = new URL(fullUrl);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // Optional timeout
                    connection.setReadTimeout(5000); // Optional timeout
                    connection.connect();

                    // Check the response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                        }

                        // Parse JSON response
                        JSONArray responseArray = new JSONArray(response.toString());

                        // Update the UI with fetched data
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    appointmentsList.clear();  // Clear existing data
                                    appointmentDaysClasses.clear();
                                    // Loop through the JSON array and parse each appointment
                                    for (int i = 0; i < responseArray.length(); i++) {
                                        JSONObject booking = responseArray.getJSONObject(i);

                                        String bookingID = booking.getString("booking_id");
                                        String service = booking.getString("service_type");
                                        String rawDate = booking.getString("booking_date");
                                        String rawTime = booking.getString("booking_time");
                                        String remarks = booking.getString("remarks");
                                        String status = booking.getString("status");

                                        // Format the date and time
                                        String formattedDateTime = formatDateTime(rawDate, rawTime);

                                        String appointNumber = "Appointment " + (i + 1); // Incremented appointment number
                                        // Create and add the appointment to the list
                                        AppointmentsClass appointment = new AppointmentsClass(bookingID, status, appointNumber, service, formattedDateTime, remarks);
                                        appointmentsList.add(appointment);

                                        int numberOfAppointments = appointmentsList.size();
                                        String appointmentText = "You have " + numberOfAppointments + " appointments.";

                                        if (rawDate != null) {
                                            // Parse the date into a CalendarDay object
                                            String[] dateParts = rawDate.split("-"); // Assuming "YYYY-MM-DD" format
                                            int year = Integer.parseInt(dateParts[0]);
                                            int month = Integer.parseInt(dateParts[1]) ; // CalendarDay uses 0-based months
                                            int day = Integer.parseInt(dateParts[2]);
                                            calendarDay = CalendarDay.from(year, month, day);

                                            AppointmentDaysClass appointmentDaysClass = new AppointmentDaysClass(calendarDay.toString(), status);
                                            appointmentDaysClasses.add(appointmentDaysClass);
                                            if(status.equals("Approved")){
                                                approvedDays.add(calendarDay);
                                            }
                                            else if(status.equals("Pending")){
                                                pendingDays.add(calendarDay);
                                            }else if(status.equals("Cancelled")){
                                                appointmentsDays.add(calendarDay);
                                            } else if ( status.equals("No Show") ) {
                                                appointmentsDays.add(calendarDay);
                                            } else if (status.equals("Completed")) {
                                                appointmentsDays.add(calendarDay);
                                            }


                                        }

                                        MaterialCalendarView calendarView = ((Activity) context).findViewById(R.id.calendarView);
                                        Set<CalendarDay> combinedDates = new HashSet<>(approvedDays);
                                        calendarView.addDecorator(new EventDecoratorApproved(combinedDates));

                                        Set<CalendarDay> combinedPendingDates = new HashSet<>(pendingDays);
                                        calendarView.addDecorator(new EventDecoratorPending(combinedPendingDates));

                                    }
                                    Log.d("AppointmentsDebug", "Applying decorator with dates: " + appointmentsDays);

                                    // Notify the callback
                                    callback.onAppointmentsFetched(appointmentsList, appointmentDaysClasses);
                                    // Notify the adapter to update the RecyclerView
                                    adapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    Log.d("context", "Error parsing data: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        // Handle non-OK response code
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Error: HTTP " + responseCode, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    // Handle connection or parsing errors
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(context, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } finally {
                    if (connection != null) {
                        connection.disconnect();  // Close the connection
                    }
                }
            }
        }).start();  // Start the network operation in a new thread
    }


    private String formatDateTime(String rawDate, String rawTime) {
        try {
            // Parse the date
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = inputDateFormat.parse(rawDate);

            // Format the date
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            String formattedDate = outputDateFormat.format(date);

            // Parse the time
            SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            Date time = inputTimeFormat.parse(rawTime);

            // Format the time
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            String formattedTime = outputTimeFormat.format(time);

            // Combine date and time
            return formattedDate + " / " + formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date/Time";
        }
    }

    public interface AppointmentsCallback {
        void onAppointmentsFetched(List<AppointmentsClass> fetchedList, List<AppointmentDaysClass> fetchedDays);
        void onError(String errorMessage);
    }


    public void fetchAllAppointments(String userEmail, final Runnable callback) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/fetchAll_bookings.php";
        String fullUrl = url + "?user_email=" + userEmail;

        // Start a background thread to handle the network operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create the URL object
                    URL urlObj = new URL(fullUrl);

                    // Set up the connection
                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000); // Timeout 5 seconds
                    connection.setReadTimeout(3000); // Timeout 5 seconds

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();


                    // Parse the response (assuming it's just a number)
                    final int numberOfAppointments = Integer.parseInt(response.toString());
                    // Store the result in the AppointmentData singleton
                    allAppointments.getInstance().setNumberOfAppointments(numberOfAppointments);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.run(); // This will notify the adapter to update
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle errors like connection issues
                }
            }
        }).start();
    }
    // Callback interface for dashboard data

    // Listener interface for fetching appointments
    public interface AppointmentsFetchListener {
        void onAppointmentsFetched(int numberOfAppointments);
    }


}