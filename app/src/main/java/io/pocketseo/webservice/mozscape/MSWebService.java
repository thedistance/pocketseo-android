/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape;

import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pharris on 17/02/16.
 */
public interface MSWebService {
    // TODO: this might not allow SSL
    String URL_PREFIX = "https://lsapi.seomoz.com/linkscape/";

    @GET("url-metrics/{website}/")
    MSUrlMetrics getUrlMetrics(@Path("website") String website, @Query("cols") long parameterBitmask);
}
