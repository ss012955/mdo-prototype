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

public class forgotPass extends AppCompatActivity {
    ImageView umakLogo;
    TextView enterEmail, recoverEmail, createAcc, logIn;
    EditText email;
    Toast toast;
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

        email = findViewById(R.id.etEmail);
        umakLogo = findViewById(R.id.umakLogo);
        enterEmail = findViewById(R.id.txtviewForPass);
        recoverEmail = findViewById(R.id.txtviewRecoverPass);
        createAcc = findViewById(R.id.txtviewCreateAcc);
        logIn = findViewById(R.id.txtviewLogIn);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn.setPaintFlags(logIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                logIn();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               createAcc.setPaintFlags(createAcc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                createAccount();
            }
        });

        recoverEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast = Toast.makeText(getApplicationContext(),
                        "Recover Email",Toast.LENGTH_SHORT);
                toast.show();

            }
        });
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