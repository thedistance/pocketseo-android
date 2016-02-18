/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import android.util.Log;

import java.util.Locale;

import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import retrofit2.Call;
import retrofit2.Response;

public class DataRepositoryImpl implements DataRepository {
    public static final String PREF_FILE = "cache";

    private final MSWebService mMozWebService;
    private final MSHelper.Authenticator mMozAuthenticator;
    private final DataCache mCache;

    public DataRepositoryImpl(MSWebService mMozWebService, MSHelper.Authenticator mMozAuthenticator, DataCache cache) {
        this.mMozWebService = mMozWebService;
        this.mMozAuthenticator = mMozAuthenticator;
        this.mCache = cache;
    }

    @Override
    public void getWebsiteMetrics(String website, boolean refresh, final Callback<MozScape> callbacks) {
        MozScape cachedValue = mCache.getWebsiteMetrics(website);
        if(null == cachedValue || refresh){
            loadWebsiteMetricsFromWeb(website, callbacks);
        } else {
            Log.d("DataRepo", String.format("Cache hit for %s", website));
            callbacks.success(cachedValue);
        }
    }

    private void loadWebsiteMetricsFromWeb(final String website, final Callback<MozScape> callbacks){
        Log.d("DataRepo", String.format("Load web for %s", website));
        mMozWebService.getUrlMetrics(website, MSUrlMetrics.getBitmask(), mMozAuthenticator.getAuthenticationMap()).enqueue(new retrofit2.Callback<MSUrlMetrics>() {
            @Override
            public void onResponse(Call<MSUrlMetrics> call, Response<MSUrlMetrics> response) {
                if(response.isSuccess()) {
                    mCache.store(website, response.body());
                    callbacks.success(response.body());
                } else {
                    callbacks.error(String.format(Locale.US, "Error code %d", response.code()));
                }
            }

            @Override
            public void onFailure(Call<MSUrlMetrics> call, Throwable t) {
                callbacks.error(t.getMessage());
            }
        });
    }
}
