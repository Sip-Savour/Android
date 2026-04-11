package com.sipandsavour.util;

import android.view.HapticFeedbackConstants;
import android.view.View;

/**
 * Utilitaire pour centraliser les retours haptiques (vibrations) de l'application.
 */
public class HapticUtil {

    /**
     * Vibration légère (idéale pour les sélections, les puces, les petits boutons)
     * @param view La vue sur laquelle effectuer le retour haptique
     */
    public static void playLightClick(View view) {
        if (view != null) {
            view.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }
    }

    /**
     * Vibration un peu plus marquée (idéale pour les validations, ajouts aux favoris, gros boutons)
     * @param view La vue sur laquelle effectuer le retour haptique
     */
    public static void playConfirm(View view) {
        if (view != null) {
            view.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }
    }
}