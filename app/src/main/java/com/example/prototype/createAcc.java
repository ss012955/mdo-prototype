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

public class createAcc extends AppCompatActivity {
    ImageView umakLogo;
    TextView txtviewEmail, txtviewUsername, txtviewFirstName, txtviewLastName,
            txtviewEntPass, confirmPass, txtviewLogIn;
    EditText etEmail, etUsername, etFirstName, etLastName, etPassword, etconfirmPass;
    Button signUp;
    Toast toast;
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

        umakLogo = findViewById(R.id.umakLogo);
        txtviewEmail = findViewById(R.id.txtviewEmail);
        etEmail = findViewById(R.id.etEmail);
        txtviewUsername = findViewById(R.id.txtviewUsername);
        etUsername = findViewById(R.id.etUsername);
        txtviewFirstName = findViewById(R.id.txtviewFirstName);
        etFirstName = findViewById(R.id.etFirstName);
        txtviewLastName = findViewById(R.id.txtviewLastName);
        etLastName = findViewById(R.id.etLastName);
        txtviewEntPass = findViewById(R.id.txtviewEntPass);
        etPassword = findViewById(R.id.etPassword);
        confirmPass = findViewById(R.id.confirmPass);
        etconfirmPass = findViewById(R.id.etconfirmPass);
        signUp = findViewById(R.id.btnSignUp);
        txtviewLogIn = findViewById(R.id.txtviewLogIn);

       txtviewLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               txtviewLogIn.setPaintFlags(txtviewLogIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                LogIn();
            }
        });
       signUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               toast = Toast.makeText(getApplicationContext(),
                       "Sign Up",Toast.LENGTH_SHORT);
               toast.show();

           }
       });
    }
    public void LogIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}