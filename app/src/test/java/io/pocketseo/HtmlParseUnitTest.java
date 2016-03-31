/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.pocketseo.htmlparser.HtmlParser;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmlParseUnitTest {

    public static final String TEST_DATA_FILE = "./src/test/assets/thedistance.html";
    private HtmlParser mParser;

    @Before
    public void setup(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(0, 30, TimeUnit.SECONDS))
                .build();
        mParser = new HtmlParser(client);
    }

    @Test
    @LargeTest
    public void check_html_from_static_file() {
        InputStream is = null;
        try {
            is = new FileInputStream(TEST_DATA_FILE);
        } catch (FileNotFoundException e) {
            Assert.fail("Cannot open test data");
            return;
        }

        // parsed data
        HtmlData data = null;
        try {
            data = mParser.parseData(is);
        } catch (HtmlParser.ParserError parserError) {
            parserError.printStackTrace();
            Assert.fail(parserError.toString());
            return;        }
        if(null == data){
            Assert.fail("Data parsing failed - null response");
            return;
        }

        // what to expect
        HtmlData testMeta = HtmlParser.theDistanceMetaData();

        Assert.assertEquals("Parsed title incorrectly.", testMeta.getPageTitle(), data.getPageTitle());
        Assert.assertEquals("Parsed description incorrectly", testMeta.getMetaDescription(), data.getMetaDescription());
        Assert.assertEquals("Parsed H1 incorrectly", testMeta.getH1TagList(), data.getH1TagList());
        Assert.assertEquals("Parsed H2 incorrectly", testMeta.getH2TagList(), data.getH2TagList());
//        Assert.assertEquals("Parsed URL incorrectly", testMeta.getCanonicalUrl(), data.getCanonicalUrl());
    }

    @Test
    @LargeTest
    public void check_html_from_web(){
        HtmlData data = null;
        try {
            data = mParser.getHtmlData("http://thedistance.co.uk/");
        } catch (HtmlParser.ParserError parserError) {
            parserError.printStackTrace();
            Assert.fail(parserError.getMessage());
        }

        if(data == null){
            Assert.fail("Data parsing failed - null response");
            return;
        }

        Assert.assertNotNull("Title missing", data.getPageTitle());
        Assert.assertEquals("Canonical URL is incorrect", "https://thedistance.co.uk/", data.getCanonicalUrl());
        Assert.assertNotNull("Meta description missing", data.getMetaDescription());
        Assert.assertNotNull("H1 missing", data.getH1TagList());
        Assert.assertNotNull("h2 missing", data.getH2TagList());
        Assert.assertEquals("Redirect is incorrect", "https://thedistance.co.uk/", data.getFinalUrl());
        Assert.assertTrue("SSL not detected", data.isSsl());
    }


    @Test
    @LargeTest
    public void check_slimming_world(){
        HtmlData data = null;
        try {
            data = mParser.getHtmlData("http://www.slimmingworld.co.uk/food");
        } catch (HtmlParser.ParserError parserError) {
            parserError.printStackTrace();
            Assert.fail(parserError.getMessage());
        }

        if(data == null){
            Assert.fail("Data parsing failed - null response");
            return;
        }

        Assert.assertNotNull("Title missing", data.getPageTitle());
        Assert.assertNotNull("Meta description missing", data.getMetaDescription());
        Assert.assertNotNull("H1 missing", data.getH1TagList());
        Assert.assertNotNull("h2 missing", data.getH2TagList());
        Assert.assertEquals("Redirect is incorrect", "http://www.slimmingworld.co.uk/healthy-eating/food-optimising.aspx", data.getFinalUrl());
        Assert.assertTrue("SSL detected incorrectly", !data.isSsl());
    }

    @Test
    @LargeTest
    public void check_404_error(){
        try {
            mParser.getHtmlData("http://staging.thedistance.co.uk/notfound");
        } catch (HtmlParser.ParserError parserError) {
            Assert.assertEquals(parserError.getMessage(), "Server responded with status code 404");
        }
    }

    @Test
    @LargeTest
    public void check_random_hosts(){
        HtmlData data = null;
        String[] hosts = new String[]{
//                "http://www.thedistance.co.uk",
//                "http://www.azlyrics.com/lyrics/linkinpark/intheend.html",
                "http://www.slimmingworld.co.uk/food",
        };
        for(String host: hosts) {
            System.out.println("Checking " + host);
            try {
                data = mParser.getHtmlData(host);
            } catch (HtmlParser.ParserError parserError) {
                parserError.printStackTrace();
                Assert.fail(parserError.getMessage());
            }
            if(data == null){
                Assert.fail("Data parsing failed - null response");
                return;
            }
        }

    }
}
