/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pharris on 17/02/16.
 */
public class MSUrlMetrics {
    public static final long FLAG_DOMAIN_AUTHORITY = 68719476736l;
    public static final long FLAG_HTTP_STATUS_CODE = 536870912l;
    public static final long FLAG_PAGE_AUTHORITY = 34359738368l;
    public static final long FLAG_SPAM_SCORE = 67108864l;
    public static final long FLAG_ESTABLISHED_LINKS_ROOT = 512l;
    public static final long FLAG_ESTABLISHED_LINKS_TOTAL = 2048l;

    @SerializedName("pda")
    public float domainAuthority;

    @SerializedName("us")
    public int statusCode;

    @SerializedName("upa")
    public float pageAuthority;

    @SerializedName("fspsc")
    public float spamScore;

    @SerializedName("uifq")
    public int establishedLinksRoot;

    @SerializedName("uid")
    public int establishedLinksTotal;

    public static long getBitmask(){
        return FLAG_DOMAIN_AUTHORITY
                & FLAG_HTTP_STATUS_CODE
                & FLAG_PAGE_AUTHORITY
                & FLAG_SPAM_SCORE
                & FLAG_ESTABLISHED_LINKS_ROOT
                & FLAG_ESTABLISHED_LINKS_TOTAL;
    }
}
