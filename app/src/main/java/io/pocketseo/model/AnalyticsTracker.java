/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

/**
 * Created by pharris on 25/02/16.
 */
public interface AnalyticsTracker {
    void sendAnalytic(String category, String action, String label);
}
