/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;

import java.text.DateFormat;
import java.text.NumberFormat;

import io.pocketseo.model.MozScape;

/**
 * Created by pharris on 18/02/16.
 */
public class MozScapeViewModel {

    final MozScape model;
    DateFormat df;
    java.text.NumberFormat nf;

    public MozScapeViewModel(MozScape model, Context context) {
        df = android.text.format.DateFormat.getDateFormat(context);
        nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
        this.model = model;
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
