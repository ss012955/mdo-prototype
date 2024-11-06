package Database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NetworkUtils {
    public static String performSignup(String studentId, String email, String firstName, String lastName, String password) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://192.168.254.104/MDOapp/signup.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String postData = "student_id=" + URLEncoder.encode(studentId, "UTF-8") +
                    "&umak_email=" + URLEncoder.encode(email, "UTF-8") +
                    "&first_name=" + URLEncoder.encode(firstName, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(lastName, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

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

    public static String performLogin(String studentId, String password) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://127.0.0.1/MDOapp/login.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String postData = "student_id=" + URLEncoder.encode(studentId, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

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
}
