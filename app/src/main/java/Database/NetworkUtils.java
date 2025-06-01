package Database;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import com.example.prototype.MainActivity;
import com.example.prototype.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import Database.NetworkUtils;
import HelperClasses.BaseClass;
import HelperClasses.NetworkChangeReceiver;
import HelperClasses.LoginManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NetworkUtils {
    private static FirebaseAuth mAuth;
    static {
        mAuth = FirebaseAuth.getInstance();
    }
    private BaseClass baseClass = new BaseClass();

    public static String performSignup(String studentId, String email, String firstName, String lastName, String password, String role) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://umakmdo-91b845374d5b.herokuapp.com/signup.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String postData = "student_id=" + URLEncoder.encode(studentId, "UTF-8") +
                    "&umak_email=" + URLEncoder.encode(email, "UTF-8") +
                    "&first_name=" + URLEncoder.encode(firstName, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(lastName, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8")+
                    "&role=" + URLEncoder.encode(role, "UTF-8");


            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return response.toString();
    }

    public static String performLoginDB(String umakEmail, String password) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://umakmdo-91b845374d5b.herokuapp.com/login.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "umak_email=" + URLEncoder.encode(umakEmail, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            Log.d("Login", "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d("Login", "Response: " + response.toString());
            } else {
                Log.e("Login", "Server returned error: " + responseCode);
                return "Error: Server returned code " + responseCode;
            }
        } catch (SocketTimeoutException e) {
            Log.e("Login", "Timeout Exception", e);
            return "Error: Server timeout.";
        } catch (Exception e) {
            Log.e("Login", "Exception occurred", e);
            return "Error: " + (e.getMessage() != null ? e.getMessage() : "An unknown error occurred.");
        }
        return response.toString();
    }




    public void showNoConnectionDialog(Context context, final Activity activity) {
        baseClass.showOneButtonDialog(context, "No Internet Connection", "Please check your internet connection.", "Retry", v-> {
            Intent intent = new Intent(context, activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            activity.finish();
        });

    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Check if mobile data is available
    public static boolean isMobileDataAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() &&
                activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }


}
