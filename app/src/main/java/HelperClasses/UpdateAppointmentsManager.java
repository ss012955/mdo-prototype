package HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prototype.home;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateAppointmentsManager {
    DashboardManager dashboardManager;
    BaseClass baseClass;


    public UpdateAppointmentsManager(){baseClass = new BaseClass();}


    public void bookingDelete(
            Context context,
            String umakEmail,
            String bookingID){

        baseClass.showTwoButtonDialog(
                context, "Confirmation", "Are you sure you want to cancel this booking?", "YES", "NO",
                v->{
                    delete(context, umakEmail, bookingID);

                },
                v->{

                }
        );

    }

    private void delete(Context context, String umakEmail, String bookingID) {
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/delete_booking.php"; // Replace with your PHP endpoint

        // Create a Volley request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success) {
                            baseClass.showOneButtonDialog(context, "Confirmation", "Booking cancelled successfully.\n Please wait for email confirmation. Thank you", "Confirm",
                                    v2->{
                                        Intent i = new Intent(context, home.class);
                                        context.startActivity(i);
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("umak_email", umakEmail);
                params.put("booking_id", bookingID);
                return params;
            }
        };

        // Add the request to the Volley queue
        Volley.newRequestQueue(context).add(stringRequest);
    }




}
