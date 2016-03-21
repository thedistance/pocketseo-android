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
import io.pocketseo.webservice.alexa.AlexaWebService;
import io.pocketseo.webservice.alexa.model.AlexaData;
import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import io.pocketseo.webservice.mozscape.model.MSNextUpdate;
import io.pocketseo.webservice.mozscape.model.MSUrlMetrics;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class DataRepositoryImpl implements DataRepository {

    private LruCache<String, Observable<?>> cachedObservables = new LruCache<>(10);

    public interface DataCache {
        MozScape getWebsiteMetrics(String url);

        AlexaScore getAlexaScore(String url);

        HtmlData getHtmldata(String url);

        void store(String url, MSUrlMetrics body);

        void store(String url, AlexaData body);

        void store(String url, HtmlParser.HtmlDataImpl body);
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
                .unsubscribeOn(Schedulers.io());


        Observable<MSNextUpdate> nextUpdateTime = mMozWebService.getNextUpdate(mMozAuthenticator.getAuthenticationMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        Observable<MSUrlMetrics> combined = Observable.combineLatest(webServiceResponse, nextUpdateTime, new Func2<MSUrlMetrics, MSNextUpdate, MSUrlMetrics>() {
            @Override
            public MSUrlMetrics call(MSUrlMetrics msUrlMetrics, MSNextUpdate msNextUpdate) {
                if (null == msNextUpdate || null == msUrlMetrics) {
                    return null;
                }

                msUrlMetrics.setNextCrawl(msNextUpdate.nextUpdate);
                return msUrlMetrics;
            }
        }).filter(new Func1<MSUrlMetrics, Boolean>() {
            @Override
            public Boolean call(MSUrlMetrics msUrlMetrics) {
                return msUrlMetrics != null;
            }
        }).doOnEach(new Subscriber<MSUrlMetrics>() {
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
                .concat(cacheResponse, combined)
                .first();
    }

    @Override
    public Observable<List<MozScapeLink>> getLinkMetrics(String url, int page, boolean refresh) {

        Observable<List<MSLinkMetrics>> webServiceResponse = mMozWebService.getLinks(url, MSLinkMetrics.getBitmask(), 25, 25 * (page - 1), mMozAuthenticator.getAuthenticationMap());

        return (Observable<List<MozScapeLink>>) getPreparedObservable(webServiceResponse, "links:" + url + "/" + page, refresh);
    }

    @Override
    public Observable<AlexaScore> getAlexaScore(final String website, final boolean refresh) {
        Observable<AlexaData> webServiceResponse = mAlexaWebService.getAlexaData(website)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .filter(new Func1<AlexaData, Boolean>() {
                    @Override
                    public Boolean call(AlexaData alexaData) {
                        return null != alexaData && alexaData.isComplete();
                    }
                })
                .doOnEach(new Subscriber<AlexaData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AlexaData alexaData) {
                        mCache.store(website, alexaData);
                        Log.d("DataRepo", String.format("Caching %s", website));
                    }
                });

        Observable<AlexaScore> cacheResponse = Observable.create(new Observable.OnSubscribe<AlexaScore>() {
            @Override
            public void call(Subscriber<? super AlexaScore> subscriber) {
                if (!refresh) {
                    AlexaScore cachedValue = mCache.getAlexaScore(website);
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

    private void loadAlexaScoreFromWeb(final String website, final Callback<AlexaScore> callbacks) {

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
