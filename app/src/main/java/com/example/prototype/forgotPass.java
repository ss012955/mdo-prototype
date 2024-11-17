package com.example.prototype;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import HelperClasses.NetworkChangeReceiver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class forgotPass extends BaseActivity{
    ImageView umakLogo;
    TextView enterEmail, recoverEmail, createAcc, logIn, emailConfirmationValidator;
    EditText umakEmail;
    Toast toast;
    private String email;
    private FirebaseAuth mAuth;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        umakEmail = findViewById(R.id.etEmail);
        recoverEmail = findViewById(R.id.txtviewRecoverPass);
        createAcc = findViewById(R.id.txtviewCreateAcc);
        logIn = findViewById(R.id.txtviewLogIn);
        emailConfirmationValidator = findViewById(R.id.createAccValidationText);



        logIn.setOnClickListener(v->{
            logIn.setPaintFlags(logIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            logIn();
                });


        createAcc.setOnClickListener(v -> {
            createAcc.setPaintFlags(createAcc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            createAccount();
        });

        recoverEmail.setOnClickListener(v->{sendPasswordResetEmail(); });

    }
    private void sendPasswordResetEmail() {
        String email = umakEmail.getText().toString().trim();

        if (email.isEmpty()) {
            showConfirmation("Please enter your registered email");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showConfirmation("Check your email.");
                    } else {
                        Toast.makeText(forgotPass.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void updatePasswordInDatabase(String email, String newPassword) {
        // The URL of your PHP endpoint that updates the password
        String url = "https://umakmdo-91b845374d5b.herokuapp.com/update_password.php"; // Replace with your PHP endpoint

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create the request body with the parameters (umak_email and new_password)
        RequestBody formBody = new FormBody.Builder()
                .add("umak_email", email)
                .add("new_password", newPassword)
                .build();

        // Create the request object
        Request request = new Request.Builder()
                .url(url)
                .post(formBody) // Send POST request
                .build();

        // Make the network request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (network error)
                runOnUiThread(() -> {
                    Toast.makeText(forgotPass.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // If password updated successfully, notify the user
                    runOnUiThread(() -> {
                        Toast.makeText(forgotPass.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Handle the case where the response was not successful
                    runOnUiThread(() -> {
                        Toast.makeText(forgotPass.this, "Error updating password: " + response.message(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }


    private void showConfirmation(String message) {
        emailConfirmationValidator.setText(message);
        emailConfirmationValidator.setVisibility(View.VISIBLE);
    }
    public void createAccount(){
        Intent intent = new Intent(this, createAcc.class);
        startActivity(intent);
    }

    public void logIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}