package com.sipandsavour.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.api.AuthApi;
import com.sipandsavour.data.api.WineApi;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.data.dto.LoginRequest;
import com.sipandsavour.ui.common.UiState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryTest {

    // Règle pour forcer l'exécution des LiveData sur le thread principal du test
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock AuthApi mockAuthApi;
    @Mock WineApi mockWineApi;
    @Mock SessionManager mockSessionManager;
    @Mock ApiClient mockApiClient;
    @Mock Call<AuthResponse> mockCall;

    private Repository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Réinitialisation du Singleton Repository par réflexion
        Field instanceField = Repository.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Mock des Singletons dont dépend le Repository
        try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class);
             MockedStatic<SessionManager> mockedSession = mockStatic(SessionManager.class)) {

            mockedApiClient.when(ApiClient::getInstance).thenReturn(mockApiClient);
            mockedSession.when(SessionManager::getInstance).thenReturn(mockSessionManager);

            when(mockApiClient.getAuthApi()).thenReturn(mockAuthApi);
            when(mockApiClient.getWineApi()).thenReturn(mockWineApi);

            repository = Repository.getInstance();
        }
    }

    @Test
    public void login_success_updatesUiStateToSuccess() {
        // 1. Préparation
        String email = "test@test.com";
        String pass = "password";
        AuthResponse fakeResponse = new AuthResponse("token123", 1, "pseudo", email);

        when(mockAuthApi.login(any(LoginRequest.class))).thenReturn(mockCall);

        // 2. Exécution
        LiveData<UiState<AuthResponse>> liveData = Repository.login(email, pass);

        // On capture le callback envoyé à Retrofit
        ArgumentCaptor<Callback<AuthResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockAuthApi).login(any());
        verify(mockCall).enqueue(callbackCaptor.capture());

        // On simule une réponse positive de l'API
        callbackCaptor.getValue().onResponse(mockCall, Response.success(fakeResponse));

        // 3. Vérifications
        assertNotNull(liveData.getValue());
        assertEquals(UiState.Status.SUCCESS, liveData.getValue().getStatus());
        assertEquals("token123", liveData.getValue().getData().getToken());

        // Vérifie que les données sont bien sauvegardées dans la session
        verify(mockSessionManager).saveToken("token123");
        verify(mockSessionManager).saveUser(anyInt(), anyString(), anyString());
    }

    @Test
    public void login_failure_updatesUiStateToError() {
        // 1. Préparation
        when(mockAuthApi.login(any())).thenReturn(mockCall);

        // 2. Exécution
        LiveData<UiState<AuthResponse>> liveData = Repository.login("wrong@mail.com", "123");

        ArgumentCaptor<Callback<AuthResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(callbackCaptor.capture());

        // Simule une erreur 401 (Unauthorized)
        callbackCaptor.getValue().onResponse(mockCall, Response.error(401, okhttp3.ResponseBody.create(null, "")));

        // 3. Vérifications
        assertEquals(UiState.Status.ERROR, liveData.getValue().getStatus());
        assertEquals("Email ou mot de passe incorrect", liveData.getValue().getMessage());
    }

    @Test
    public void logout_callsSessionManagerLogout() {
        Repository.logout();
        verify(mockSessionManager).logout();
    }

    @Test
    public void isLoggedIn_returnsValueFromSessionManager() {
        when(mockSessionManager.isLoggedIn()).thenReturn(true);
        assertTrue(repository.isLoggedIn());

        when(mockSessionManager.isLoggedIn()).thenReturn(false);
        assertFalse(repository.isLoggedIn());
    }
}