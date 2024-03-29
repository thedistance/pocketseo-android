/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.pocketseo.model.AnalyticsTracker;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScapeLink;
import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.model.MSLinkFilter;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by pharris on 17/02/16.
 */
public class LinksPresenter implements Presenter {

    private View mView;
    private final DataRepository mRepo;
    private final AnalyticsTracker mAnalytics;
    private String mWebsite;
    private Subscription subscription;
    List<MozScapeLink> links = new ArrayList<>();
    private boolean moreToLoad;
    private int currentPage;
    private MozScapeLink selectedLink;

    private MSLinkFilter filter = new MSLinkFilter();
    public MSLinkFilter appliedFilter = new MSLinkFilter();
    private String error429message;
    private String mozWebsite;

    interface View {
        void showLoading(boolean loading);

        void showResults(List<MozScapeLink> links, boolean clear, boolean moreToLoad);

        void showError(String message);

        void showEmpty(boolean show);

        void showFab(boolean show);

        void openLink(MozScapeLink link);

        void openWebsite(String url, boolean chromeCustomTab);
    }

    public LinksPresenter(Context context, String website, DataRepository repo, AnalyticsTracker analytics) {
        mWebsite = website;
        mRepo = repo;
        mAnalytics = analytics;
        error429message = context.getString(R.string.Error429Text);
        mozWebsite = context.getString(R.string.MozWebsiteURL);
    }

    public void performSearch(String websiteUrl, boolean firstLoad, boolean refresh) {
        if (TextUtils.isEmpty(websiteUrl)) {
            return;
        }
        mWebsite = websiteUrl;

//        if(firstLoad) {
//            mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_LOADURL, mWebsite);
//        } else if(refresh){
//            mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_REFRESHDATA, mWebsite);
//        }

        mView.showLoading(true);
        loadLinks(websiteUrl, 1, refresh);
    }

    public void loadNext() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            return;
        }
        loadLinks(mWebsite, currentPage + 1, true);
    }

    private void loadLinks(String websiteUrl, final int page, boolean force) {
        unsubscribe();

        subscription = mRepo.getLinkMetrics(websiteUrl, page, appliedFilter, force)
                .subscribe(new Subscriber<List<MozScapeLink>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof MSHelper.ApiLimitException) {
                            mView.showError(error429message);
                        } else {
                            mView.showError(e.getMessage());
                        }
                        mView.showLoading(false);
                    }

                    @Override
                    public void onNext(List<MozScapeLink> links) {
                        if (page == 1) {
                            LinksPresenter.this.links.clear();
                        }
                        currentPage = page;
                        LinksPresenter.this.links.addAll(links);
                        mView.showLoading(false);

                        moreToLoad = links.size() == 25;
                        mView.showResults(links, page == 1, moreToLoad);
                        mView.showEmpty(LinksPresenter.this.links.isEmpty());
                    }
                });
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void onViewAttached(View view) {
        mView = view;
        mView.showResults(links, true, moreToLoad);

        if (links.isEmpty()) {
            performSearch(mWebsite, true, true);
        }
    }

    @Override
    public void onViewDetached() {
        unsubscribe();
    }

    @Override
    public void onDestroyed() {
        onViewDetached();
    }

    public void setSelectedLink(MozScapeLink link) {
        selectedLink = link;
        mView.showFab(selectedLink != null);
    }

    public void setSort(MSLinkMetrics.Sort sort) {
        filter.sort = sort;
        applyFilter();
    }

    public void setSourceFilter(MSLinkMetrics.Filter sourceFilter) {
        if (this.filter.filters.get(0).equals(sourceFilter)) {
            return;
        }
        filter.filters.set(0, sourceFilter);
        applyFilter();
    }

    public void setLinkFilter(MSLinkMetrics.Filter filter) {
        if (this.filter.filters.get(1).equals(filter)) {
            return;
        }
        this.filter.filters.set(1, filter);
        applyFilter();
    }

    public void setScope(MSLinkMetrics.Scope scope) {
        if (filter.scope.equals(scope)) {
            return;
        }
        filter.scope = scope;
        applyFilter();
    }

    public void openSelected() {
        if (selectedLink != null) {
            mView.openLink(selectedLink);
        }
    }

    public void retry() {
        if (links.isEmpty()) {
            performSearch(mWebsite, true, true);
        } else {
            loadNext();
        }
    }

    public void applyFilter() {
        appliedFilter.scope = filter.scope;
        appliedFilter.sort = filter.sort;
        appliedFilter.filters = filter.filters;
        performSearch(mWebsite, false, true);
    }

    public void resetFilter() {
        filter = new MSLinkFilter();
    }

    public void onMozClick() {
        mView.openWebsite(mozWebsite, false);
    }
}
