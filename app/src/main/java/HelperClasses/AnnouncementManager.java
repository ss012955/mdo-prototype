package HelperClasses;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

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
import java.util.List;

public class AnnouncementManager {

    public static void fetchAnnouncements(Context context, AnnouncementsCallback callback) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/Admin/announcements.php?type=all";


        SharedPreferences sharedPreferences = context.getSharedPreferences("AnnouncementsPrefs", Context.MODE_PRIVATE);
        String lastFetchedAnnouncements = sharedPreferences.getString("lastFetchedAnnouncements", "");


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                List<AnnouncementsItems> announcementsList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject announcementObject = jsonArray.getJSONObject(i);
                    String title = announcementObject.getString("title");
                    String text = announcementObject.getString("details");
                    String imageUrl = announcementObject.getString("image_url");

                    announcementsList.add(0,new AnnouncementsItems(title, text, imageUrl));

                    // Check if the new announcement is different from the last fetched
                    if (!response.equals(lastFetchedAnnouncements)) {
                        showNewAnnouncementNotification(context,title, text);

                        // Save the new response in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastFetchedAnnouncements", response);
                        editor.apply();
                    }

                }
                List<AnnouncementsItems> latestAnnounceItems = announcementsList.size() > 3 ? announcementsList.subList(0, 3) : announcementsList;
                // Pass the list back to the callback
                callback.onSuccess(latestAnnounceItems);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError("Error parsing announcements data");
            }
        }, error -> {
            callback.onError("Error: " + error.getMessage());
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "ANNOUNCEMENTS_CHANNEL",
                    "Announcements Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Announcements notifications");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private static void showNewAnnouncementNotification(Context context, String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                // Request permission if not granted
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ANNOUNCEMENTS_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Announcement")
                .setContentText(title + "\n" + text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }

    public interface AnnouncementsCallback {
        void onSuccess(List<AnnouncementsItems> announcements);
        void onError(String errorMessage);
    }

}