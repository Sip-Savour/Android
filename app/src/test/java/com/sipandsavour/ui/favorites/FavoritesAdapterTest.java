package com.sipandsavour.ui.favorites;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
// On retire "theme" de l'annotation pour éviter l'erreur de compilation
@Config(sdk = 34)
public class FavoritesAdapterTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FavoritesAdapter adapter;
    private Context context;

    @Mock
    FavoritesAdapter.OnFavoriteClickListener mockListener;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();

        // Application MANUELLE du thème pour MaterialCardView
        context.setTheme(R.style.Theme_SipSavour);

        adapter = new FavoritesAdapter();
        adapter.setOnFavoriteClickListener(mockListener);
    }

    @Test
    public void onBindViewHolder_setsCorrectText() {
        WineDto wine = new WineDto();
        wine.setTitle("Château Petrus");

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wine, null);
        FavoritesAdapter.FavoriteViewHolder holder = adapter.new FavoriteViewHolder(itemView);

        holder.bind(wine, 0);

        TextView tvName = itemView.findViewById(R.id.tvFavoriteName);
        assertEquals("Château Petrus", tvName.getText().toString());
    }

    @Test
    public void onBindViewHolder_setsCorrectImageForWhiteWine() {
        WineDto wine = new WineDto();
        wine.setColor("white");

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wine, null);
        FavoritesAdapter.FavoriteViewHolder holder = adapter.new FavoriteViewHolder(itemView);

        holder.bind(wine, 0);

        ImageView ivWine = itemView.findViewById(R.id.ivFavoriteWine);

        // MÉTHODE ALTERNATIVE : On regarde le Drawable (le dessin)
        // Shadows.shadowOf(Drawable) renvoie un ShadowDrawable qui possède getCreatedFromResId()
        int imageResId = Shadows.shadowOf(ivWine.getDrawable()).getCreatedFromResId();

        assertEquals(R.drawable.ic_wine_white, imageResId);
    }

    @Test
    public void itemClick_triggersListenerWithCorrectPosition() {
        WineDto wine = new WineDto();
        wine.setId(99);
        wine.setTitle("Vin de test");

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wine, null);
        FavoritesAdapter.FavoriteViewHolder holder = adapter.new FavoriteViewHolder(itemView);
        holder.bind(wine, 3);

        itemView.performClick();

        verify(mockListener).onFavoriteClick(eq(wine), eq(3));
    }

    @Test
    public void bind_usesVarietyIfTitleIsNull() {
        WineDto wine = new WineDto();
        wine.setTitle(null);
        wine.setVariety("Chardonnay");

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wine, null);
        FavoritesAdapter.FavoriteViewHolder holder = adapter.new FavoriteViewHolder(itemView);

        holder.bind(wine, 0);

        TextView tvName = itemView.findViewById(R.id.tvFavoriteName);
        assertEquals("Chardonnay", tvName.getText().toString());
    }

    @Test
    public void getWineImageRes_defaultsToRedIfColorIsNull() {
        WineDto wine = new WineDto();
        wine.setColor(null);

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wine, null);
        FavoritesAdapter.FavoriteViewHolder holder = adapter.new FavoriteViewHolder(itemView);

        holder.bind(wine, 0);

        ImageView ivWine = itemView.findViewById(R.id.ivFavoriteWine);
        int imageResId = Shadows.shadowOf(ivWine.getDrawable()).getCreatedFromResId();

        assertEquals(R.drawable.ic_wine_red, imageResId);
    }
}