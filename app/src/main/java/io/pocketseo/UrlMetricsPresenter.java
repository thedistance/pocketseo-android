/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.text.TextUtils;

import io.pocketseo.model.AnalyticsTracker;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;
import io.pocketseo.webservice.mozscape.MSHelper;
import rx.Subscriber;

/**
 * Created by pharris on 17/02/16.
 */
public class UrlMetricsPresenter {

    private final View mView;
    private final DataRepository mRepo;
    private final AnalyticsTracker mAnalytics;
    private String mWebsite;
    private String distanceWebsite;
    private String distanceFeedbackSurvey;
    private String mozWebsite;

    private String distancePhoneNumber;
    private String distanceEmail;
    private String feedbackEmail;
    private String feedbackSubject;
    private String feedbackPrompt;
    private String feedbackBody;
    private String error429message;

    interface View {
        void showMozLoading(boolean loading);
        void showMozResult(MozScape data);
        void showMozError(String message);

        void showHtmldataLoading(boolean loading);
        void showHtmldataResult(HtmlData result);
        void showHtmldataError(String message);

        void sendEmail(String recipient, String subject, String body, String userInstruction);

        void openWebsite(String url, boolean chromeCustomTab);

        void makePhoneCall(String phoneNumber);
    }

    public UrlMetricsPresenter(Context context, View view, DataRepository repo, AnalyticsTracker analytics){
        mView = view;
        mRepo = repo;
        mAnalytics = analytics;

        distanceWebsite = context.getString(R.string.TheDistanceContactWebsiteURL);
        distanceFeedbackSurvey = context.getString(R.string.TheDistancePanelFeedbackURL);
        mozWebsite = context.getString(R.string.MozWebsiteURL);
        distancePhoneNumber = context.getString(R.string.TheDistanceContactPhone);
        distanceEmail = context.getString(R.string.TheDistanceContactEmail);
        feedbackEmail = context.getString(R.string.TheDistancePanelSendFeedbackEmailAddress);
        feedbackPrompt = context.getString(R.string.TheDistancePanelButtonSendFeedback);
        feedbackSubject = context.getString(R.string.TheDistancePanelSendFeedbackSubject);
        feedbackBody = context.getString(R.string.TheDistancePanelEmailBody, context.getString(R.string.app_name), "Android", BuildConfig.VERSION_NAME);
        error429message = context.getString(R.string.Error429Text);

        // String body = String.format(Locale.US, "\n\nSent from PocketSEO %s (%d) on Android", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
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
                        mView.showMozResult(null);
                        if(e instanceof MSHelper.ApiLimitException){
                            mView.showMozError(error429message);
                        } else {
                            mView.showMozError(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(MozScape mozScape) {
                        mView.showMozLoading(false);
                        mView.showMozError(null);
                        mView.showMozResult(mozScape);
                    }
                });
    }

    public void sendFeedback(){
        mView.openWebsite(distanceFeedbackSurvey, true);
    }

    public void getInTouchByEmail(){
        mView.sendEmail(distanceEmail, null, null, "Get in touch");
    }

    public void getInTouchByPhone() {
        mView.makePhoneCall(distancePhoneNumber);
    }

    public void mozscapeLogoTouched() {
        mView.openWebsite(mozWebsite, false);
    }

    public void visitTheDistanceWebsite(){
        mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_VIEW_DISTANCE_WEBSITE, null);
        mView.openWebsite(distanceWebsite, false);
    }

}
