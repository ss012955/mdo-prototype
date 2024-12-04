package HelperClasses;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateDeleteNotes {

    // Method to update note
    public void updateNote(final String noteId, final String umakEmail, final String title, final String symptoms, final String mood, final String medicine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Prepare URL and connection
                    URL url = new URL("https://umakmdo-91b845374d5b.herokuapp.com/update_note.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Prepare data to send
                    String data = "note_id=" + noteId +
                            "&umak_email=" + umakEmail +
                            "&title=" + title +
                            "&symptoms=" + symptoms +
                            "&mood=" + mood +
                            "&medicine=" + medicine;

                    // Send the request data
                    OutputStream os = connection.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    // Get response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        // Parse the response JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Log.d("UpdateNote", "Note updated successfully!");
                        } else {
                            Log.d("UpdateNote", "Error updating note!");
                        }
                    } else {
                        Log.e("UpdateNote", "Error in response: " + responseCode);
                    }

                } catch (Exception e) {
                    Log.e("UpdateNote", "Error updating note", e);
                }
            }
        }).start();
    }

    // Method to delete note
    public void deleteNote(final String noteId, final String umakEmail) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Prepare URL and connection
                    URL url = new URL("https://umakmdo-91b845374d5b.herokuapp.com/delete_note.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Prepare data to send
                    String data = "note_id=" + noteId + "&umak_email=" + umakEmail;

                    // Send the request data
                    OutputStream os = connection.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    // Get response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        // Parse the response JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Log.d("DeleteNote", "Note deleted successfully!");
                        } else {
                            Log.d("DeleteNote", "Error deleting note!");
                        }
                    } else {
                        Log.e("DeleteNote", "Error in response: " + responseCode);
                    }

                } catch (Exception e) {
                    Log.e("DeleteNote", "Error deleting note", e);
                }
            }
        }).start();
    }
}
