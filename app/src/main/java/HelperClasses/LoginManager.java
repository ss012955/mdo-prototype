package HelperClasses;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.prototype.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;

public class LoginManager implements DefaultLifecycleObserver {
    private final ExecutorService executorService;
    private final Context context;
    private final FirebaseAuth mAuth;


    public LoginManager(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mAuth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth

    }
    public void performLogin(String umakEmail, String password, LoginCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL("http://192.168.100.4/MDOapp-main/login.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "umak_email=" + URLEncoder.encode(umakEmail, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");

                // Handle the response based on status
                if ("success".equals(status)) {
                    ((MainActivity) context).runOnUiThread(() -> {
                        callback.onLoginSuccess();
                    });
                } else {
                    ((MainActivity) context).runOnUiThread(() -> {
                        callback.onLoginFailed(message);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((MainActivity) context).runOnUiThread(() -> {
                    callback.onLoginFailed("Error: " + e.getMessage());
                });
            }
        });
    }

    // Callback interface
    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed(String message);
    }


}
