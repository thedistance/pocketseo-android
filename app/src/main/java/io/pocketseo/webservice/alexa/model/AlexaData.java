/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.alexa.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.pocketseo.model.AlexaScore;

/**
 * Created by pharris on 18/02/16.
 */
@Root(name = "ALEXA")
public class AlexaData implements AlexaScore {
/*

<?xml version="1.0" encoding="UTF-8"?>
<!-- Need more Alexa data?  Find our APIs here: https://aws.amazon.com/alexa/ -->
<ALEXA VER="0.9" URL="thedistance.co.uk/" HOME="0" AID="=" IDN="thedistance.co.uk/">
    <SD>
        <POPULARITY URL="thedistance.co.uk/" TEXT="937679" SOURCE="panel"/>
        <REACH RANK="904536"/>
        <RANK DELTA="-550943"/>
    </SD>
</ALEXA>
 */
    @Attribute(name = "VER")
    float version;
    @Attribute(name = "URL")
    String url;
    @Attribute(name = "HOME")
    int home;
    @Attribute(name = "IDN")
    String idn;
    @Attribute(name = "AID")
    String aid;

    @Element(name = "SD")
    SearchDomain domain;

    public static class SearchDomain{
        @Element(name="POPULARITY")
        Popularity popularity;

        @Element(name="REACH")
        Reach reach;

        @Element(name="RANK")
        Rank rank;
    }

    public static class Popularity {
        @Attribute(name="URL")
        String url;

        @Attribute(name="TEXT")
        int text;

        @Attribute(name="SOURCE")
        String source;

    }

    public static class Reach {
        @Attribute(name="RANK")
        int rank;
    }

    public static class Rank {
        @Attribute(name="DELTA")
        int delta;
    }

    @Override
    public int getPopularityText() {
        return domain.popularity.text;
    }

    @Override
    public int getReachRank() {
        return domain.reach.rank;
    }

    @Override
    public int getRankDelta() {
        return domain.rank.delta;
    }
}
