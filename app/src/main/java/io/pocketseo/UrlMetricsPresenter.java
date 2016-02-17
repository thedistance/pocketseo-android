/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.text.TextUtils;

import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;

/**
 * Created by pharris on 17/02/16.
 */
public class UrlMetricsPresenter {

    private final View mView;
    private final DataRepository mRepo;

    interface View {
        void showLoading(boolean loading);
        void showUrlMetrics(MozScape data);
        void showError(String message);
    }

    public UrlMetricsPresenter(View view, DataRepository repo){
        mView = view;
        mRepo = repo;
    }

    public void performSearch(String websiteUrl){
        if(TextUtils.isEmpty(websiteUrl)) return;

        mView.showLoading(true);
        mRepo.getWebsiteMetrics(websiteUrl, new DataRepository.Callback<MozScape>() {
            @Override
            public void success(MozScape data) {
                mView.showLoading(false);
                mView.showUrlMetrics(data);
            }

            @Override
            public void error(String message) {
                mView.showLoading(false);
                mView.showError(message);
            }
        });
    }
}
