package com.sipnsavour.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etFirstName, etEmail, etPassword, etBirthdate;
    private Button btnRegister;
    private TextView tvLogin;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        etFirstName = findViewById(R.id.et_firstname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etBirthdate = findViewById(R.id.et_birthdate);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        calendar = Calendar.getInstance();
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String birthdate = etBirthdate.getText().toString().trim();

                if (validateInputs(firstName, email, password, birthdate)) {
                    performRegistration(firstName, email, password, birthdate);
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etBirthdate.setText(date);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean validateInputs(String firstName, String email, String password, String birthdate) {
        if (firstName.isEmpty()) {
            etFirstName.setError("Prénom requis");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email requis");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Mot de passe requis");
            return false;
        }
        if (birthdate.isEmpty()) {
            etBirthdate.setError("Date de naissance requise");
            return false;
        }
        return true;
    }

    private void performRegistration(String firstName, String email, String password, String birthdate) {
        // Inscription (à implémenter avec Firebase ou API)
        Toast.makeText(this, "Inscription en cours...", Toast.LENGTH_SHORT).show();

        // Si succès
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
