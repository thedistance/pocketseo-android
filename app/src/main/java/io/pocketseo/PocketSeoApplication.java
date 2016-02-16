/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class PocketSeoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (BuildConfig.USE_CRASHLYTICS /* && preferences.getBoolean(getString(R.string.pref_key_send_crashes), true) */) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
