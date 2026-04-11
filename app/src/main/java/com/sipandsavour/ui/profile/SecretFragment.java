package com.sipandsavour.ui.profile; 

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer; 
import android.os.Bundle; 
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.SeekBar; 

import androidx.annotation.NonNull; 
import androidx.annotation.Nullable; 
import androidx.appcompat.app.AppCompatDelegate; 
import androidx.fragment.app.Fragment; 

import com.sipandsavour.R; 
import com.sipandsavour.data.SessionManager; 
import com.sipandsavour.util.HapticUtil; 

public class SecretFragment extends Fragment { 

    private static final int THEME_JINX_CODE = 100; 
    private MediaPlayer mediaPlayer; 

    @Nullable 
    @Override 
    /**
     * Inflate le layout du fragment et prépare les vues.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { 
        return inflater.inflate(R.layout.fragment_secret, container, false); 
    }

    @Override 
    /**
     * Appelé après que la vue du fragment soit créée.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { 
        super.onViewCreated(view, savedInstanceState); 

        // === JOUER LE SON ===
        if (savedInstanceState == null) { 
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.secret_unlocked); 
            if (mediaPlayer != null) { 
                mediaPlayer.start(); 
                mediaPlayer.setOnCompletionListener(mp -> { 
                    mp.release(); 
                    mediaPlayer = null; 
                }); 
            } 
        }

        SeekBar slideButtonJinx = view.findViewById(R.id.slideButtonJinx); 

        boolean isJinxActive = SessionManager.getInstance().getTheme() == THEME_JINX_CODE; 
        slideButtonJinx.setProgress(isJinxActive ? 100 : 0); 

        // === LANCER L'ANIMATION DU SINGE JINX ===
        Drawable thumb = slideButtonJinx.getThumb();
        if (thumb instanceof AnimationDrawable) {
            AnimationDrawable monkeyAnim = (AnimationDrawable) thumb;
            monkeyAnim.start();
        }

        // === EFFET "SLIDE TO ACTIVATE" AVEC RESSORT ===
        slideButtonJinx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { 
            @Override 
            /**
             * Appelé lorsque la progression du curseur change.
             */
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
            } 

            @Override 
            /**
             * Appelé lorsque l'utilisateur commence à faire glisser le curseur.
             */
            public void onStartTrackingTouch(SeekBar seekBar) { 
            } 

            @Override 
            /**
             * Appelé lorsque l'utilisateur arrête de faire glisser le curseur.
             */
            public void onStopTrackingTouch(SeekBar seekBar) { 
                int progress = seekBar.getProgress(); 
                boolean currentlyActive = SessionManager.getInstance().getTheme() == THEME_JINX_CODE; 

                if (!currentlyActive) { 
                    if (progress >= 95) { 
                        seekBar.setProgress(100); 
                        activateJinx(view); 
                    } else { 
                        seekBar.setProgress(0); 
                    } 
                } else { 
                    if (progress <= 5) { 
                        seekBar.setProgress(0); 
                        deactivateJinx(view); 
                    } else { 
                        seekBar.setProgress(100); 
                    } 
                } 
            } 
        }); 
    }

    /**
     * Active le thème Jinx.
     */
    private void activateJinx(View view) { 
        HapticUtil.playConfirm(view); 
        SessionManager.getInstance().setTheme(THEME_JINX_CODE); 
        requireActivity().recreate(); 
    } 

    /**
     * Désactive le thème Jinx.
     */
    private void deactivateJinx(View view) { 
        HapticUtil.playConfirm(view); 
        SessionManager.getInstance().setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); 
        requireActivity().recreate(); 
    } 

    @Override 
    /**
     * Appelé lorsque la vue du fragment est détruite.
     */
    public void onDestroyView() { 
        super.onDestroyView(); 

        if (mediaPlayer != null) { 
            if (mediaPlayer.isPlaying()) { 
                mediaPlayer.stop(); 
            } 
            mediaPlayer.release(); 
            mediaPlayer = null; 
        } 
    } 
} 