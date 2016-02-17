/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by pharris on 17/02/16.
 */
public class DataRespositoryImpl implements DataRepository {
    private final MSWebService mMozWebService;
    private final MSHelper.Authenticator mMozAuthenticator;

    public DataRespositoryImpl(MSWebService mMozWebService, MSHelper.Authenticator mMozAuthenticator) {
        this.mMozWebService = mMozWebService;
        this.mMozAuthenticator = mMozAuthenticator;
    }

    @Override
    public void getWebsiteMetrics(String website, final Callback<MozScape> callbacks) {
        mMozWebService.getUrlMetrics(website, MSUrlMetrics.getBitmask(), mMozAuthenticator.getAuthenticationMap()).enqueue(new retrofit2.Callback<MSUrlMetrics>() {
            @Override
            public void onResponse(Call<MSUrlMetrics> call, Response<MSUrlMetrics> response) {
                if(response.isSuccess()) {
                    callbacks.success(response.body());
                } else {
                    callbacks.error(String.format("Error code %d", response.code()));
                }
            }

            @Override
            public void onFailure(Call<MSUrlMetrics> call, Throwable t) {
                callbacks.error(t.getMessage());
            }
        });
    }
}
