package com.example.prototype;

import android.content.Intent;
import android.graphics.Paint;
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
import HelperClasses.LoginManager;

public class MainActivity extends AppCompatActivity {
    private LoginManager loginManager;
TextView txtUN, txtPass, createAcc, forgotPass, incorrect;
EditText username, password;
Button logIn;
Toast toast;

private String usernameInput, passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       createAcc = findViewById(R.id.createAccount);
       forgotPass = findViewById(R.id.forgotPassword);
       username = findViewById(R.id.etUsername);
       password = findViewById(R.id.etPassword);
       logIn = findViewById(R.id.btnLogIn);
       incorrect = findViewById(R.id.incorrectUNPass);
        incorrect.setVisibility(View.GONE);
        loginManager = new LoginManager(this);
        getLifecycle().addObserver(loginManager);


       createAcc.setOnClickListener(v -> {
           createAcc.setPaintFlags(createAcc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
           createAccount();
       });

       forgotPass.setOnClickListener(v->{
           forgotPass.setPaintFlags(forgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
           forgotPass();
       });


        logIn.setOnClickListener(v -> {
            usernameInput = username.getText().toString().trim();
            passwordInput = password.getText().toString().trim();

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Pass the callback to handle the result of login
                loginManager.performLogin(usernameInput, passwordInput, new LoginManager.LoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        home();
                    }

                    @Override
                    public void onLoginFailed() {
                        // If login fails, show the incorrect TextView
                        incorrect.setVisibility(View.VISIBLE);
                        incorrect.setText("Incorrect username or password.");
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        home();

                    }
                });
            }
        });


    }

    public void createAccount(){
        Intent intent = new Intent(this, createAcc.class);
        startActivity(intent);
    }

    public void forgotPass(){
        Intent intent = new Intent(this, forgotPass.class);
        startActivity(intent);
    }

    public void home(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }

}