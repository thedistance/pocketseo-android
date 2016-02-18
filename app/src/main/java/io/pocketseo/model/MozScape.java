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

    /**
     * Spam score - need to substract one from the score as described in the
     * <a href="https://moz.com/help/guides/moz-api/mozscape/getting-started-with-mozscape/spam-score">doccumentstaion</a>
     * <br />
     * 0 corresponds to "no data in our index for that subdomain."
     * A returned value of 1 corresponds to a Spam Score of 0 and an fspsc value of 18 corresponds to a Spam Score of 17.
     * @return
     */
    int getSpamScore();
    int getLinksRoot();
    int getLinksTotal();
    Date getLastIndex();
    Date getNextIndex();
}
