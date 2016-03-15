/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape.model;

import com.google.gson.annotations.SerializedName;

public class MSLinkMetrics extends MSUrlMetrics {

    public static final int FLAG_TITLE = 1;
    public final static int FLAG_CANONICAL_URL = 4;

    public static long getBitmask(){
        return MSUrlMetrics.getBitmask()
                | FLAG_CANONICAL_URL
                | FLAG_TITLE;
    }

    @SerializedName("uu")
    public String url;

    @SerializedName("ut")
    public String title;

    @SerializedName("lnt")
    public String anchorText;
}
