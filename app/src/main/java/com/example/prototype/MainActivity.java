package com.example.prototype;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import Database.NetworkUtils;
import HelperClasses.NetworkChangeReceiver;
import HelperClasses.LoginManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends BaseActivity {
    private LoginManager loginManager;
    private TextView createAcc, forgotPass, incorrect;
    private EditText umakEmail, password;
    private Button logIn;
    private NetworkChangeReceiver networkChangeReceiver;
    private Handler handler = new Handler();
    private NetworkUtils networkUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        // Initialize views
        createAcc = findViewById(R.id.createAccount);
        forgotPass = findViewById(R.id.forgotPassword);
        umakEmail = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        logIn = findViewById(R.id.btnLogIn);
        incorrect = findViewById(R.id.incorrectUNPass);
        incorrect.setVisibility(View.GONE);

        loginManager = new LoginManager(this);
        getLifecycle().addObserver(loginManager);

        createAcc.setOnClickListener(v -> startActivity(new Intent(this, createAcc.class).putExtra("isCreate", true)));
        forgotPass.setOnClickListener(v -> startActivity(new Intent(this, forgotPass.class)));

        logIn.setOnClickListener(v -> attemptLogin());

        networkUtils = new NetworkUtils();

        //Authentication
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, home.class));
            finish();  // Close MainActivity to prevent going back
            return;
        }
    }

    private void attemptLogin() {
        String emailInput = umakEmail.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            showToast("Please enter both username and password");
        } else if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            showError("No internet connection.");
        } else {
            loginManager.performLogin(emailInput, passwordInput, new LoginManager.LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    showToast("Login Successful");
                    SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("user_email", emailInput);  // Store user email or other data
                    editor.apply();

                    startActivity(new Intent(MainActivity.this, home.class));
                    finish();
                }

                @Override
                public void onLoginFailed(String message) {
                    if (NetworkUtils.isMobileDataAvailable(MainActivity.this)) {
                        showError("No internet connection.");
                    } else {
                        showError(message);
                    }


                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        incorrect.setText(message);
        incorrect.setVisibility(View.VISIBLE);
    }




}
