package HelperClasses;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesDatabaseHelper {

    // URL of the PHP script
    private static final String INSERT_NOTE_URL = "https://umakmdo-91b845374d5b.herokuapp.com/insert_notes.php"; // Replace with your actual PHP endpoint

    // ExecutorService for running background tasks
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Inserts a note into the database by calling the PHP script.
     *
     * @param email     The user's email (umak_email).
     * @param title     The title of the note.
     * @param symptoms  The symptoms described in the note.
     * @param mood      The mood associated with the note.
     * @param medicine  The medicine mentioned in the note.
     * @param datetime  The creation date and time in "YYYY-MM-DD HH:mm:ss" format.
     */
    public void insertNote(String email, String title, String symptoms, String mood, String medicine, String datetime) {
        executorService.execute(() -> {
            try {
                // Establish a connection to the server
                URL url = new URL(INSERT_NOTE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Write the POST data
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData = URLEncoder.encode("umak_email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") + "&" +
                        URLEncoder.encode("symptoms", "UTF-8") + "=" + URLEncoder.encode(symptoms, "UTF-8") + "&" +
                        URLEncoder.encode("mood", "UTF-8") + "=" + URLEncoder.encode(mood, "UTF-8") + "&" +
                        URLEncoder.encode("medicine", "UTF-8") + "=" + URLEncoder.encode(medicine, "UTF-8") + "&" +
                        URLEncoder.encode("created_at", "UTF-8") + "=" + URLEncoder.encode(datetime, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                outputStream.close();

                // Get the server response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("NotesDatabaseHelper", "Note inserted successfully!");
                } else {
                    Log.e("NotesDatabaseHelper", "Error: Server responded with code " + responseCode);
                }

            } catch (Exception e) {
                Log.e("NotesDatabaseHelper", "Error inserting note: " + e.getMessage());
            }
        });
    }
}