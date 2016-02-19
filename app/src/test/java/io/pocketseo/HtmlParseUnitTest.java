/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.pocketseo.htmlparser.HtmlParser;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmlParseUnitTest {

    private HtmlParser mParser;

    @Before
    public void setup(){
        mParser = new HtmlParser();
    }

    @Test
    @LargeTest
    public void check_html() {
        HtmlData data = mParser.getHtmlData("https://thedistance.co.uk/");

        HtmlData testMega = HtmlParser.theDistanceMetaData();

        Assert.assertEquals("Parsed title incorrectly.", data.getPageTitle(), testMega.getPageTitle());
        Assert.assertEquals("Parsed description incorrectly", data.getMetaDescription(), testMega.getMetaDescription());
        Assert.assertEquals("Parsed URL incorrectly", data.getCanonicalUrl(), testMega.getCanonicalUrl());
        Assert.assertEquals("Parsed H1 incorrectly", data.getH1TagList(), testMega.getH1TagList());
        Assert.assertEquals("Parsed H2 incorrectly", data.getH2TagList(), testMega.getH2TagList());
    }
}
