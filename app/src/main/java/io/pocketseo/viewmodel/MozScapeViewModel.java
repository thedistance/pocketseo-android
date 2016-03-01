/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;
import android.util.SparseArray;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import io.pocketseo.R;
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
    private final SparseArray<String> statusCodeLookup = new SparseArray<>();

    public MozScapeViewModel(MozScape model, Context context) {
        df = android.text.format.DateFormat.getDateFormat(context);
        nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);

        statusCodeLookup.put(0, context.getString(R.string.URLMozscapeStatusCode0));
        statusCodeLookup.put(1, context.getString(R.string.URLMozscapeStatusCode1));
        statusCodeLookup.put(2, context.getString(R.string.URLMozscapeStatusCode2));
        statusCodeLookup.put(3, context.getString(R.string.URLMozscapeStatusCode3));
        statusCodeLookup.put(4, context.getString(R.string.URLMozscapeStatusCode4));
        statusCodeLookup.put(5, context.getString(R.string.URLMozscapeStatusCode5));
        statusCodeLookup.put(6, context.getString(R.string.URLMozscapeStatusCode6));
        statusCodeLookup.put(7, context.getString(R.string.URLMozscapeStatusCode7));
        statusCodeLookup.put(8, context.getString(R.string.URLMozscapeStatusCode8));
        statusCodeLookup.put(9, context.getString(R.string.URLMozscapeStatusCode9));
        statusCodeLookup.put(10, context.getString(R.string.URLMozscapeStatusCode10));
        statusCodeLookup.put(11, context.getString(R.string.URLMozscapeStatusCode11));
        statusCodeLookup.put(12, context.getString(R.string.URLMozscapeStatusCode12));
        statusCodeLookup.put(13, context.getString(R.string.URLMozscapeStatusCode13));

        this.model = model;
    }

    public String getStatusCode() {
        String mozError = statusCodeLookup.get(model.getStatusCode());
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
        if(0 == model.getSpamScore()){
            return "-";
        }
        return String.valueOf(Math.round(model.getSpamScore() - 1));
    }
}
