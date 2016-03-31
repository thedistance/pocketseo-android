/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.FrameLayout;

import uk.co.thedistance.thedistancekit.TheDistanceActivity;

/**
 * Created by pharris on 25/02/16.
 */
public class SettingsActivity extends TheDistanceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout layout = new FrameLayout(this);
        layout.setId(R.id.content);
        setContentView(layout);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(layout.getId(), new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs_tracking);
        }
    }
}