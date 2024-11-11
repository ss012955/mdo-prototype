package com.example.prototype;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;
import HelperClasses.NetworkChangeReceiver;
import HelperClasses.SignupManager;

public class createAcc extends BaseActivity {
    TextView txtviewLogIn, createAccValidation;
    EditText etEmail, etStudentId, etFirstName, etLastName, etPassword, etconfirmPass;
    Button signUp;
    Toast toast;
    public SignupManager signupManager;
    private NetworkUtils networkUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_acc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etStudentId = findViewById(R.id.etStudentId);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        etconfirmPass = findViewById(R.id.etconfirmPass);
        signUp = findViewById(R.id.btnSignUp);
        createAccValidation = findViewById(R.id.createAccValidationText);
        txtviewLogIn = findViewById(R.id.txtviewLogIn); // Ensure this line is added

       txtviewLogIn.setOnClickListener(v->{LogIn();});

        signUp.setOnClickListener(v-> { attemptSignup();});

        signupManager = new SignupManager(this);
        getLifecycle().addObserver(signupManager);
        networkUtils = new NetworkUtils();
    }

    public void LogIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void attemptSignup(){
        String studentId = etStudentId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etconfirmPass.getText().toString().trim();

        if (studentId.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            showError("Please input the required field.", Color.RED);
        } else if (!NetworkUtils.isNetworkAvailable(createAcc.this)) {
            // Check if mobile data is available but not connected
            if (NetworkUtils.isMobileDataAvailable(createAcc.this)) {
                showError("No connection. Please check your mobile data network.", Color.RED);
            } else {
                showError("No internet connection.", Color.RED);
            }
        } else if (!password.equals(confirmPass)) {
            showError("Passwords do not match.", Color.RED);
        } else if (!isValidUmakEmail(email)) {
            showError("Use a valid UMAK email.", Color.RED);
        } else {
            SignupManager.performSignupWithFirebase(this, studentId, email, firstName, lastName, password, new SignupManager.SignUpCallBack() {
                @Override
                public void onSignupSuccess() {
                    showError("Registration Successful! Please check your email.", Color.GREEN);
                }

                @Override
                public void onSignupFailed(String message) {
                        if(NetworkUtils.isMobileDataAvailable(createAcc.this)){
                            showError("No connection. Please check your mobile data network.", Color.RED);
                        }else{
                            showError(message, Color.RED);
                        }
                }
            });
        }
    }

    private boolean isValidUmakEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@umak\\.edu\\.ph");
    }


    private void showError(String message, int color) {
        createAccValidation.setText(message);
        createAccValidation.setTextColor(color);
        createAccValidation.setVisibility(View.VISIBLE);
    }



}