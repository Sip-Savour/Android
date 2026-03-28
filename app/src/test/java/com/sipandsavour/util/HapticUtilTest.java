package com.sipandsavour.util;

import static org.mockito.Mockito.*;

import android.view.HapticFeedbackConstants;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HapticUtilTest {

    @Mock
    View mockView;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void playLightClick_callsViewWithCorrectConstants() {
        // Exécution
        HapticUtil.playLightClick(mockView);

        // Vérification : on vérifie que la méthode a été appelée sur la vue
        // avec KEYBOARD_TAP et le flag d'ignorance des réglages globaux
        verify(mockView).performHapticFeedback(
                eq(HapticFeedbackConstants.KEYBOARD_TAP),
                eq(HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        );
    }

    @Test
    public void playConfirm_callsViewWithCorrectConstants() {
        // Exécution
        HapticUtil.playConfirm(mockView);

        // Vérification : on vérifie l'usage de VIRTUAL_KEY
        verify(mockView).performHapticFeedback(
                eq(HapticFeedbackConstants.VIRTUAL_KEY),
                eq(HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        );
    }

    @Test
    public void playMethods_doNotCrashWithNullView() {
        // On vérifie simplement que l'appel ne déclenche pas de NullPointerException
        HapticUtil.playLightClick(null);
        HapticUtil.playConfirm(null);

        // Si on arrive ici sans exception, le test est réussi
    }
}