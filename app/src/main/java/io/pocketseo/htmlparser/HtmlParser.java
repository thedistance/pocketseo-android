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

    public static HtmlData theDistanceMetaData(){
        return new HtmlData() {
            @Override
            public String getPageTitle() {
                return "App Developers UK | Mobile App Development | The Distance, York";
            }

            @Override
            public String getCanonicalUrl() {
                return "https://thedistance.co.uk/";
            }

            @Override
            public String getMetaDescription() {
                return "We are award winning UK app developers UK who develop mobile app development solutions for IOS & Android for B2C, B2B & Enterprise. Call York team today.";
            }

            @Override
            public List<String> getH1TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("The Yorkshire & UK leading mobile app developers");
                return testData;
            }

            @Override
            public List<String> getH2TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                for(String s: new String[]{"Mobile App Consultancy",
                        "Mobile App Development",
                        "Mobile App UI/UX",
                        "Trusted By",
                        "OUR TOOLS",
                        "PLATFORMS",
                        "TELL US YOUR APP IDEA"}){
                    testData.add(s);
                }
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

    public HtmlData getHtmlData(String url){
        return theDistanceMetaData();
    }
}
