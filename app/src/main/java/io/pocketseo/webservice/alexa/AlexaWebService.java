/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.alexa;

import io.pocketseo.webservice.alexa.model.AlexaData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pharris on 18/02/16.
 */
public interface AlexaWebService {
    String URL_PREFIX = "http://data.alexa.com/";

    // I don't know what these arguments mean
    @GET("data?cli=10&data=snbamz")
    Call<AlexaData> getAlexaData(@Query("url") String url);
}
