/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.htmlparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.pocketseo.HtmlData;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmlParser {

    public HtmlParser(){

    }

    public HtmlData getHtmlDate(String url){
        return new HtmlData() {
            @Override
            public String getPageTitle() {
                return "The Distance App Developers";
            }

            @Override
            public String getCanonicalUrl() {
                return "https://thedistance.co.uk";
            }

            @Override
            public String getMetaDescription() {
                return "we make apps, we rock";
            }

            @Override
            public List<String> getH1TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("App Developers");
                testData.add("Android");
                testData.add("iOS");
                testData.add("York");
                return testData;
            }

            @Override
            public List<String> getH2TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("h2 App Developers");
                testData.add("Josh");
                testData.add("Anthony");
                testData.add("Rob");
                testData.add("Ben");
                testData.add("Pete");
                return testData;
            }

            @Override
            public boolean isSsl() {
                return true;
            }

            @Override
            public Date getDateChecked() {
                return new Date();
            }
        };
    }
}
