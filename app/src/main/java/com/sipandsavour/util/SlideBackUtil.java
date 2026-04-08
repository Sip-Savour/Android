package com.sipandsavour.util;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class SlideBackUtil {

    /**
     * Attache un écouteur de balayage (swipe) pour gérer le retour en arrière.
     * @param onSwipeRight L'action à exécuter lors d'un glissement vers la droite (ex: popBackStack).
     * @param views        Les vues sur lesquelles écouter le balayage (ex: layout racine, RecyclerView, ScrollView...).
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void attach(Runnable onSwipeRight, View... views) {
        View.OnTouchListener invincibleSwipeListener = new View.OnTouchListener() {
            private float startX = 0;
            private float startY = 0;
            private boolean isSwiping = false;
            private static final int SWIPE_THRESHOLD = 120; // Sensibilité du glissement

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        isSwiping = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float diffX = event.getRawX() - startX;
                        float diffY = event.getRawY() - startY;

                        // Détection d'un mouvement majoritairement horizontal
                        if (!isSwiping && Math.abs(diffX) > 40 && Math.abs(diffX) > Math.abs(diffY)) {
                            isSwiping = true;
                            // Empêche le parent (RecyclerView, ScrollView) d'intercepter l'événement
                            if (v.getParent() != null) {
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        }

                        if (isSwiping) return true;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isSwiping) {
                            float finalDiffX = event.getRawX() - startX;
                            if (Math.abs(finalDiffX) > SWIPE_THRESHOLD) {
                                if (finalDiffX > 0) {
                                    // Slide vers la DROITE (→) : On exécute l'action de retour
                                    if (onSwipeRight != null) {
                                        onSwipeRight.run();
                                    }
                                }
                            }
                            isSwiping = false;
                            return true; // Le swipe est terminé
                        }
                        break;
                }
                return false; // Laisse passer le clic et le scroll normal
            }
        };

        // On attache le détecteur à toutes les vues fournies en paramètres
        for (View view : views) {
            if (view != null) {
                view.setOnTouchListener(invincibleSwipeListener);
            }
        }
    }
}