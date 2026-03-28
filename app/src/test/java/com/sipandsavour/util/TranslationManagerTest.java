package com.sipandsavour.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.Translation;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.WineDto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
// FIX : On force le SDK à 34 car Robolectric ne supporte pas encore le SDK 36
@Config(sdk = 34)
public class TranslationManagerTest {

    private TranslationManager translationManager;

    @Mock Translator mockTranslator;
    @Mock SessionManager mockSessionManager;

    private MockedStatic<Translation> mockedTranslationStatic;
    private MockedStatic<SessionManager> mockedSessionStatic;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 1. On intercepte ML Kit
        mockedTranslationStatic = mockStatic(Translation.class);
        mockedTranslationStatic.when(() -> Translation.getClient(any())).thenReturn(mockTranslator);

        // 2. Simulation du téléchargement
        when(mockTranslator.downloadModelIfNeeded(any())).thenReturn(Tasks.forResult(null));

        // 3. Interception SessionManager
        mockedSessionStatic = mockStatic(SessionManager.class);
        mockedSessionStatic.when(SessionManager::getInstance).thenReturn(mockSessionManager);

        // 4. Reset du Singleton
        resetSingleton();
        translationManager = TranslationManager.getInstance();
    }

    @After
    public void tearDown() {
        if (mockedTranslationStatic != null) mockedTranslationStatic.close();
        if (mockedSessionStatic != null) mockedSessionStatic.close();
    }

    @Test
    public void translate_ShouldNotTranslate_WhenLanguageIsEnglish() {
        when(mockSessionManager.getLanguage()).thenReturn("en");

        WineDto wine = new WineDto();
        wine.setDescription("Awesome wine");

        translationManager.translateWineIfNeeded(wine, translatedWine -> {
            assertEquals("Awesome wine", translatedWine.getDescription());
            verify(mockTranslator, never()).translate(anyString());
        });
    }

    @Test
    public void translate_ShouldTranslate_WhenLanguageIsFrench() {
        when(mockSessionManager.getLanguage()).thenReturn("fr");

        WineDto wine = new WineDto();
        wine.setDescription("Dry red wine");
        wine.setVariety("Merlot");

        when(mockTranslator.translate("Dry red wine")).thenReturn(Tasks.forResult("Vin rouge sec"));
        when(mockTranslator.translate("Merlot")).thenReturn(Tasks.forResult("Merlot (FR)"));

        translationManager.translateWineIfNeeded(wine, translatedWine -> {
            assertEquals("Vin rouge sec", translatedWine.getDescription());
            assertEquals("Merlot (FR)", translatedWine.getVariety());
        });
    }

    @Test
    public void translateList_ShouldHandleAllItems() {
        when(mockSessionManager.getLanguage()).thenReturn("fr");

        List<WineDto> wines = new ArrayList<>();
        WineDto w1 = new WineDto(); w1.setDescription("Wine 1");
        WineDto w2 = new WineDto(); w2.setDescription("Wine 2");
        wines.add(w1);
        wines.add(w2);

        when(mockTranslator.translate(anyString())).thenReturn(Tasks.forResult("Traduit"));

        translationManager.translateWineListIfNeeded(wines, translatedWines -> {
            assertEquals(2, translatedWines.size());
            assertEquals("Traduit", translatedWines.get(0).getDescription());
        });
    }

    private void resetSingleton() throws Exception {
        Field instance = TranslationManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }
}