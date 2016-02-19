/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

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

        void showHtmldataLoading(boolean loading);
        void showHtmldataResult(HtmlData result);
        void showHtmldataError(String message);
    }

    public UrlMetricsPresenter(View view, DataRepository repo){
        mView = view;
        mRepo = repo;
    }

    public void performSearch(String websiteUrl, boolean force){
        if(TextUtils.isEmpty(websiteUrl)) return;

//        loadMoz(websiteUrl, force);
//        loadAlexa(websiteUrl, force);
        loadHtmlData(websiteUrl, force);
    }

    private void loadHtmlData(String websiteUrl, boolean force) {
//        mView.showHtmldataLoading(true);
//        mView.showHtmldataError(null);
        mView.showHtmldataResult(new HtmlData() {
            @Override
            public String getPageTitle() {
                return "The Distance App Developers";
            }

            @Override
            public String getCanonicalUrl() {
                return "https://thedistance.co.uk";
            }

            @Override
            public String getMetaDescription() {
                return "we make apps, we rock";
            }

            @Override
            public List<String> getH1TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("App Developers");
                testData.add("Android");
                testData.add("iOS");
                testData.add("York");
                return testData;
            }

            @Override
            public List<String> getH2TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("h2 App Developers");
                testData.add("Josh");
                testData.add("Anthony");
                testData.add("Rob");
                testData.add("Ben");
                testData.add("Pete");
                return testData;
            }

            @Override
            public boolean isSsl() {
                return true;
            }
        });
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
