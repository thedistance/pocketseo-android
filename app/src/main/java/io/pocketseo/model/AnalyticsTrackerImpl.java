/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import android.content.Context;

import javax.inject.Inject;

import io.pocketseo.PocketSeoApplication;

/**
 * Created by pharris on 25/02/16.
 */
public class AnalyticsTrackerImpl implements AnalyticsTracker {

    private final Context mContext;

    @Inject
    public AnalyticsTrackerImpl(PocketSeoApplication application){
        this.mContext = application;
    }

    @Override
    public void sendAnalytic(String category, String action, String label) {
        uk.co.thedistance.thedistancekit.Analytics.send(
                mContext,
                new uk.co.thedistance.thedistancekit.Analytics.AnalyticEvent(category, action, label));
    }
}
