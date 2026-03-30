package com.sipandsavour.ui.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UiStateTest {

    @Test
    public void loading_setsCorrectStatus() {
        UiState<String> state = UiState.loading();

        assertEquals(UiState.Status.LOADING, state.getStatus());
        assertTrue(state.isLoading());
        assertFalse(state.isSuccess());
        assertNull(state.getData());
    }

    @Test
    public void success_containsDataAndCorrectStatus() {
        String mockData = "Vin Rouge";
        UiState<String> state = UiState.success(mockData);

        assertTrue(state.isSuccess());
        assertEquals(mockData, state.getData());
        assertNull(state.getMessage());
        assertTrue(state.hasData());
    }

    @Test
    public void error_containsMessage() {
        String errorMsg = "Erreur réseau";
        UiState<String> state = UiState.error(errorMsg);

        assertTrue(state.isError());
        assertEquals(errorMsg, state.getMessage());
        assertNull(state.getData());
    }

    @Test
    public void error_withData_containsBoth() {
        String cachedData = "Ancienne donnée";
        String errorMsg = "Serveur hors ligne";

        // On crée l'état d'erreur avec des données (utile pour le cache)
        UiState<String> state = UiState.error(errorMsg, cachedData);

        assertTrue(state.isError());
        // On vérifie que le message est EXACTEMENT le même
        assertEquals(errorMsg, state.getMessage());
        assertEquals(cachedData, state.getData());
        assertTrue(state.hasData());
    }

    @Test
    public void idle_isInitialState() {
        UiState<Void> state = UiState.idle();
        assertTrue(state.isIdle());
        assertFalse(state.isLoading());
    }
}