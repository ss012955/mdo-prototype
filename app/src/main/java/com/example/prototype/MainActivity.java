package com.example.prototype;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

TextView txtUN, txtPass, createAcc, forgotPass, incorrect, textmdo;
EditText username, password;
Button logIn;
Toast toast;
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

       ImageView logo = findViewById(R.id.umakLogo);
       logo.setImageResource(R.drawable.umaklogo);
       txtUN = findViewById(R.id.txtviewName);
       txtPass = findViewById(R.id.txtviewPassword);
       createAcc = findViewById(R.id.createAccount);
       forgotPass = findViewById(R.id.forgotPassword);
       username = findViewById(R.id.etUsername);
       password = findViewById(R.id.etPassword);
       logIn = findViewById(R.id.btnLogIn);
       incorrect = findViewById(R.id.incorrectUNPass);
       textmdo = findViewById(R.id.textmdo);

       createAcc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               createAcc.setPaintFlags(createAcc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
               createAccount();
           }
       });

       forgotPass.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               forgotPass.setPaintFlags(forgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
               forgotPass();
           }
       });

       logIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               home();
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