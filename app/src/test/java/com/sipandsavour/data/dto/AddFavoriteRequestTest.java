package com.sipandsavour.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AddFavoriteRequestTest {

    @Test
    public void testConstructorAndGetters() {
        // 1. Préparation des données
        int expectedWineId = 42;
        int expectedUserId = 7;

        // 2. Exécution : Utilisation du constructeur avec les deux paramètres
        AddFavoriteRequest request = new AddFavoriteRequest(expectedWineId, expectedUserId);

        // 3. Vérification : Les getters doivent renvoyer les bonnes valeurs
        assertEquals("Le wineId doit correspondre à la valeur passée au constructeur", expectedWineId, request.getWineId());
        assertEquals("Le userId doit correspondre à la valeur passée au constructeur", expectedUserId, request.getUserId());
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