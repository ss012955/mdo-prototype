package HelperClasses;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;

import com.example.prototype.MainActivity;

public class UpdateProfileManager {

    private BaseClass baseClass = new BaseClass();

    public UpdateProfileManager() {
        this.baseClass = baseClass;
    }

    public void updateUserProfile(Context context, Activity activity, String userEmail,
                                  EditText etFirstname, EditText etLastname,
                                  EditText etPassword, EditText etConfirmPassword) {
        // URL of your Heroku PHP script
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_profile.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle Heroku server response
                    if (!etPassword.getText().toString().isEmpty() &&
                            etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                        updateFirebasePassword(context,userEmail, etPassword.getText().toString());
                    } else {
                        Toast.makeText(context, "Update Successful (No Password Change)", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle errors from Heroku
                    Toast.makeText(context, "Update Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Include the email as it's required for identification
                params.put("email", userEmail);

                // Only include fields with updated values
                if (!etFirstname.getText().toString().isEmpty()) {
                    params.put("first_name", etFirstname.getText().toString());
                }
                if (!etLastname.getText().toString().isEmpty()) {
                    params.put("last_name", etLastname.getText().toString());
                }
                if (!etPassword.getText().toString().isEmpty() &&
                        etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    params.put("password", etPassword.getText().toString());
                }

                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void updateFirebasePassword(Context context, String email, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Prompt user for current password
            promptForCurrentPassword(context, user, newPassword, currentPassword -> {
                // Re-authenticate with the provided password
                user.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update password in Firebase
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(context, "Password Updated in Firebase", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed to Update Password in Firebase", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(context, "Re-authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        } else {
            Toast.makeText(context, "User Not Logged In", Toast.LENGTH_SHORT).show();
        }
    }


    public void showSaveValidator(Context context, final Activity activity, String userEmail,
                                  EditText etFirstname, EditText etLastname,
                                  EditText etPassword, EditText etConfirmPassword) {

        baseClass.showTwoButtonDialog(context, "Save", "Are you sure you want to save changes?", "SAVE", "CANCEL",
                v -> {
                    // Save changes
                    updateUserProfile(context, activity, userEmail, etFirstname, etLastname, etPassword, etConfirmPassword);
                },
                v -> {
                    // Do nothing on cancel
                });
    }
    private void promptForCurrentPassword(Context context, FirebaseUser user, String newPassword, UpdatePasswordCallback callback) {
        // Inflate custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_passwordprompt, null);
        builder.setView(dialogView);

        // Find and customize dialog components
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        EditText inputPassword = dialogView.findViewById(R.id.edit_text_password); // Get the EditText for password input
        Button positiveButton = dialogView.findViewById(R.id.positive_button);
        Button negativeButton = dialogView.findViewById(R.id.negative_button);

        // Set custom dialog text
        title.setText("Re-authentication");
        message.setText("Please enter your current password:");

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Set button click listeners
        positiveButton.setText("CONFIRM");
        positiveButton.setOnClickListener(v -> {
            String currentPassword = inputPassword.getText().toString().trim();
            if (!currentPassword.isEmpty()) {
                callback.onPasswordProvided(currentPassword);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        negativeButton.setText("CANCEL");
        negativeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }



    interface UpdatePasswordCallback {
        void onPasswordProvided(String currentPassword);
    }

}
