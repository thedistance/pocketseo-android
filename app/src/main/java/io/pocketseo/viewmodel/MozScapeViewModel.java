/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;
import android.util.SparseArray;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import io.pocketseo.model.MozScape;

/**
 * Created by pharris on 18/02/16.
 */
public class MozScapeViewModel {

    final MozScape model;
    DateFormat df;
    java.text.NumberFormat nf;

    /**
     * lookups for responses from <a href="https://moz.com/help/guides/moz-api/mozscape/api-reference/http">Reference page</a>
     */
    private static final SparseArray<String> STATUS_CODE_LOOKUP = new SparseArray<>();
    static {
        STATUS_CODE_LOOKUP.put(0, "Not Crawled");
        STATUS_CODE_LOOKUP.put(1, "Network error");
        STATUS_CODE_LOOKUP.put(2, "Transcode failure (failure detecting content type)");
        STATUS_CODE_LOOKUP.put(3, "Binary Content");
        STATUS_CODE_LOOKUP.put(4, "Invalid HTTP status code");
        STATUS_CODE_LOOKUP.put(5, "Blocked by robots.txt");
        STATUS_CODE_LOOKUP.put(6, "Blocked by request");
        STATUS_CODE_LOOKUP.put(7, "Internal status code to crawler");
        STATUS_CODE_LOOKUP.put(8, "Internal status code to crawler");
        STATUS_CODE_LOOKUP.put(9, "Page has a meta no-index tag");
        STATUS_CODE_LOOKUP.put(10, "Page too big");
        STATUS_CODE_LOOKUP.put(11, "Currently unused");
        STATUS_CODE_LOOKUP.put(12, "Currently unused");
        STATUS_CODE_LOOKUP.put(13, "Failed to fetch robots.txt");
    }

    public MozScapeViewModel(MozScape model, Context context) {
        df = android.text.format.DateFormat.getDateFormat(context);
        nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
        this.model = model;
    }

    public String getStatusCode() {
        String mozError = STATUS_CODE_LOOKUP.get(model.getStatusCode());
        if(null != mozError) return mozError;
        return String.format(Locale.US, "HTTP Response %d", model.getStatusCode());
    }

    public String getLastIndex(){
        if(model.getLastIndex() == null) return "N/A";
        return df.format(model.getLastIndex());
    }

    public String getNextIndex(){
        if(model.getNextIndex() == null) return "N/A";
        return df.format(model.getNextIndex());
    }

    public String getLinksRoot(){
        return nf.format(model.getLinksRoot());
    }

    public String getLinksTotal(){
        return nf.format(model.getLinksTotal());
    }

    public String getPageAuthority(){
        return String.valueOf(Math.round(model.getPageAuthority()));
    }
    public String getDomainAuthority(){
        return String.valueOf(Math.round(model.getDomainAuthority()));
    }
    public String getSpamScore(){
        return String.valueOf(Math.round(model.getSpamScore()));
    }
}
