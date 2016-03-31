/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;

import io.fabric.sdk.android.Fabric;
import io.pocketseo.injection.ApplicationComponent;
import io.pocketseo.injection.ApplicationModule;
import io.pocketseo.injection.DaggerApplicationComponent;
import io.pocketseo.injection.MozscapeModule;
import uk.co.thedistance.thedistancekit.TheDistanceApplication;

public class PocketSeoApplication extends TheDistanceApplication {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.USE_CRASHLYTICS && PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_crashlytics_enabled), true)) {
            Fabric.with(this, new Crashlytics());
        }

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .mozscapeModule(new MozscapeModule())
                .build();
    }

    @Override
    protected synchronized void initializeTracker() {
        if (mTracker == null && PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_analytics_enabled), true)) {
            mTracker = GoogleAnalytics.getInstance(this)
                    .newTracker(R.xml.global_tracker);
        }
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((PocketSeoApplication) context.getApplicationContext()).mApplicationComponent;
    }
}
