package com.sipandsavour.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AddFavoriteRequestTest {

    @Test
    public void testConstructorAndGetters() {
        // 1. Préparation des données
        int expectedId = 42;

        // 2. Exécution : Utilisation du constructeur avec paramètres
        AddFavoriteRequest request = new AddFavoriteRequest(expectedId);

        // 3. Vérification : Le getter doit renvoyer la même valeur
        assertEquals(expectedId, request.getWineId());
    }

    @Test
    public void testSetters() {
        // 1. Préparation
        AddFavoriteRequest request = new AddFavoriteRequest();
        int newId = 101;

        // 2. Exécution : Utilisation du setter
        request.setWineId(newId);

        // 3. Vérification
        assertEquals(newId, request.getWineId());
    }

    @Test
    public void testDefaultConstructor() {
        // Vérifie que le constructeur par défaut (utilisé par GSON/Retrofit) fonctionne
        AddFavoriteRequest request = new AddFavoriteRequest();
        assertNotNull(request);
        // Par défaut, un int est à 0 en Java
        assertEquals(0, request.getWineId());
    }
}