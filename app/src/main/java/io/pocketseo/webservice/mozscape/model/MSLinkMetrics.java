/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape.model;

import com.google.gson.annotations.SerializedName;

import io.pocketseo.model.MozScapeLink;

public class MSLinkMetrics extends MSUrlMetrics implements MozScapeLink {

    public static final int FLAG_TITLE = 1;
    public final static int FLAG_CANONICAL_URL = 4;
    public final static int FLAG_LINK_NO_FOLLOW = 1;

    public static long getBitmask() {
        return MSUrlMetrics.getBitmask()
                | FLAG_CANONICAL_URL
                | FLAG_TITLE;
    }

    public enum Scope {
        Page("page_to_page"),
        Subdomain("page_to_subdomain"),
        Domain("page_to_domain");

        private final String value;

        Scope(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Sort {
        PageAuthority("page_authority"),
        DomainAuthority("domain_authority"),
        SpamScore("spam_score");

        private final String value;

        Sort(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Filter {
        AllSource(""),
        AllLink(""),
        Internal("internal"),
        External("external"),
        Follow("follow"),
        NoFollow("nofollow"),
        Equity("equity"),
        NoEquity("nonequity"),
        Redirect301("301"),
        Redirect302("302");

        private final String value;

        Filter(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @SerializedName("uu")
    public String url;

    @SerializedName("ut")
    public String title;

    @SerializedName("lnt")
    public String anchorText;

    @SerializedName("lf")
    public int flags;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAnchorText() {
        return anchorText;
    }

    @Override
    public boolean isNoFollow() {
        return (flags & FLAG_LINK_NO_FOLLOW) == FLAG_LINK_NO_FOLLOW;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        MSLinkMetrics that = (MSLinkMetrics) o;

        if (flags != that.flags) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        return anchorText != null ? anchorText.equals(that.anchorText) : that.anchorText == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (anchorText != null ? anchorText.hashCode() : 0);
        result = 31 * result + flags;
        return result;
    }
}
