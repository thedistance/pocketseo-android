/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;

/**
 * Created by pharris on 17/02/16.
 */
public interface DataRepository {

    interface Callback<T> {
        void success(T data);
        void error(String message);
    }

    interface DataCache {
        MozScape getWebsiteMetrics(String url);
        void store(String url, MSUrlMetrics body);
    }

    void getWebsiteMetrics(String url, boolean refresh, Callback<MozScape> callbacks);

}
