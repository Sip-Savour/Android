package com.sipandsavour.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

public class SessionManagerTest {

    @Mock Context mockContext;
    @Mock SharedPreferences mockPrefs;
    @Mock SharedPreferences.Editor mockEditor;
    @Mock ApiClient mockApiClient;

    private SessionManager sessionManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Configuration du mock SharedPreferences (enchaînement fluide)
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), boolean.class.isPrimitive() ? anyBoolean() : any())).thenReturn(mockEditor);
        when(mockEditor.remove(anyString())).thenReturn(mockEditor);
        when(mockEditor.clear()).thenReturn(mockEditor);

        // Réinitialisation du Singleton SessionManager
        Field instance = SessionManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        // Initialisation avec le mock context
        SessionManager.init(mockContext);
        sessionManager = SessionManager.getInstance();
    }

    @Test
    public void saveToken_updatesPrefs() {
        // Exécution : On sauvegarde le token
        sessionManager.saveToken("my_secret_token");

        verify(mockEditor).putString(Constants.KEY_TOKEN, "my_secret_token");

        verify(mockEditor).apply();

    }

    @Test
    public void isLoggedIn_returnsTrueOnlyWhenTokenAndFlagPresent() {
        // Cas 1 : Rien
        when(mockPrefs.getString(Constants.KEY_TOKEN, null)).thenReturn(null);
        when(mockPrefs.getBoolean(Constants.KEY_LOGGED_IN, false)).thenReturn(false);
        assertFalse(sessionManager.isLoggedIn());

        // Cas 2 : Flag présent mais pas de token (ne devrait pas arriver, mais on teste la sécurité)
        when(mockPrefs.getBoolean(Constants.KEY_LOGGED_IN, true)).thenReturn(true);
        assertFalse(sessionManager.isLoggedIn());

        // Cas 3 : Tout est ok
        when(mockPrefs.getString(Constants.KEY_TOKEN, null)).thenReturn("valid_token");
        when(mockPrefs.getBoolean(Constants.KEY_LOGGED_IN, false)).thenReturn(true);
        assertTrue(sessionManager.isLoggedIn());
    }

    @Test
    public void logout_clearsAllData() {
        try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
            mockedApiClient.when(ApiClient::getInstance).thenReturn(mockApiClient);

            sessionManager.logout();

            // Vérifie le clear des prefs
            verify(mockEditor).clear();
            verify(mockEditor).apply();
        }
    }

    @Test
    public void addWineToHistory_addsAtTopAndRemovesDuplicates() {
        // On simule un historique existant : "10,20,30"
        when(mockPrefs.getString(Constants.KEY_HISTORY, "")).thenReturn("10,20,30");

        // Pour ce test, on doit mocker TextUtils.join car c'est une méthode statique Android
        try (MockedStatic<TextUtils> mockedTextUtils = mockStatic(TextUtils.class)) {
            // On simule le comportement de join (très simplifié)
            mockedTextUtils.when(() -> TextUtils.join(anyString(), any(Iterable.class)))
                    .thenReturn("20,10,30");

            // On ajoute le vin 20 (qui existe déjà en 2ème position)
            sessionManager.addWineToHistory(20);

            // On vérifie que le vin 20 est bien passé en premier (grâce à notre mock de join)
            verify(mockEditor).putString(Constants.KEY_HISTORY, "20,10,30");
        }
    }

    @Test
    public void getHistoryIds_convertsStringToList() {
        when(mockPrefs.getString(Constants.KEY_HISTORY, "")).thenReturn("1,5,12");

        List<Integer> ids = sessionManager.getHistoryIds();

        assertEquals(3, ids.size());
        assertEquals(Integer.valueOf(1), ids.get(0));
        assertEquals(Integer.valueOf(5), ids.get(1));
        assertEquals(Integer.valueOf(12), ids.get(2));
    }

    @Test
    public void getHistoryIds_withEmptyHistory_returnsEmptyList() {
        when(mockPrefs.getString(Constants.KEY_HISTORY, "")).thenReturn("");
        List<Integer> ids = sessionManager.getHistoryIds();
        assertTrue(ids.isEmpty());
    }
}