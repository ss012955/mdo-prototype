package HelperClasses;
import android.content.Context;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryManager {
    public interface HistoryCallback {
        void onSuccess(List<HistoryItem> historyItems);
        void onError(String errorMessage);
    }
    public static void fetchHistory(String url, String userEmail, List<HistoryItem> historyList,
                                    RecyclerView.Adapter adapter, Context context, HistoryCallback callback) {
        url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_bookings.php";
        String fullUrl = url + "?user_email=" + userEmail;
        new Thread(() -> {
            try {
                // Open connection to the server
                HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
                connection.setRequestMethod("GET");
                // Read the response from the server
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                List<HistoryItem> fetchedHistoryItems = new ArrayList<>();
                // Loop through the JSON array and create HistoryItem objects
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject booking = jsonArray.getJSONObject(i);
                    String serviceType = booking.getString("service_type");
                    String bookingDate = booking.getString("booking_date");
                    String bookingTime = booking.getString("booking_time");
                    String remarks = booking.getString("remarks");
                    String status = booking.getString("status");
                    // Combine details into a single string for display
                    String details = "Service: " + serviceType + "\n" +
                            "Date: " + bookingDate + "\n" +
                            "Time: " + bookingTime + "\n" +
                            "Remarks: " + remarks + "\n" +
                            "Status: " + status;
                    // Add to the history list
//                    fetchedHistoryItems.add(new HistoryItem(serviceType, details));
                }
                // Return the fetched history items via the callback
                callback.onSuccess(fetchedHistoryItems);
                adapter.notifyDataSetChanged();;
            } catch (Exception e) {
                // Log and pass error to the callback
                Log.e("HistoryManager", "Error fetching history", e);
                callback.onError("Error fetching history: " + e.getMessage());
            }
        }).start();
    }
    public static void fetchHistoryWithTitleAndDate(String url, String userEmail, List<HistoryItem> historyList,
                                                    RecyclerView.Adapter adapter, Context context, HistoryCallback callback) {
        url = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_booking_completed.php";
        String fullUrl = url + "?user_email=" + userEmail;
        new Thread(() -> {
            try {
                // Open connection to the server
                HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
                connection.setRequestMethod("GET");
                // Read the response from the server
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                List<HistoryItem> fetchedHistoryItems = new ArrayList<>();
                // Create a SimpleDateFormat to format the date
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // assuming the date format from the server is yyyy-MM-dd
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM. dd, yyyy", Locale.US); // desired format: Nov. 20, 2024
                // Loop through the JSON array and create HistoryItem objects
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject booking = jsonArray.getJSONObject(i);
                    int bookingId = booking.getInt("booking_id");  // Corrected variable name
                    String serviceType = booking.getString("service_type"); // Title
                    String bookingDate = booking.getString("booking_date"); // Date
                    // Parse the date from the JSON response and format it
                    Date date = inputDateFormat.parse(bookingDate);
                    String formattedDate = outputDateFormat.format(date);
                    // Combine title and formatted date into a single string for display
                    String details = serviceType + " - " + formattedDate;
                    // Add to the history list
                    fetchedHistoryItems.add(new HistoryItem(bookingId, serviceType, formattedDate));
                }
                // Return the fetched history items via the callback
                callback.onSuccess(fetchedHistoryItems);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                // Log and pass error to the callback
                Log.e("HistoryManager", "Error fetching history", e);
                callback.onError("Error fetching history: " + e.getMessage());
            }
        }).start();
    }
}