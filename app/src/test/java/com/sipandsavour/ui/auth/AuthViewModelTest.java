package com.sipandsavour.ui.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.ui.common.UiState;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock Repository mockRepository;
    private AuthViewModel viewModel;
    private MockedStatic<Repository> mockedStaticRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // On mock l'appel statique
        mockedStaticRepository = mockStatic(Repository.class);
        mockedStaticRepository.when(Repository::getInstance).thenReturn(mockRepository);

        // Plus besoin de réflexion ici !
        viewModel = new AuthViewModel();
    }

    @After
    public void tearDown() {
        mockedStaticRepository.close();
    }

    @Test
    public void login_success_updatesLoadingAndSuccess() {
        String email = "test@test.com";
        String pass = "password";
        MutableLiveData<UiState<AuthResponse>> result = new MutableLiveData<>();

        mockedStaticRepository.when(() -> Repository.login(email, pass)).thenReturn(result);

        viewModel.login(email, pass);

        // Simule le succès
        result.setValue(UiState.success(new AuthResponse()));

        assertNotNull(viewModel.getLoginSuccess().getValue());
        assertTrue(viewModel.getLoginSuccess().getValue());
        assertFalse(viewModel.getIsLoading().getValue());
    }
}