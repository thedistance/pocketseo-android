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

import io.pocketseo.htmlparser.HtmlParser;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmlParseUnitTest {

    public static final String TEST_DATA_FILE = "./src/test/assets/thedistance.html";
    private HtmlParser mParser;

    @Before
    public void setup(){
        mParser = new HtmlParser(null);
    }

    @Test
    @LargeTest
    public void check_html_from_static_file() {
//        HtmlData data = mParser.getHtmlData("https://thedistance.co.uk/");

        InputStream is = null;
        try {
            is = new FileInputStream(TEST_DATA_FILE);
        } catch (FileNotFoundException e) {
            Assert.fail("Cannot open test data");
            return;
        }

        // parsed data
        HtmlData data = mParser.parseData(is);
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
}
