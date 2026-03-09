package com.sipnsavour.helper;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.sipnsavour.app.FavoritesActivity;
import com.sipnsavour.app.MainActivity;
import com.sipnsavour.app.ProfileActivity;
import com.sipnsavour.app.R;
import com.sipnsavour.app.SearchActivity;
import com.sipnsavour.app.WineListActivity;

public class NavigationHelper {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNav, int currentItemId) {
        bottomNav.setSelectedItemId(currentItemId);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == currentItemId) {
                    return true;
                }

                Intent intent = null;

                if (itemId == R.id.nav_home) {
                    intent = new Intent(activity, MainActivity.class);
                } else if (itemId == R.id.nav_search) {
                    intent = new Intent(activity, SearchActivity.class);
                } else if (itemId == R.id.nav_wine) {
                    intent = new Intent(activity, WineListActivity.class);
                } else if (itemId == R.id.nav_favorites) {
                    intent = new Intent(activity, FavoritesActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(activity, ProfileActivity.class);
                }

                if (intent != null) {
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }
}
