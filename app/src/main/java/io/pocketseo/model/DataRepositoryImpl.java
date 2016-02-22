/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

import io.pocketseo.HtmlData;
import io.pocketseo.htmlparser.HtmlParser;
import io.pocketseo.webservice.alexa.AlexaWebService;
import io.pocketseo.webservice.alexa.model.AlexaData;
import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import retrofit2.Call;
import retrofit2.Response;

public class DataRepositoryImpl implements DataRepository {

    public interface DataCache {
        MozScape getWebsiteMetrics(String url);
        AlexaScore getAlexaScore(String url);

        void store(String url, MSUrlMetrics body);
        void store(String url, AlexaData body);
    }


    public static final String PREF_FILE = "cache";

    private final MSWebService mMozWebService;
    private final MSHelper.Authenticator mMozAuthenticator;
    private final DataCache mCache;
    private final AlexaWebService mAlexaWebService;
    private final HtmlParser mParser;

    public DataRepositoryImpl(AlexaWebService alexaWebService, MSWebService mMozWebService, MSHelper.Authenticator mMozAuthenticator, HtmlParser parser, DataCache cache) {
        mAlexaWebService = alexaWebService;
        this.mMozWebService = mMozWebService;
        this.mMozAuthenticator = mMozAuthenticator;
        this.mCache = cache;
        mParser = parser;
    }

    @Override
    public void getWebsiteMetrics(String website, boolean refresh, final Callback<MozScape> callbacks) {
        MozScape cachedValue = mCache.getWebsiteMetrics(website);
        if(null == cachedValue || refresh){
            loadWebsiteMetricsFromWeb(website, callbacks);
        } else {
            Log.d("DataRepo", String.format("Cache hit for %s", website));
            callbacks.success(cachedValue);
        }
    }

    @Override
    public void getAlexaScore(String website, boolean refresh, Callback<AlexaScore> callbacks) {
        AlexaScore cachedValue = mCache.getAlexaScore(website);
        if(null == cachedValue || refresh) {
            loadAlexaScoreFromWeb(website, callbacks);
        } else {
            callbacks.success(cachedValue);
        }
    }

    @Override
    public void getHtmldata(String url, boolean refresh, final Callback<HtmlData> callbacks) {
        String sanitisedUrl = sanitiseUrl(url);
        if(null == sanitisedUrl){
            callbacks.error("Cannot understand URL");
            return;
        }
        loadHtmlDataFromWeb(sanitisedUrl, callbacks);
    }

    private void loadHtmlDataFromWeb(String url, final Callback<HtmlData> callbacks){
        new AsyncTask<String, Void, HtmlData>(){
            public HtmlParser.ParserError error;

            @Override
            protected HtmlData doInBackground(String... params) {
                try {
                    String url = params[0];
                    return mParser.getHtmlData(url);
                } catch (HtmlParser.ParserError parserError) {
                    error = parserError;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HtmlData htmlData) {
                if(htmlData == null){
                    callbacks.error(error.getMessage());
                } else {
                    callbacks.success(htmlData);
                }
            }
        }.execute(url);
    }

    private void loadWebsiteMetricsFromWeb(final String website, final Callback<MozScape> callbacks){
        Log.d("DataRepo", String.format("Load web for %s", website));
        mMozWebService.getUrlMetrics(website, MSUrlMetrics.getBitmask(), mMozAuthenticator.getAuthenticationMap()).enqueue(new retrofit2.Callback<MSUrlMetrics>() {
            @Override
            public void onResponse(Call<MSUrlMetrics> call, Response<MSUrlMetrics> response) {
                if(response.isSuccess()) {
                    mCache.store(website, response.body());
                    callbacks.success(response.body());
                } else {
                    callbacks.error(String.format(Locale.US, "Error code %d", response.code()));
                }
            }

            @Override
            public void onFailure(Call<MSUrlMetrics> call, Throwable t) {
                callbacks.error(t.getMessage());
            }
        });
    }

    private void loadAlexaScoreFromWeb(final String website, final Callback<AlexaScore> callbacks){
        Log.d("DataRepo", String.format("Load web for %s", website));
        mAlexaWebService.getAlexaData(website).enqueue(new retrofit2.Callback<AlexaData>() {
            @Override
            public void onResponse(Call<AlexaData> call, Response<AlexaData> response) {
                if(response.isSuccess()) {
                    AlexaData alexaData = response.body();
                    if(!alexaData.isComplete()){
                        callbacks.error("Cannot get response from Alexa");
                    } else {
                        mCache.store(website, response.body());
                        callbacks.success(response.body());
                    }
                } else {
                    callbacks.error(String.format(Locale.US, "Error code %d", response.code()));
                }
            }

            @Override
            public void onFailure(Call<AlexaData> call, Throwable t) {
                callbacks.error(t.getMessage());
            }
        });
    }

    /**
     * Try to work out what the user meant for a URL - try prefixing missing "http://"
     * @param url
     * @return null, or a validated URL
     */
    public static String sanitiseUrl(String url) {
        Uri u = Uri.parse(url);

        String scheme = u.getScheme();
        if (scheme == null)  u = Uri.parse("http://" + url);

        scheme = u.getScheme();
        if(!scheme.equals("http") && !scheme.equals("https")){
            Log.e("UrlSanitise", "Scheme not recognised");
            return null;
        }

        String host = u.getHost();
        if(TextUtils.isEmpty(host)){
            Log.e("UrlSanitise", "Host not specified");
            return null;
        }

        return u.toString();
    }

}
