package HelperClasses;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prototype.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TriviaManager {

    public static void fetchTrivia(Context context, TriviaCallback callback) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/trivia.php?type=all";

        SharedPreferences sharedPreferences = context.getSharedPreferences("TriviaPrefs", Context.MODE_PRIVATE);
        String lastFetchedTrivia = sharedPreferences.getString("lastFetchedTrivia", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("Trivia Response", response);
                JSONArray jsonArray = new JSONArray(response);
                List<TriviaItem> triviaItems = new ArrayList<>();

                // Loop through the response and add trivia items
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject triviaObject = jsonArray.getJSONObject(i);
                    String title = triviaObject.getString("title");
                    String text = triviaObject.getString("details");

                    triviaItems.add(0, new TriviaItem(title, "\t" + text));


                    // Check if this trivia is different from the last fetched trivia
                    if (!response.equals(lastFetchedTrivia)) {
                        // New trivia, show the notification
                        showNewTriviaNotification(context, title, text);

                        // Save the new trivia in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastFetchedTrivia", response); // Store the new trivia
                        editor.apply();
                    }
                }

                // Get the two trivia items with the highest IDs
                List<TriviaItem> latestTriviaItems = triviaItems.size() > 3 ? triviaItems.subList(0, 3) : triviaItems;

                // Pass the two most recent trivia items to the callback
                callback.onSuccess(latestTriviaItems);

            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError("Error parsing trivia data");
            }
        }, error -> {
            String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
            callback.onError(errorMessage);
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context); // Pass Context here
        requestQueue.add(stringRequest);
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TRIVIA_CHANNEL",
                    "Trivia Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Trivia notifications");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void showNewTriviaNotification(Context context, String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                // Prompt the user for permission
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TRIVIA_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Trivia Added")
                .setContentText(title + "\n" + text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }

    // Define a callback interface
    public interface TriviaCallback {
        void onSuccess(List<TriviaItem> triviaItems);
        void onError(String errorMessage);
    }
}

