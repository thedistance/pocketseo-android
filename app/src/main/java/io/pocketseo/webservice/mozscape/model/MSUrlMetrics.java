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
    public static final long FLAG_ESTABLISHED_LINKS_TOTAL = 2048l;
    public static final long FLAG_LAST_CRAWL = 144115188075855872l;

    @SerializedName("pda")
    public float domainAuthority;

    @SerializedName("us")
    public int statusCode;

    @SerializedName("upa")
    public float pageAuthority;

    @SerializedName("uid")
    public int establishedLinksTotal;

    @SerializedName("ulc")
    public long lastCrawl;

    public static long getBitmask(){
        return FLAG_DOMAIN_AUTHORITY
                | FLAG_HTTP_STATUS_CODE
                | FLAG_PAGE_AUTHORITY
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
    public int getLinksTotal() {
        return establishedLinksTotal;
    }

    @Override
    public Date getLastIndex() {
        if(0 == lastCrawl) return null;
        return new Date(lastCrawl * 1000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MSUrlMetrics metrics = (MSUrlMetrics) o;

        if (Float.compare(metrics.domainAuthority, domainAuthority) != 0) {
            return false;
        }
        if (statusCode != metrics.statusCode) {
            return false;
        }
        if (Float.compare(metrics.pageAuthority, pageAuthority) != 0) {
            return false;
        }
        if (establishedLinksTotal != metrics.establishedLinksTotal) {
            return false;
        }
        return lastCrawl == metrics.lastCrawl;

    }

    @Override
    public int hashCode() {
        int result = (domainAuthority != +0.0f ? Float.floatToIntBits(domainAuthority) : 0);
        result = 31 * result + statusCode;
        result = 31 * result + (pageAuthority != +0.0f ? Float.floatToIntBits(pageAuthority) : 0);
        result = 31 * result + establishedLinksTotal;
        result = 31 * result + (int) (lastCrawl ^ (lastCrawl >>> 32));
        return result;
    }
}
