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
        void showMozLoading(boolean loading);
        void showMozResult(MozScape data);
        void showMozError(String message);

        void showAlexaLoading(boolean loading);
        void showAlexaResult(AlexaScore score);
        void showAlexaError(String message);
    }

    public UrlMetricsPresenter(View view, DataRepository repo){
        mView = view;
        mRepo = repo;
    }

    public void performSearch(String websiteUrl, boolean force){
        if(TextUtils.isEmpty(websiteUrl)) return;

        loadMoz(websiteUrl, force);
        loadAlexa(websiteUrl, force);
    }

    private void loadMoz(String websiteUrl, boolean force) {
        mView.showMozLoading(true);
        mView.showMozError(null);
        mRepo.getWebsiteMetrics(websiteUrl, force, new DataRepository.Callback<MozScape>() {
            @Override
            public void success(MozScape data) {
                mView.showMozLoading(false);
                mView.showMozResult(data);
            }

            @Override
            public void error(String message) {
                mView.showMozLoading(false);
                mView.showMozError(message);
                mView.showMozResult(null);
            }
        });
    }

    private void loadAlexa(String websiteUrl, boolean force) {
        mView.showAlexaLoading(true);
        mView.showAlexaError(null);
        mRepo.getAlexaScore(websiteUrl, force, new DataRepository.Callback<AlexaScore>() {
            @Override
            public void success(AlexaScore data) {
                mView.showAlexaLoading(false);
                mView.showAlexaResult(data);
            }

            @Override
            public void error(String message) {
                mView.showAlexaLoading(false);
                mView.showAlexaError(message);
                mView.showAlexaResult(null);
            }
        });
    }


}
