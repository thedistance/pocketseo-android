/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.pocketseo.PocketSeoApplication;
import io.pocketseo.htmlparser.HtmlParser;
import okhttp3.OkHttpClient;

/**
 * Created by pharris on 17/02/16.
 */
@Module
public class HtmldataModule {

    @Provides
    @Singleton
    public HtmlParser provideParser(final PocketSeoApplication context, OkHttpClient okHttpClient) {
        return new HtmlParser(okHttpClient);
    }

}
