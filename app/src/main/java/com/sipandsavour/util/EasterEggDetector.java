package com.sipandsavour.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

public class EasterEggDetector implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor lightSensor;
    private final Sensor proximitySensor;
    private final Sensor magneticSensor; // === NOUVEAU : Capteur magnétique ===

    private AudioRecord audioRecord;
    private volatile boolean isListeningAudio = false;
    private Thread audioThread;

    private volatile long lastSnapTime = 0;

    private boolean isFlat = false;
    private boolean isDark = false;
    private boolean isCovered = false;
    private boolean isTriggered = false;

    // === NOUVEAUTÉ : Variables pour la boussole ===
    private float[] gravity;
    private float[] geomagnetic;
    private boolean isPointingNorth = false;

    private OnSecretUnlockedListener listener;

    public interface OnSecretUnlockedListener {
        void onSecretUnlocked();
    }

    public EasterEggDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // Initialisation
    }

    public void setListener(OnSecretUnlockedListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (lightSensor != null) sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        if (magneticSensor != null) sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI); // NOUVEAU

        startAudioListening();
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        stopAudioListening();
    }

    @SuppressLint("MissingPermission")
    private void startAudioListening() {
        if (isListeningAudio) return;

        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) return;

        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufferSize);
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) return;

            audioRecord.startRecording();
            isListeningAudio = true;

            audioThread = new Thread(() -> {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                short[] buffer = new short[minBufferSize];

                try {
                    while (isListeningAudio) {
                        int readSize = audioRecord.read(buffer, 0, minBufferSize);
                        if (readSize > 0) {
                            double maxAmplitude = 0;
                            for (int i = 0; i < readSize; i++) {
                                if (Math.abs(buffer[i]) > maxAmplitude) {
                                    maxAmplitude = Math.abs(buffer[i]);
                                }
                            }

                            if (maxAmplitude > 20000) {
                                lastSnapTime = System.currentTimeMillis();
                                verifierLeRituel();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (audioRecord != null) {
                        try {
                            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                                audioRecord.stop();
                            }
                            audioRecord.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            audioThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudioListening() {
        isListeningAudio = false;
        audioRecord = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone(); // Sauvegarde pour la boussole
            float z = event.values[2], x = event.values[0], y = event.values[1];
            isFlat = (z > 8.5f && Math.abs(x) < 2.0f && Math.abs(y) < 2.0f);
        } else if (type == Sensor.TYPE_LIGHT) {
            isDark = (event.values[0] < 10.0f);
        } else if (type == Sensor.TYPE_PROXIMITY) {
            isCovered = (event.values[0] < 3.0f);
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone(); // Sauvegarde pour la boussole
        }

        // === NOUVEAUTÉ : Calcul de l'orientation vers le Nord ===
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                // L'azimut est en radians, on le convertit en degrés (0 à 360)
                float azimuthInDegrees = (float)(Math.toDegrees(orientation[0]) + 360) % 360;

                // Le Nord est à 0° (ou 360°). On prend une tolérance de 20 degrés.
                isPointingNorth = (azimuthInDegrees < 20 || azimuthInDegrees > 340);
            }
        }

        verifierLeRituel();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void verifierLeRituel() {
        boolean isSnappedRecently = (System.currentTimeMillis() - lastSnapTime) < 500;

        // === NOUVEAUTÉ : On ajoute isPointingNorth à la condition ===
        if (isFlat && isDark && isCovered && isPointingNorth && isSnappedRecently) {
            if (!isTriggered && listener != null) {
                isTriggered = true;

                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onSecretUnlocked();
                });
            }
        } else {
            isTriggered = false;
        }
    }
}