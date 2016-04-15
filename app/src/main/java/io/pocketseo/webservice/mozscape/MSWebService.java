/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape;

import java.util.List;
import java.util.Map;

import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import io.pocketseo.webservice.mozscape.model.MSNextUpdate;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by pharris on 17/02/16.
 */
public interface MSWebService {
    // TODO: this might not allow SSL
    String URL_PREFIX = "https://lsapi.seomoz.com/linkscape/";

    @GET("url-metrics/{website}/")
    Observable<MSUrlMetrics> getUrlMetrics(@Path("website") String website, @Query("Cols") long parameterBitmask, @QueryMap Map<String, String> authParams);

    @GET("metadata/next_update.json")
    Observable<MSNextUpdate> getNextUpdate(@QueryMap Map<String, String> authParams);

    @GET("links/{website}?TargetCols=0&LinkCols=8&Scope=page_to_page")
    Observable<List<MSLinkMetrics>> getLinks(@Path("website") String website, @Query("SourceCols") long sourceBitmask,
                                             @Query("Limit") int limit, @Query("Offset") int offset,
                                             @QueryMap Map<String, String> authParams);

    @GET("links/{website}?TargetCols=0&LinkCols=10")
    Observable<List<MSLinkMetrics>> getLinks(@Path("website") String website, @Query("SourceCols") long sourceBitmask,
                                             @Query("Limit") int limit, @Query("Offset") int offset, @Query("Scope") String scope,
                                             @Query("Sort") String sort, @Query("Filter") String filter, @QueryMap Map<String, String> authParams);
}
