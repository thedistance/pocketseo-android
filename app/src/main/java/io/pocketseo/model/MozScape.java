/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import java.util.Date;

/**
 * Created by pharris on 17/02/16.
 */
public interface MozScape {
    int getStatusCode();
    float getDomainAuthority();
    float getPageAuthority();
    float getSpamScore();
    int getLinksRoot();
    int getLinksTotal();
    Date getLastIndex();
    Date getNextIndex();
}
