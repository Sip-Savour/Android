package com.sipandsavour.util;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class ShakeDetectorTest {

    private ShakeDetector detector;
    private ShakeDetector.OnShakeListener mockListener;

    @Before
    public void setUp() {
        detector = new ShakeDetector();
        mockListener = mock(ShakeDetector.OnShakeListener.class);
        detector.setOnShakeListener(mockListener);
    }

    @Test
    public void normalGravity_DoesNotTriggerShake() throws Exception {
        // Simule un téléphone immobile (pesanteur normale sur l'axe Z)
        // gForce = sqrt(0^2 + 0^2 + 1^2) = 1.0 (en dessous du seuil de 2.7)
        sendShakeEvent(0, 0, SensorManager.GRAVITY_EARTH);

        verify(mockListener, never()).onShake(anyInt());
    }

    @Test
    public void violentMovement_TriggersShake() throws Exception {
        // On simule une accélération de 30 m/s^2 sur l'axe X (environ 3G)
        // 30 / 9.8 = 3.06G (> 2.7)
        sendShakeEvent(30.0f, 0, 0);

        verify(mockListener).onShake(1);
    }

    @Test
    public void multipleShakes_IncrementsCounter() throws Exception {
        // Première secousse
        sendShakeEvent(30.0f, 0, 0);

        // On attend 600ms (plus que le SHAKE_SLOP_TIME_MS de 500ms)
        Thread.sleep(600);

        // Deuxième secousse
        sendShakeEvent(0, 30.0f, 0);

        verify(mockListener).onShake(1);
        verify(mockListener).onShake(2);
    }

    @Test
    public void shakesTooClose_AreIgnored() throws Exception {
        // Première secousse
        sendShakeEvent(30.0f, 0, 0);

        // Secousse trop rapide (100ms après)
        Thread.sleep(100);
        sendShakeEvent(30.0f, 0, 0);

        // Le listener ne doit avoir été appelé qu'une seule fois
        verify(mockListener, times(1)).onShake(anyInt());
    }

    @Test
    public void longPause_ResetsCounter() throws Exception {
        // Première secousse
        sendShakeEvent(30.0f, 0, 0);
        verify(mockListener).onShake(1);

        // On attend plus de 3 secondes (SHAKE_COUNT_RESET_TIME_MS)
        Thread.sleep(3100);

        // Nouvelle secousse : le compteur doit repartir à 1
        sendShakeEvent(30.0f, 0, 0);
        verify(mockListener, times(2)).onShake(1);
    }

    // === HELPER DE RÉFLEXION ===
    private void sendShakeEvent(float x, float y, float z) throws Exception {
        // Création du SensorEvent
        Constructor<SensorEvent> constructor = SensorEvent.class.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        SensorEvent event = constructor.newInstance(3);

        // Remplissage des valeurs d'accélération
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        float[] values = (float[]) valuesField.get(event);
        values[0] = x;
        values[1] = y;
        values[2] = z;

        detector.onSensorChanged(event);
    }
}