package com.sipnsavour.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etFirstName, etEmail, etBirthdate;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        etFirstName = findViewById(R.id.et_firstname);
        etEmail = findViewById(R.id.et_email);
        etBirthdate = findViewById(R.id.et_birthdate);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadUserData() {
        // TODO: Charger les données de l'utilisateur
        etFirstName.setText("Jane");
        etEmail.setText("jane.doe@mail.com");
        etBirthdate.setText("22/06/1995");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String birthdate = etBirthdate.getText().toString().trim();

                if (validateInputs(firstName, email, birthdate)) {
                    // TODO: Sauvegarder les modifications
                    Toast.makeText(EditProfileActivity.this,
                        "Profil mis à jour",
                        Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private boolean validateInputs(String firstName, String email, String birthdate) {
        if (firstName.isEmpty()) {
            etFirstName.setError("Prénom requis");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email requis");
            return false;
        }
        if (birthdate.isEmpty()) {
            etBirthdate.setError("Date de naissance requise");
            return false;
        }
        return true;
    }
}