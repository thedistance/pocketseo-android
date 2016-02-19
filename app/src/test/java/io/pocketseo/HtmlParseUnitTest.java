/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

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
    public void check_html() {
        HtmlData data = mParser.getHtmlData("https://thedistance.co.uk/");

        Assert.assertEquals(data.getPageTitle(), "App Developers UK | Mobile App Development | The Distance, York");
    }
}
