package com.sipnsavour.app;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etEmail;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        etEmail = findViewById(R.id.et_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email requis");
                return;
            }

            // TODO: Implémenter la réinitialisation du mot de passe
            Toast.makeText(ForgotPasswordActivity.this,
                "Un email de réinitialisation a été envoyé",
                Toast.LENGTH_LONG).show();
            finish();
        });
    }
}