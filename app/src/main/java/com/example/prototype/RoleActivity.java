package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RoleActivity extends BaseActivity {
    Button btnStudent, btnFaculty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnFaculty = findViewById(R.id.btnFaculty);

        btnStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, createAcc.class);
            intent.putExtra("role", "student");
            startActivity(intent);
        });

        btnFaculty.setOnClickListener(v -> {
            Intent intent = new Intent(this, createAcc.class);
            intent.putExtra("role", "faculty");
            startActivity(intent);
        });

    }
}