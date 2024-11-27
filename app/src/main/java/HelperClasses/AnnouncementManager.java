package HelperClasses;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {

    public static void fetchAnnouncements(Context context, AnnouncementsCallback callback) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/Admin/announcements.php?type=all";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                List<AnnouncementsItems> announcementsList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject announcementObject = jsonArray.getJSONObject(i);
                    String title = announcementObject.getString("title");
                    String text = announcementObject.getString("details");
                    String imageUrl = announcementObject.getString("image_url");

                    announcementsList.add(new AnnouncementsItems(title, text, imageUrl));
                }

                // Pass the list back to the callback
                callback.onSuccess(announcementsList);
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

    public interface AnnouncementsCallback {
        void onSuccess(List<AnnouncementsItems> announcements);
        void onError(String errorMessage);
    }

}
