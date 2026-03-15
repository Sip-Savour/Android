package com.sipandsavour.ui.auth;

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

public class LoginFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;

    // Views
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private TextView tvError;
    private MaterialButton btnLogin;
    private LinearProgressIndicator progressLogin;
    private TextView tvForgotPassword;
    private TextView btnGoToRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
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
        // Header title is already set in XML, no need to bind

        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etPassword = view.findViewById(R.id.etPassword);
        tvError = view.findViewById(R.id.tvError);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressLogin = view.findViewById(R.id.progressLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        btnGoToRegister = view.findViewById(R.id.btnGoToRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> onLoginClicked());

        tvForgotPassword.setOnClickListener(v -> {
            // TODO: Naviguer vers l'écran de mot de passe oublié
        });

        btnGoToRegister.setOnClickListener(v -> navController.navigate(R.id.action_login_to_register));
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnLogin.setEnabled(!isLoading);
            progressLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                navigateToHome();
            }
        });
    }

    private void onLoginClicked() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.validation_email_required));
            return;
        }
        tilEmail.setError(null);

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.validation_password_required));
            return;
        }
        tilPassword.setError(null);

        viewModel.clearError();
        viewModel.login(email, password);
    }

    private void navigateToHome() {
        navController.navigate(R.id.action_login_to_home);
    }
}