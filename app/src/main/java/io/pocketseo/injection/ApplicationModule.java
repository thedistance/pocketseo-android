/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.pocketseo.PocketSeoApplication;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.DataRepositoryImpl;
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
    DataRepository provideDataRepository(MSWebService mozWebService, MSHelper.Authenticator mMozAuthenticator){
        return new DataRepositoryImpl(mozWebService, mMozAuthenticator);
    }
}
