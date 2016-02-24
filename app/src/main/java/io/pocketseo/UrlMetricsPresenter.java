/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.text.TextUtils;

import io.pocketseo.model.AlexaScore;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;
import rx.Subscriber;

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

        void sendEmail(String recipient, String subject, String body, String userInstruction);

        void openWebsite(String url, String userInstruction);
    }

    public UrlMetricsPresenter(View view, DataRepository repo){
        mView = view;
        mRepo = repo;
    }

    public void performSearch(String websiteUrl, boolean force){
        if(TextUtils.isEmpty(websiteUrl)) return;

        loadMoz(websiteUrl, force);
        loadAlexa(websiteUrl, force);
        loadHtmlData(websiteUrl, force);
    }

    private void loadHtmlData(String websiteUrl, boolean force) {
        mView.showHtmldataLoading(true);
        mView.showHtmldataError(null);

        mRepo.getHtmldata(websiteUrl, force).subscribe(new Subscriber<HtmlData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showHtmldataLoading(false);
                mView.showHtmldataResult(null);
                mView.showHtmldataError(e.getMessage());

            }

            @Override
            public void onNext(HtmlData htmlData) {
                mView.showHtmldataLoading(false);
                mView.showHtmldataError(null);
                mView.showHtmldataResult(htmlData);
            }
        });
    }

    private void loadMoz(String websiteUrl, boolean force) {
        mView.showMozLoading(true);
        mView.showMozError(null);
        mRepo.getWebsiteMetrics(websiteUrl, force)
                .subscribe(new Subscriber<MozScape>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showMozLoading(false);
                        mView.showMozError(e.getMessage());
                        mView.showMozResult(null);
                    }

                    @Override
                    public void onNext(MozScape mozScape) {
                        mView.showMozLoading(false);
                        mView.showMozError(null);
                        mView.showMozResult(mozScape);
                    }
                });
    }

    private void loadAlexa(String websiteUrl, boolean force) {
        mView.showAlexaLoading(true);
        mView.showAlexaError(null);
        mRepo.getAlexaScore(websiteUrl, force).subscribe(new Subscriber<AlexaScore>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showAlexaLoading(false);
                mView.showAlexaError(e.getMessage());
                mView.showAlexaResult(null);
            }

            @Override
            public void onNext(AlexaScore alexaScore) {
                mView.showAlexaLoading(false);
                mView.showAlexaError(null);
                mView.showAlexaResult(alexaScore);
            }
        });
    }


    public void sendFeedback(){
        mView.sendEmail("pocketseo@thedistance.co.uk", "Pocket SEO Feedback", null, "Send Feedback");
    }

    public void getInTouch(){
        
    }

    public void visitWebsite(){
        mView.openWebsite("https://thedistance.co.uk/", "Visit The Distance Website");
    }

}
