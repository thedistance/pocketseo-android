/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import io.pocketseo.HtmlData;
import io.pocketseo.htmlparser.HtmlParser;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;

/**
 * Created by pharris on 18/02/16.
 */
public class DataCacheImpl implements DataRepositoryImpl.DataCache {

    private final Gson mGson;
    private final SharedPreferences mPrefs;

    public DataCacheImpl(Gson gson, SharedPreferences prefs) {
        this.mGson = gson;
        this.mPrefs = prefs;
    }

    @Override
    public MozScape getWebsiteMetrics(String url) {
        String cachedValue = mPrefs.getString(url, null);
        if (null == cachedValue) {
            return null;
        }
        return mGson.fromJson(cachedValue, MSUrlMetrics.class);
    }

    @Override
    public void store(String url, MSUrlMetrics body) {
        mPrefs.edit().putString(url, mGson.toJson(body)).apply();
    }

    @Override
    public HtmlData getHtmldata(String url) {
        String cachedValue = mPrefs.getString("htmldata:" + url, null);
        if (null == cachedValue) {
            return null;
        }
        try {
            HtmlData data = mGson.fromJson(cachedValue, HtmlParser.HtmlDataImpl.class);
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void store(String url, HtmlParser.HtmlDataImpl body) {
        mPrefs.edit().putString("htmldata:" + url, mGson.toJson(body)).apply();
    }
}
