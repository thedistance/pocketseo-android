/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.pocketseo.PocketSeoApplication;
import io.pocketseo.webservice.alexa.AlexaWebService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by pharris on 17/02/16.
 */
@Module
public class AlexaModule {

    @Provides
    @Singleton
    public AlexaWebService provideWebService(final PocketSeoApplication context, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AlexaWebService.URL_PREFIX)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(AlexaWebService.class);
    }

}
