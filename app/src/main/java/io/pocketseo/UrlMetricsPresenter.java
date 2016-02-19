/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.text.TextUtils;

import io.pocketseo.model.AlexaScore;
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
        void showAlexaScore(AlexaScore score);
        void showError(String message);
    }

    public UrlMetricsPresenter(View view, DataRepository repo){
        mView = view;
        mRepo = repo;
    }

    boolean mozInProgress;
    boolean alexaInProgress;

    public void performSearch(String websiteUrl, boolean force){
        if(TextUtils.isEmpty(websiteUrl)) return;

        mozInProgress = alexaInProgress = true;
        mView.showLoading(mozInProgress || alexaInProgress);
        mRepo.getWebsiteMetrics(websiteUrl, force, new DataRepository.Callback<MozScape>() {
            @Override
            public void success(MozScape data) {
                mozInProgress = false;
                mView.showLoading(mozInProgress || alexaInProgress);
                mView.showUrlMetrics(data);
            }

            @Override
            public void error(String message) {
                mozInProgress = false;
                mView.showLoading(mozInProgress || alexaInProgress);
                mView.showError(message);
            }
        });

        mRepo.getAlexaScore(websiteUrl, force, new DataRepository.Callback<AlexaScore>() {
            @Override
            public void success(AlexaScore data) {
                alexaInProgress = false;
                mView.showLoading(mozInProgress || alexaInProgress);
                mView.showAlexaScore(data);
            }

            @Override
            public void error(String message) {
                alexaInProgress = false;
                mView.showLoading(mozInProgress || alexaInProgress);
                mView.showError(message);
            }
        });
    }
}
