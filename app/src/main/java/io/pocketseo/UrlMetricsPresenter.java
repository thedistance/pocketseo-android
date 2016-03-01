/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;

import io.pocketseo.model.AlexaScore;
import io.pocketseo.model.AnalyticsTracker;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;
import rx.Subscriber;

/**
 * Created by pharris on 17/02/16.
 */
public class UrlMetricsPresenter {

    private final View mView;
    private final DataRepository mRepo;
    private final AnalyticsTracker mAnalytics;
    private String mWebsite;

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

        void openWebsite(String url);

        void makePhoneCall(String phoneNumber);
    }

    public UrlMetricsPresenter(View view, DataRepository repo, AnalyticsTracker analytics){
        mView = view;
        mRepo = repo;
        mAnalytics = analytics;
    }

    public void performSearch(String websiteUrl, boolean firstLoad, boolean refresh){
        if(TextUtils.isEmpty(websiteUrl)) return;
        mWebsite = websiteUrl;

        if(firstLoad) {
            mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_LOADURL, mWebsite);
        } else if(refresh){
            mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_REFRESHDATA, mWebsite);
        }

        loadMoz(websiteUrl, firstLoad || refresh);
        loadAlexa(websiteUrl, firstLoad || refresh);
        loadHtmlData(websiteUrl, firstLoad || refresh);
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

    public void openInBrowser(){
        mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_OPEN_IN_BROWSER, mWebsite);

        Uri uri = Uri.parse(mWebsite);
        // TODO: parse URL and check it can be launched
        if(uri.getScheme() == null){
            uri = Uri.parse("http://" + mWebsite);
        }
        mView.openWebsite(uri.toString());
    }


    public void sendFeedback(){
        String body = String.format(Locale.US, "\n\nSent from PocketSEO %s (%d) on Android", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        mView.sendEmail("pocketseo@thedistance.co.uk", "Pocket SEO Feedback", body, "Send Feedback");
    }

    public void getInTouchByEmail(){
        mView.sendEmail("hello@thedistance.co.uk", null, null, "Get in touch");
    }

    public void getInTouchByPhone() {
        mView.makePhoneCall("+441904217171");
    }

    public void visitTheDistanceWebsite(){
        mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_VIEW_DISTANCE_WEBSITE, null);
        mView.openWebsite("https://thedistance.co.uk/");
    }

}
