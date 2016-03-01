/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.pocketseo.webservice.alexa.AlexaWebService;
import io.pocketseo.webservice.alexa.model.AlexaData;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.observers.TestSubscriber;

/**
 * Created by pharris on 19/02/16.
 */
public class AlexaUnitTest {

    private AlexaWebService mWebService;


    @Before
    public void setup(){
        // todo: I'd like to inject this dependency
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AlexaWebService.URL_PREFIX)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

        mWebService = retrofit.create(AlexaWebService.class);
    }


    @Test
    @LargeTest
    public void check_random_hosts(){
        String[] hosts = new String[]{
//                "www.thedistance.co.uk",
                "www.slimmingworld.co.uk",
                "ibm.com",
                "www.probablynotreal.co.uk",
                "www.google.co.uk",
                "slashdot.org",
        };
        for(String host: hosts) {
           checkHost(host);
        }

    }

    private void checkHost(String host){
        System.out.println("Checking " + host);

        TestSubscriber<AlexaData> testSubscriber = new TestSubscriber<>();
        mWebService.getAlexaData(host).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<AlexaData> results = testSubscriber.getOnNextEvents();

        if(results.size() == 0) Assert.fail("No result seen");
        AlexaData firstResult = results.get(0);
        Assert.assertNotNull(firstResult);
        if(firstResult.isComplete()) {
            Assert.assertNotNull(firstResult.getPopularityText());
            Assert.assertNotNull(firstResult.getRankDelta());
            Assert.assertNotNull(firstResult.getReachRank());
        } else {
            System.out.println("Nothing found for " + host);
        }
    }
}
