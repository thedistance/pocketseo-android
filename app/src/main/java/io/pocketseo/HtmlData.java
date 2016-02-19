/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import java.util.Date;
import java.util.List;

public interface HtmlData {
    String getPageTitle();
    String getCanonicalUrl();
    String getMetaDescription();
    List<String> getH1TagList();
    List<String> getH2TagList();
    boolean isSsl();

    Date getDateChecked();
}
