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

public class createAcc extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    TextView txtviewLogIn, createAccValidation;
    EditText etEmail, etStudentId, etFirstName, etLastName, etPassword, etconfirmPass;
    Button signUp;
    Toast toast;
    private NetworkChangeReceiver networkChangeReceiver;
    public SignupManager signupManager;
    private NetworkUtils networkUtils = new NetworkUtils();


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

        signUp.setOnClickListener(v-> { attempSignup();});

        signupManager = new SignupManager(this);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        getLifecycle().addObserver(signupManager);

    }

    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }    }

    public void onNetworkChange(boolean isConnected) {
        if (!isConnected) networkUtils.showNoConnectionDialog(this, createAcc.this);
    }


    public void LogIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void attempSignup(){
        String studentId = etStudentId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etconfirmPass.getText().toString().trim();

        if(studentId.isEmpty()|| email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()){
            showError("Please input the required field.",Color.RED);
        }else if(!NetworkUtils.isNetworkAvailable(createAcc.this)){
            showError("No internet connection.",Color.RED);
        }else if(!password.equals(confirmPass)) {
            showError("Password do not match",Color.RED);
        }else if (!isValidUmakEmail(email)) {
            showError("Use valid UMAK email.", Color.RED);
        }else {
            signupManager.performSignup(studentId, email, firstName, lastName, password, new SignupManager.SignUpCallBack(){

                @Override
                public void onSignupSuccess() {
                    showError("Registration Successful!\nPlease check your email", Color.GREEN);

                }

                @Override
                public void onSignupFailed() {
                    showError(NetworkUtils.isMobileDataAvailable(createAcc.this) ? "No internet connection." : "Registration Failed.", Color.RED);
                    startActivity(new Intent(createAcc.this, home.class));

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