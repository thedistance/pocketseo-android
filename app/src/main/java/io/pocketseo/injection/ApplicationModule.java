/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.injection;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.pocketseo.PocketSeoApplication;
import io.pocketseo.htmlparser.HtmlParser;
import io.pocketseo.model.AnalyticsTracker;
import io.pocketseo.model.AnalyticsTrackerImpl;
import io.pocketseo.model.DataCacheImpl;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.DataRepositoryImpl;
import io.pocketseo.webservice.alexa.AlexaWebService;
import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;

@Module
public class ApplicationModule {
    private PocketSeoApplication mApplication;

    public ApplicationModule(PocketSeoApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    PocketSeoApplication provideApplicationContext(){
        return mApplication;
    }

    @Provides
    @Singleton
    DataRepository provideDataRepository(AlexaWebService alexaWebService, MSWebService mozWebService, MSHelper.Authenticator mMozAuthenticator, HtmlParser parser, DataRepositoryImpl.DataCache cache){
        return new DataRepositoryImpl(alexaWebService, mozWebService, mMozAuthenticator, parser, cache);
    }

    @Provides
    @Singleton
    DataRepositoryImpl.DataCache provideDataCache(Gson gson, SharedPreferences prefs){
        return new DataCacheImpl(gson, prefs);
    }

    @Provides
    SharedPreferences provideSharedPreferences(PocketSeoApplication mApplication){
        return mApplication.getSharedPreferences(DataRepositoryImpl.PREF_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    AnalyticsTracker provideAnalytics(PocketSeoApplication application){
        return new AnalyticsTrackerImpl(application);
    }
}
