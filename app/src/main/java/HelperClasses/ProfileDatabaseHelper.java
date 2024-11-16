package HelperClasses;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileDatabaseHelper {

    private Context context;
    private ExecutorService executorService;

    public ProfileDatabaseHelper(Context context) {
        this.context = context;
        // ExecutorService for background thread handling
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void getUserDetails(String userEmail, final UserDetailsCallback callback) {
        // Run the network operation in a background thread
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                User user = fetchUserDetails(userEmail);
                // Send the result back to the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onUserDetailsFetched(user);
                    }
                });
            }
        });
    }

    private User fetchUserDetails(String userEmail) {
        try {
            // URL of your PHP API
            String apiUrl = "https://umakmdo-9d08cdb4c431.herokuapp.com/getUserProfile.php?email=" + userEmail;
            Log.d("API Request", "Fetching user details for email: " + userEmail);

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(apiUrl).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("message")) {
                return null; // Return null if no user found
            }

            String studentId = jsonResponse.getString("student_id");
            String email = jsonResponse.getString("umak_email");
            String firstName = jsonResponse.getString("first_name");
            String lastName = jsonResponse.getString("last_name");

            // Return User object
            return new User(studentId, email, firstName, lastName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Callback interface for returning user details
    public interface UserDetailsCallback {
        void onUserDetailsFetched(User user);
    }

    public class User {
        private String studentId;
        private String email;
        private String firstName;
        private String lastName;

        public User(String studentId, String email, String firstName, String lastName) {
            this.studentId = studentId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}
