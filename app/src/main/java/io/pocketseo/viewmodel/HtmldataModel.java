/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;
import android.text.TextUtils;

import java.text.DateFormat;

import io.pocketseo.HtmlData;
import io.pocketseo.R;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmldataModel {
    private final HtmlData mData;
    private final DateFormat df;

    public HtmldataModel(HtmlData data, Context context) {
        this.mData = data;
        df = DateFormat.getDateTimeInstance();
    }

    public String getPageTitle(){
        if(TextUtils.isEmpty(mData.getPageTitle())) return "(No content)";
        return mData.getPageTitle();
    }
    public String getPageTitleLength(){
        return String.valueOf(mData.getPageTitle().length());
    }

    public String getCanonicalUrl(){
        if(TextUtils.isEmpty(mData.getCanonicalUrl())) return "(No content)";
        return mData.getCanonicalUrl();
    }

    public String getMetaDescription(){
        if(TextUtils.isEmpty(mData.getMetaDescription())) return "(No content)";
        return mData.getMetaDescription();
    }

    public String getMetaDescriptionLength(){
        return String.valueOf(mData.getMetaDescription().length());
    }

    public String getH1List(){
        StringBuilder sb = new StringBuilder();
        for(String s: mData.getH1TagList()){
            if(sb.length() > 0) sb.append(" • ");
            sb.append(s);
        }
        if(sb.length() == 0) return "(No content)";
        return sb.toString();
    }

    public String getH1ListLength(){
        int length = 0;
        for(String s: mData.getH1TagList()){
            length += s.length();
        }
        return String.valueOf(length);
    }

    public String getH2List(){
        StringBuilder sb = new StringBuilder();
        for(String s: mData.getH2TagList()){
            if(sb.length() > 0) sb.append(" • ");
            sb.append(s);
        }
        if(sb.length() == 0) return "(No content)";
        return sb.toString();
    }
    public String getH2ListLength(){
        int length = 0;
        for(String s: mData.getH2TagList()){
            length += s.length();
        }
        return String.valueOf(length);
    }

    public String getSsl(){
        if(mData.isSsl()) return "Yes";
        return "No";
    }

    public int getSslImage(){
        if(mData.isSsl()) return R.drawable.ic_lock_outline_black_48dp;
        return R.drawable.ic_lock_open_black_48dp;
    }


    public String getFinalUrl(){
        return mData.getFinalUrl();
    }

    public String getDateChecked(){
        return df.format(mData.getDateChecked());
    }
}
