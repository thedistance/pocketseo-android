/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import io.pocketseo.HtmlData;
import io.pocketseo.htmlparser.HtmlParser;
import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSLinkFilter;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.thedistance.thedistancekit.text.StringUtils;

public class DataRepositoryImpl implements DataRepository {

    private LruCache<String, Observable<?>> cachedObservables = new LruCache<>(10);

    public interface DataCache {
        MozScape getWebsiteMetrics(String url);

        HtmlData getHtmldata(String url);

        void store(String url, MSUrlMetrics body);

        void store(String url, HtmlParser.HtmlDataImpl body);
    }


    public static final String PREF_FILE = "cache";

    private final MSWebService mMozWebService;
    private final MSHelper.Authenticator mMozAuthenticator;
    private final DataCache mCache;
    private final HtmlParser mParser;

    public DataRepositoryImpl(MSWebService mMozWebService, MSHelper.Authenticator mMozAuthenticator, HtmlParser parser, DataCache cache) {
        this.mMozWebService = mMozWebService;
        this.mMozAuthenticator = mMozAuthenticator;
        this.mCache = cache;
        mParser = parser;
    }

    private Observable<?> getPreparedObservable(Observable<?> unPrepared, String key, boolean useCache) {

        Observable<?> prepared = null;
        if (useCache) {
            prepared = cachedObservables.get(key);
        }

        if (prepared != null) {
            return prepared;
        }

        prepared = unPrepared.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .cache();

        cachedObservables.put(key, prepared);

        return prepared;
    }

    @Override
    public Observable<MozScape> getWebsiteMetrics(final String website, final boolean refresh) {
        Observable<MSUrlMetrics> webServiceResponse = mMozWebService.getUrlMetrics(website, MSUrlMetrics.getBitmask(), mMozAuthenticator.getAuthenticationMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
        .doOnEach(new Subscriber<MSUrlMetrics>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MSUrlMetrics msUrlMetrics) {
                mCache.store(website, msUrlMetrics);
                Log.d("DataRepo", String.format("Caching %s", website));
            }
        });
        ;

        Observable<MozScape> cacheResponse = Observable.create(new Observable.OnSubscribe<MozScape>() {
            @Override
            public void call(Subscriber<? super MozScape> subscriber) {
                if (!refresh) {
                    MozScape cachedValue = mCache.getWebsiteMetrics(website);
                    if (null != cachedValue) {
                        Log.d("DataRepo", String.format("Cache hit for %s", website));
                        subscriber.onNext(cachedValue);
                    }
                }
                subscriber.onCompleted();
            }
        });

        return Observable
                .concat(cacheResponse, webServiceResponse)
                .first();
    }

    @Override
    public Observable<List<MozScapeLink>> getLinkMetrics(String url, int page, MSLinkFilter filter, boolean refresh) {


//        Observable<List<MSLinkMetrics>> webServiceResponse = mMozWebService.getLinks(url, MSLinkMetrics.getBitmask(), 25, 25 * (page - 1), mMozAuthenticator.getAuthenticationMap());

        String filterString = StringUtils.join(filter.filters, "+");
        Observable<List<MSLinkMetrics>> webServiceResponse = mMozWebService.getLinks(url, MSLinkMetrics.getBitmask(), 25, 25 * (page - 1),
                filter.scope.toString(), filter.sort.toString(), filterString, mMozAuthenticator.getAuthenticationMap());

        return (Observable<List<MozScapeLink>>) getPreparedObservable(webServiceResponse, "links:" + url + "/" + page, !refresh);
    }

    @Override
    public Observable<HtmlData> getHtmldata(final String url, final boolean refresh) {
        Observable<HtmlParser.HtmlDataImpl> webServiceResponse =
                Observable.create(new Observable.OnSubscribe<HtmlParser.HtmlDataImpl>() {
                    @Override
                    public void call(Subscriber<? super HtmlParser.HtmlDataImpl> subscriber) {
                        try {
                            String sanitisedUrl = sanitiseUrl(url);
                            if (null == sanitisedUrl) {
                                subscriber.onError(new HtmlParser.ParserError("Cannot understand URL"));
                            } else {
                                HtmlParser.HtmlDataImpl data = mParser.getHtmlData(sanitisedUrl);
                                subscriber.onNext(data);
                            }
                        } catch (HtmlParser.ParserError parserError) {
                            parserError.printStackTrace();
                            subscriber.onError(parserError);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .doOnEach(new Subscriber<HtmlParser.HtmlDataImpl>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(HtmlParser.HtmlDataImpl htmlData) {
                                mCache.store(url, htmlData);
                                Log.d("DataRepo", String.format("Caching %s", url));
                            }
                        });

        Observable<HtmlData> cacheResponse = Observable.create(new Observable.OnSubscribe<HtmlData>() {
            @Override
            public void call(Subscriber<? super HtmlData> subscriber) {
                if (!refresh) {
                    HtmlData cachedValue = mCache.getHtmldata(url);
                    if (null != cachedValue) {
                        Log.d("DataRepo", String.format("Cache hit for %s", url));
                        subscriber.onNext(cachedValue);
                    }
                }
                subscriber.onCompleted();
            }
        });

        return Observable
                .concat(cacheResponse, webServiceResponse)
                .first();
    }

    private void loadHtmlDataFromWeb(String url, final Callback<HtmlData> callbacks) {
        new AsyncTask<String, Void, HtmlData>() {
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
                if (htmlData == null) {
                    callbacks.error(error.getMessage());
                } else {
                    callbacks.success(htmlData);
                }
            }
        }.execute(url);
    }

    /**
     * Try to work out what the user meant for a URL - try prefixing missing "http://"
     *
     * @param url
     * @return null, or a validated URL
     */
    public static String sanitiseUrl(String url) {
        Uri u = Uri.parse(url);

        String scheme = u.getScheme();
        if (scheme == null) {
            u = Uri.parse("http://" + url);
        }

        scheme = u.getScheme();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            Log.e("UrlSanitise", "Scheme not recognised");
            return null;
        }

        String host = u.getHost();
        if (TextUtils.isEmpty(host)) {
            Log.e("UrlSanitise", "Host not specified");
            return null;
        }

        return u.toString();
    }

}
