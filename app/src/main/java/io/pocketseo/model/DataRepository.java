/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import io.pocketseo.HtmlData;

/**
 * Created by pharris on 17/02/16.
 */
public interface DataRepository {

    interface Callback<T> {
        void success(T data);
        void error(String message);
    }

    void getWebsiteMetrics(String url, boolean refresh, Callback<MozScape> callbacks);
    void getAlexaScore(String url, boolean refresh, Callback<AlexaScore> callbacks);
    void getHtmldata(String url, boolean refresh, Callback<HtmlData> callbacks);

}
