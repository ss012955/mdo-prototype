package HelperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.prototype.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchImageTask {

    private static final String BASE_URL = "https://umakmdo-91b845374d5b.herokuapp.com/fetch_profileImage.php?umak_email=";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void fetchImage(String email, ImageView imageView) {
        executor.execute(() -> {
            Bitmap bitmap = null;
            String errorMessage = null;
            try {
                String urlString = BASE_URL + email;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(result.toString());
                    if (jsonResponse.has("error")) {
                        errorMessage = jsonResponse.getString("error");
                    } else if (jsonResponse.has("image")) {
                        String base64Image = jsonResponse.getString("image");
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    }
                } else {
                    errorMessage = "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "An error occurred while fetching the image.";
            }

            // On the main thread, update the UI
            Bitmap finalBitmap = bitmap;
            String finalErrorMessage = errorMessage;
            handler.post(() -> {
                if (finalBitmap != null) {
                    imageView.setImageBitmap(finalBitmap);
                } else {
                    // Handle error, e.g., show a placeholder image
                    imageView.setImageResource(R.drawable.profile);
                    if (finalErrorMessage != null) {
                        Toast.makeText(imageView.getContext(), finalErrorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
