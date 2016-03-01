/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.pocketseo.model.MozScape;

/**
 * Created by pharris on 17/02/16.
 */
public class MSUrlMetrics implements MozScape {
    public static final long FLAG_DOMAIN_AUTHORITY = 68719476736l;
    public static final long FLAG_HTTP_STATUS_CODE = 536870912l;
    public static final long FLAG_PAGE_AUTHORITY = 34359738368l;
    public static final long FLAG_SPAM_SCORE = 67108864l;
    public static final long FLAG_ESTABLISHED_LINKS_ROOT = 512l;
    public static final long FLAG_ESTABLISHED_LINKS_TOTAL = 2048l;
    public static final long FLAG_LAST_CRAWL = 144115188075855872l;

    long nextCrawl;

    @SerializedName("pda")
    public float domainAuthority;

    @SerializedName("us")
    public int statusCode;

    @SerializedName("upa")
    public float pageAuthority;

    @SerializedName("fspsc")
    public int spamScore;

    @SerializedName("uifq")
    public int establishedLinksRoot;

    @SerializedName("uid")
    public int establishedLinksTotal;

    @SerializedName("ulc")
    public long lastCrawl;

    public static long getBitmask(){
        return FLAG_DOMAIN_AUTHORITY
                | FLAG_HTTP_STATUS_CODE
                | FLAG_PAGE_AUTHORITY
                | FLAG_SPAM_SCORE
                | FLAG_ESTABLISHED_LINKS_ROOT
                | FLAG_ESTABLISHED_LINKS_TOTAL
                | FLAG_LAST_CRAWL;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public float getDomainAuthority() {
        return domainAuthority;
    }

    @Override
    public float getPageAuthority() {
        return pageAuthority;
    }

    @Override
    public int getSpamScore() {
        return spamScore;
    }

    @Override
    public int getLinksRoot() {
        return establishedLinksRoot;
    }

    @Override
    public int getLinksTotal() {
        return establishedLinksTotal;
    }

    @Override
    public Date getLastIndex() {
        if(0 == lastCrawl) return null;
        return new Date(lastCrawl * 1000);
    }

    @Override
    public Date getNextIndex() {
        if(0 == nextCrawl) return null;
        return new Date(nextCrawl* 1000);
    }

    public void setNextCrawl(long nextCrawl) {
        this.nextCrawl = nextCrawl;
    }
}
