/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import io.pocketseo.webservice.mozscape.MSHelper;
import io.pocketseo.webservice.mozscape.MSWebService;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.observers.TestSubscriber;

@RunWith(JUnit4.class)
public class MozUnitTest {

    MSWebService webService;

    @Before
    public void setup() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MSWebService.URL_PREFIX)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

        webService = retrofit.create(MSWebService.class);
    }

    @Test
    public void testTheDistance() throws Exception {

        MSHelper.Authenticator authenticator = new MSHelper.Authenticator(BuildConfig.MOZSCAPE_ACCESS_KEY, BuildConfig.MOZSCAPE_SECRET_KEY, 300);

        TestSubscriber<List<MSLinkMetrics>> subscriber = new TestSubscriber<>();

        webService.getLinks("thedistance.co.uk", MSLinkMetrics.getBitmask(), 25, 0, authenticator.getAuthenticationMap())
                .subscribe(subscriber);

        List<List<MSLinkMetrics>> events = subscriber.getOnNextEvents();
        List<Throwable> errors = subscriber.getOnErrorEvents();

        for (Throwable throwable : errors) {
            System.out.println(throwable.getLocalizedMessage());
        }
        for (List<MSLinkMetrics> result : events) {
            for (MSLinkMetrics metrics : result) {
                System.out.println(metrics.url);
            }
        }

    }

    @Test
    public void testTheDistanceFilters() throws Exception {

        MSHelper.Authenticator authenticator = new MSHelper.Authenticator(BuildConfig.MOZSCAPE_ACCESS_KEY, BuildConfig.MOZSCAPE_SECRET_KEY, 300);

        TestSubscriber<List<MSLinkMetrics>> subscriber = new TestSubscriber<>();

        webService.getLinks("thedistance.co.uk", MSLinkMetrics.getBitmask(), 25, 0, "page_to_page", "spam_score", "", authenticator.getAuthenticationMap())
                .subscribe(subscriber);

        List<List<MSLinkMetrics>> events = subscriber.getOnNextEvents();
        List<Throwable> errors = subscriber.getOnErrorEvents();

        for (Throwable throwable : errors) {
            System.out.println(throwable.getLocalizedMessage());
        }
        for (List<MSLinkMetrics> result : events) {
            for (MSLinkMetrics metrics : result) {
                System.out.println(metrics.url);
            }
        }

    }
}
