/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.model;

import java.util.Date;

/**
 * Created by pharris on 17/02/16.
 */
public class MozScape {

    int statusCode;
    int domainAuthority;
    int pageAuthority;
    int spamScore;
    int linksRoot;
    int linksTotal;
    Date lastIndex;
    Date nextIndex;
}
