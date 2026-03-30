package com.sipandsavour.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class SlideBackUtilTest {

    private View view;
    private Runnable mockAction;
    private ViewParent mockParent;
    private View.OnTouchListener capturedListener;

    @Before
    public void setUp() {
        // On utilise un contexte d'application réel via Robolectric
        view = spy(new View(ApplicationProvider.getApplicationContext()));
        mockAction = mock(Runnable.class);
        mockParent = mock(ViewParent.class);

        // Simulation du parent
        when(view.getParent()).thenReturn(mockParent);

        // 1. On attache l'utilitaire
        SlideBackUtil.attach(mockAction, view);

        // 2. ÉTAPE CLÉ : On capture le listener anonyme créé à l'intérieur de SlideBackUtil
        ArgumentCaptor<View.OnTouchListener> captor = ArgumentCaptor.forClass(View.OnTouchListener.class);
        verify(view).setOnTouchListener(captor.capture());
        capturedListener = captor.getValue();
    }

    @Test
    public void swipeRight_TriggerAction() {
        // ACTION_DOWN : Départ à x=10
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_DOWN, 10f, 100f));

        // ACTION_MOVE : Glissement vers la droite (diff=190, > 40)
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_MOVE, 200f, 100f));

        // ACTION_UP : Relâchement (diff=190, > 120)
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_UP, 200f, 100f));

        // VÉRIFICATION
        verify(mockAction, times(1)).run();
        // Vérifie que l'on a bien demandé au parent de ne pas intercepter le scroll du RecyclerView/ScrollView
        verify(mockParent).requestDisallowInterceptTouchEvent(true);
    }

    @Test
    public void verticalSwipe_DoesNotTriggerAction() {
        // Mouvement vers le bas (Y change, X peu)
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_DOWN, 100f, 100f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_MOVE, 105f, 400f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_UP, 105f, 400f));

        verify(mockAction, never()).run();
    }

    @Test
    public void swipeTooShort_DoesNotTriggerAction() {
        // Glissement de 50 pixels seulement (< 120)
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_DOWN, 10f, 100f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_MOVE, 60f, 100f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_UP, 60f, 100f));

        verify(mockAction, never()).run();
    }

    @Test
    public void swipeLeft_DoesNotTriggerAction() {
        // Glissement vers la gauche (x diminue)
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_DOWN, 200f, 100f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_MOVE, 50f, 100f));
        capturedListener.onTouch(view, createMockEvent(MotionEvent.ACTION_UP, 50f, 100f));

        verify(mockAction, never()).run();
    }

    /**
     * Helper pour créer un mock propre de MotionEvent
     */
    private MotionEvent createMockEvent(int action, float x, float y) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getActionMasked()).thenReturn(action);
        when(event.getRawX()).thenReturn(x);
        when(event.getRawY()).thenReturn(y);
        return event;
    }
}