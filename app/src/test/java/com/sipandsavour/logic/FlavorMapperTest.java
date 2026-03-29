package com.sipandsavour.logic;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.sipandsavour.data.SessionManager;

import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

public class FlavorMapperTest {

    @Test
    public void mapFlavorToWineKeywords_withFrenchFlavor_returnsCorrectKeywords() {
        List<String> result = FlavorMapper.mapFlavorToWineKeywords("épicé");

        assertTrue(result.contains("spice"));
        assertTrue(result.contains("pepper"));
        assertTrue(result.contains("ginger"));
    }

    @Test
    public void buildFeaturesFromSelections_removesDuplicates() {
        // "grillé" et "épicé" contiennent tous les deux le mot "pepper"
        List<String> selections = Arrays.asList("grillé", "épicé");

        String result = FlavorMapper.buildFeaturesFromSelections(selections);

        // On transforme la string en liste pour compter les mots exacts (évite le bug pepper/peppery)
        List<String> resultWords = Arrays.asList(result.split(" "));

        long count = resultWords.stream().filter(w -> w.equals("pepper")).count();
        assertEquals("Le mot exact 'pepper' ne doit apparaître qu'une fois", 1, count);
    }

    @Test
    public void buildFeaturesFromText_parsesComplexSentence() {
        // Correction : on utilise "crémeux" car "crémeuse" n'est pas encore dans ton Mapper
        String text = "poisson grillé crémeux";
        String result = FlavorMapper.buildFeaturesFromText(text);

        assertTrue("Devrait contenir smoke (issu de grillé)", result.contains("smoke"));
        assertTrue("Devrait contenir cream (issu de crémeux)", result.contains("cream"));
        assertTrue("Devrait contenir butter (issu de crémeux)", result.contains("butter"));
    }

    @Test
    public void getGroupDisplayName_handlesTranslation() {
        try (MockedStatic<SessionManager> mockedSession = mockStatic(SessionManager.class)) {
            SessionManager mockManager = mock(SessionManager.class);
            mockedSession.when(SessionManager::getInstance).thenReturn(mockManager);

            // Test Français
            when(mockManager.getLanguage()).thenReturn("fr");
            assertEquals("Fruits rouges", FlavorMapper.getGroupDisplayName("red_fruit"));

            // Test Anglais
            when(mockManager.getLanguage()).thenReturn("en");
            assertEquals("Red fruits", FlavorMapper.getGroupDisplayName("red_fruit"));
        }
    }

    @Test
    public void getAccordionCategories_returnsCorrectSize() {
        try (MockedStatic<SessionManager> mockedSession = mockStatic(SessionManager.class)) {
            SessionManager mockManager = mock(SessionManager.class);
            mockedSession.when(SessionManager::getInstance).thenReturn(mockManager);
            when(mockManager.getLanguage()).thenReturn("fr");

            List<FlavorMapper.AccordionCategory> categories = FlavorMapper.getAccordionCategories();
            assertEquals(6, categories.size());
        }
    }
}