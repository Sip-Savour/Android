package com.sipandsavour.ui.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;

    // Views
    private TextInputLayout tilName;
    private TextInputEditText etName;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private TextInputLayout tilDob;
    private TextInputEditText etDob;
    private TextView tvError;
    private MaterialButton btnRegister;
    private LinearProgressIndicator progressRegister;
    private TextView btnGoToLogin;

    // Date
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private String apiDob = ""; // La date formatée pour l'API

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        bindViews(view);
        setupListeners();
        observeViewModel();
    }

    private void bindViews(View view) {
        tilName = view.findViewById(R.id.tilName);
        etName = view.findViewById(R.id.etName);
        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etPassword = view.findViewById(R.id.etPassword);
        tilDob = view.findViewById(R.id.tilDob);
        etDob = view.findViewById(R.id.etDob);
        tvError = view.findViewById(R.id.tvError);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressRegister = view.findViewById(R.id.progressRegister);
        btnGoToLogin = view.findViewById(R.id.btnGoToLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onRegisterClicked();
        });

        btnGoToLogin.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            navController.navigateUp();
        });

        etDob.setOnClickListener(v -> showDatePicker());
        tilDob.setEndIconOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            showDatePicker();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnRegister.setEnabled(!isLoading);
            progressRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                navigateToHome();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etDob.setText(dateFormat.format(calendar.getTime()));
                    apiDob = apiDateFormat.format(calendar.getTime());
                },
                calendar.get(Calendar.YEAR) - 25,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        picker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        picker.show();
    }

    private void onRegisterClicked() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String dob = apiDob.isEmpty() ? (etDob.getText() != null ? etDob.getText().toString().trim() : "") : apiDob;

        boolean isValid = true;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.validation_name_required));
            isValid = false;
        } else {
            tilName.setError(null);
        }

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.validation_email_required));
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.validation_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.validation_password_short));
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (dob.isEmpty()) {
            // CORRECTION: Utilisation de getString()
            tilDob.setError(getString(R.string.validation_dob_required));
            isValid = false;
        } else {
            tilDob.setError(null);
        }

        if (!isValid) return;

        viewModel.clearError();
        viewModel.register(name, email, password, dob);
    }

    private void navigateToHome() {
        navController.navigate(R.id.action_register_to_home);
    }
}