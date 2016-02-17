/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;

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

//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (BuildConfig.USE_CRASHLYTICS /* && preferences.getBoolean(getString(R.string.pref_key_send_crashes), true) */) {
            Fabric.with(this, new Crashlytics());
        }

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .mozscapeModule(new MozscapeModule())
                .build();
    }

    @Override
    protected synchronized void initializeTracker() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (mTracker == null && preferences.getBoolean("send_usage", true)) {
            mTracker = GoogleAnalytics.getInstance(this)
                    .newTracker(R.xml.global_tracker);
//        }
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((PocketSeoApplication) context.getApplicationContext()).mApplicationComponent;
    }
}
