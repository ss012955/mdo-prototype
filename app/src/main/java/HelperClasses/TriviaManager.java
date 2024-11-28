package HelperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

                    triviaItems.add(0,new TriviaItem(title, "\t" + text));
                }

                // Get the two trivia items with the highest IDs
                List<TriviaItem> latestTriviaItems = triviaItems.size() > 2 ? triviaItems.subList(0, 2) : triviaItems;

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


    // Define a callback interface
    public interface TriviaCallback {
        void onSuccess(List<TriviaItem> triviaItems);
        void onError(String errorMessage);
    }


}

