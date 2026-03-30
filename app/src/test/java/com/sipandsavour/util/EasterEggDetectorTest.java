package com.sipandsavour.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowSensor;
import org.robolectric.shadows.ShadowSensorManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class EasterEggDetectorTest {

    private EasterEggDetector detector;
    private SensorManager sensorManager;
    private ShadowSensorManager shadowSensorManager;
    private EasterEggDetector.OnSecretUnlockedListener mockListener;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        shadowSensorManager = shadowOf(sensorManager);

        // --- ÉTAPE CRUCIALE : On ajoute les capteurs au système simulé ---
        addMockSensor(Sensor.TYPE_ACCELEROMETER);
        addMockSensor(Sensor.TYPE_LIGHT);
        addMockSensor(Sensor.TYPE_PROXIMITY);

        detector = new EasterEggDetector(context);
        mockListener = mock(EasterEggDetector.OnSecretUnlockedListener.class);
        detector.setListener(mockListener);
    }

    private void addMockSensor(int type) {
        // On crée un capteur via Robolectric et on l'ajoute au Manager
        Sensor sensor = ShadowSensor.newInstance(type);
        shadowSensorManager.addSensor(sensor);
    }

    @Test
    public void ritual_Success_WhenAllConditionsMet() throws Exception {
        detector.start();

        // 1. Simule "Téléphone à plat"
        sendSensorEvent(Sensor.TYPE_ACCELEROMETER, new float[]{0, 0, 9.8f});

        // 2. Simule "Dans le noir"
        sendSensorEvent(Sensor.TYPE_LIGHT, new float[]{5.0f});

        // 3. Simule "Recouvert"
        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});

        // 4. Simule le "Snap" sonore récent (on injecte le timestamp)
        setLastSnapTime(System.currentTimeMillis());

        // On déclenche un dernier mouvement pour forcer la vérification
        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});

        // On laisse le thread principal traiter le Handler
        ShadowLooper.idleMainLooper();

        verify(mockListener, times(1)).onSecretUnlocked();
    }

    @Test
    public void ritual_Fails_WhenOneConditionMissing() throws Exception {
        detector.start();

        sendSensorEvent(Sensor.TYPE_ACCELEROMETER, new float[]{0, 0, 9.8f});
        sendSensorEvent(Sensor.TYPE_LIGHT, new float[]{100.0f}); // Trop de lumière !
        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});
        setLastSnapTime(System.currentTimeMillis());

        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});
        ShadowLooper.idleMainLooper();

        verify(mockListener, never()).onSecretUnlocked();
    }

    @Test
    public void ritual_Fails_WhenSnapIsTooOld() throws Exception {
        detector.start();

        sendSensorEvent(Sensor.TYPE_ACCELEROMETER, new float[]{0, 0, 9.8f});
        sendSensorEvent(Sensor.TYPE_LIGHT, new float[]{5.0f});
        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});

        // Snap datant de plus de 500ms
        setLastSnapTime(System.currentTimeMillis() - 1000);

        sendSensorEvent(Sensor.TYPE_PROXIMITY, new float[]{1.0f});
        ShadowLooper.idleMainLooper();

        verify(mockListener, never()).onSecretUnlocked();
    }

    // === HELPERS DE RÉFLEXION ===

    private void sendSensorEvent(int sensorType, float[] values) throws Exception {
        // On récupère le capteur que l'on a ajouté au setUp
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        // Création du SensorEvent via réflexion
        Constructor<SensorEvent> constructor = SensorEvent.class.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        SensorEvent event = constructor.newInstance(values.length);

        // On remplit le champ 'sensor'
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        sensorField.set(event, sensor);

        // On remplit le champ 'values'
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        float[] eventValues = (float[]) valuesField.get(event);
        System.arraycopy(values, 0, eventValues, 0, values.length);

        detector.onSensorChanged(event);
    }

    private void setLastSnapTime(long time) throws Exception {
        Field field = EasterEggDetector.class.getDeclaredField("lastSnapTime");
        field.setAccessible(true);
        field.set(detector, time);
    }
}