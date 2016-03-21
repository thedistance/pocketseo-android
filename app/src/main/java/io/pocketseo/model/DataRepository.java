/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import java.util.List;

import io.pocketseo.HtmlData;
import rx.Observable;

/**
 * Created by pharris on 17/02/16.
 */
public interface DataRepository {

    interface Callback<T> {
        void success(T data);
        void error(String message);
    }

    Observable<MozScape> getWebsiteMetrics(String url, boolean refresh);
    Observable<List<MozScapeLink>> getLinkMetrics(String url, int page, boolean refresh);
    Observable<AlexaScore> getAlexaScore(String url, boolean refresh);
    Observable<HtmlData> getHtmldata(String url, boolean refresh);

}
