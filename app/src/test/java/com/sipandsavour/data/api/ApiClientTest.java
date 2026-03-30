package com.sipandsavour.data.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;

public class ApiClientTest {

    @Mock
    Context mockContext;

    @Mock
    Context mockAppContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Simulation du contexte pour éviter les NullPointerException sur le cache
        when(mockContext.getApplicationContext()).thenReturn(mockAppContext);
        when(mockAppContext.getCacheDir()).thenReturn(new File("."));

        // RÉINITIALISATION DU SINGLETON (via Réflexion)
        // C'est nécessaire car le Singleton garde son état entre les tests unitaires
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = ApiClient.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void getInstance_withoutInit_shouldThrowException() {
        // Si on appelle getInstance sans init(), ça doit crash
        ApiClient.getInstance();
    }

    @Test
    public void init_shouldCreateInstance() {
        // Initialisation
        ApiClient.init(mockContext);

        // Vérification
        assertNotNull(ApiClient.getInstance());
    }

    @Test
    public void getInstance_shouldReturnSameInstance() {
        ApiClient.init(mockContext);
        ApiClient instance1 = ApiClient.getInstance();
        ApiClient instance2 = ApiClient.getInstance();

        // Vérifie que c'est bien le même objet en mémoire (Singleton)
        assertSame(instance1, instance2);
    }

    @Test
    public void getApis_shouldReturnNonNull() {
        ApiClient.init(mockContext);
        ApiClient client = ApiClient.getInstance();

        assertNotNull(client.getAuthApi());
        assertNotNull(client.getWineApi());
    }

    @Test
    public void setAuthToken_doesNotCrash() {
        ApiClient.init(mockContext);
        ApiClient client = ApiClient.getInstance();

        // On vérifie juste que l'appel ne lève pas d'erreur
        client.setAuthToken("fake_token");
        client.clearAuthToken();
    }
}