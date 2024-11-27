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
import java.util.List;

public class TriviaManager {

    public static void fetchTrivia(Context context, TriviaCallback callback) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/trivia.php?type=all";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("Trivia Response", response);
                JSONArray jsonArray = new JSONArray(response);
                List<TriviaItem> triviaItems = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject triviaObject = jsonArray.getJSONObject(i);
                    String title = triviaObject.getString("title");
                    String text = triviaObject.getString("details");
                    triviaItems.add(new TriviaItem(title, "\t" + text));
                }
                callback.onSuccess(triviaItems); // Pass data to callback
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
