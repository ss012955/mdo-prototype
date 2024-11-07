package com.example.prototype;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import HelperClasses.NetworkChangeReceiver;
import HelperClasses.LoginManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private LoginManager loginManager;
    private TextView createAcc, forgotPass, incorrect;
    private EditText username, password;
    private Button logIn;
    private NetworkChangeReceiver networkChangeReceiver;
    private Handler handler = new Handler();
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
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        logIn = findViewById(R.id.btnLogIn);
        incorrect = findViewById(R.id.incorrectUNPass);
        incorrect.setVisibility(View.GONE);

        loginManager = new LoginManager(this);
        getLifecycle().addObserver(loginManager);

        createAcc.setOnClickListener(v -> startActivity(new Intent(this, createAcc.class).putExtra("isCreate", true)));
        forgotPass.setOnClickListener(v -> startActivity(new Intent(this, forgotPass.class)));

        logIn.setOnClickListener(v -> attemptLogin());

        networkChangeReceiver = new NetworkChangeReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (!isConnected) showNoConnectionDialog();
    }

    private void showNoConnectionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.retry_button).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void attemptLogin() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            showToast("Please enter both username and password");
        } else if (!isNetworkAvailable()) {
            showError("No internet connection.");
        } else {
            loginManager.performLogin(usernameInput, passwordInput, new LoginManager.LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    showToast("Login Successful");
                    startActivity(new Intent(MainActivity.this, home.class));
                }

                @Override
                public void onLoginFailed() {
                    showError(isMobileDataAvailable() ? "No internet connection." : "Incorrect username or password.");
                    startActivity(new Intent(MainActivity.this, home.class));

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() &&
                (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                        activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE && TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes() > 0);
    }

    private boolean isMobileDataAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() &&
                (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
