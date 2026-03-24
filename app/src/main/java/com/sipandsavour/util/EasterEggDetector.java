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

    private AudioRecord audioRecord;
    private volatile boolean isListeningAudio = false;
    private Thread audioThread;

    // On passe cette variable en volatile car elle est lue/écrite par 2 threads différents
    private volatile long lastSnapTime = 0;

    private boolean isFlat = false;
    private boolean isDark = false;
    private boolean isCovered = false;
    private boolean isTriggered = false;

    private OnSecretUnlockedListener listener;

    public interface OnSecretUnlockedListener {
        void onSecretUnlocked();
    }

    public EasterEggDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void setListener(OnSecretUnlockedListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (lightSensor != null) sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);

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

                            // Si un bruit très fort est détecté
                            if (maxAmplitude > 20000) {
                                lastSnapTime = System.currentTimeMillis();

                                // === NOUVEAUTÉ : On vérifie le rituel IMMÉDIATEMENT ===
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
            float z = event.values[2], x = event.values[0], y = event.values[1];
            isFlat = (z > 8.5f && Math.abs(x) < 2.0f && Math.abs(y) < 2.0f);
        } else if (type == Sensor.TYPE_LIGHT) {
            isDark = (event.values[0] < 10.0f);
        } else if (type == Sensor.TYPE_PROXIMITY) {
            isCovered = (event.values[0] < 3.0f);
        }

        // === NOUVEAUTÉ : On appelle la méthode centralisée ===
        verifierLeRituel();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Méthode centralisée qui peut être appelée à la fois par les capteurs de mouvement
     * ET par le thread du microphone de manière sécurisée.
     */
    private void verifierLeRituel() {
        boolean isSnappedRecently = (System.currentTimeMillis() - lastSnapTime) < 500;

        if (isFlat && isDark && isCovered && isSnappedRecently) {
            if (!isTriggered && listener != null) {
                isTriggered = true;

                // On force l'exécution sur le Main Thread (UI Thread)
                // Indispensable car cette méthode peut être appelée depuis le Thread Audio en arrière-plan !
                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onSecretUnlocked();
                });
            }
        } else {
            isTriggered = false;
        }
    }
}