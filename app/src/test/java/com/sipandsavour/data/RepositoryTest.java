package com.sipandsavour.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.ui.common.UiState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        // No setup needed
    }

    @Test
    public void login_success_updatesUiStateToSuccess() throws InterruptedException {
        String email = "test@test.com";
        String password = "password123";
        AuthResponse mockResponse = new AuthResponse();
        mockResponse.setToken("test_token");
        mockResponse.setUserId(1);
        mockResponse.setUsername("testuser");
        mockResponse.setEmail(email);

        CountDownLatch latch = new CountDownLatch(1);

        try (MockedStatic<Repository> repoMock = mockStatic(Repository.class)) {
            repoMock.when(() -> Repository.login(anyString(), anyString()))
                    .thenAnswer(invocation -> {
                        androidx.lifecycle.MutableLiveData<UiState<AuthResponse>> result =
                            new androidx.lifecycle.MutableLiveData<>();
                        result.setValue(UiState.success(mockResponse));
                        return result;
                    });

            LiveData<UiState<AuthResponse>> result = Repository.login(email, password);

            result.observeForever(state -> {
                if (state.isSuccess()) {
                    assertNotNull(state.getData());
                    assertEquals("test_token", state.getData().getToken());
                    latch.countDown();
                }
            });

            assertTrue(latch.await(2, TimeUnit.SECONDS));
        }
    }

    @Test
    public void login_failure_updatesUiStateToError() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        try (MockedStatic<Repository> repoMock = mockStatic(Repository.class)) {
            repoMock.when(() -> Repository.login(anyString(), anyString()))
                    .thenAnswer(invocation -> {
                        androidx.lifecycle.MutableLiveData<UiState<AuthResponse>> result =
                            new androidx.lifecycle.MutableLiveData<>();
                        result.setValue(UiState.error("Email ou mot de passe incorrect"));
                        return result;
                    });

            LiveData<UiState<AuthResponse>> result = Repository.login("bad@email.com", "wrong");

            result.observeForever(state -> {
                if (state.isError()) {
                    assertEquals("Email ou mot de passe incorrect", state.getMessage());
                    latch.countDown();
                }
            });

            assertTrue(latch.await(2, TimeUnit.SECONDS));
        }
    }

    @Test
    public void logout_callsSessionManagerLogout() {
        AtomicBoolean logoutCalled = new AtomicBoolean(false);

        try (MockedStatic<Repository> repoMock = mockStatic(Repository.class)) {
            repoMock.when(Repository::logout).thenAnswer(invocation -> {
                logoutCalled.set(true);
                return null;
            });

            Repository.logout();

            assertTrue("logout() should have been called", logoutCalled.get());
        }
    }

    @Test
    public void isLoggedIn_returnsValueFromSessionManager() {
        Repository mockRepo = mock(Repository.class);
        when(mockRepo.isLoggedIn()).thenReturn(true);

        try (MockedStatic<Repository> repoMock = mockStatic(Repository.class)) {
            repoMock.when(Repository::getInstance).thenReturn(mockRepo);

            boolean result = Repository.getInstance().isLoggedIn();

            assertTrue(result);
        }
    }
}