/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.injection;

import javax.inject.Singleton;

import dagger.Component;
import io.pocketseo.PocketSeoApplication;
import io.pocketseo.model.AnalyticsTracker;
import io.pocketseo.model.DataRepository;

@Singleton
@Component(modules = {ApplicationModule.class, MozscapeModule.class, AlexaModule.class, HtmldataModule.class})
public interface ApplicationComponent {
    PocketSeoApplication appContext();

    DataRepository repository();

    AnalyticsTracker analytics();
}
