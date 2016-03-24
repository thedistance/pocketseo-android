/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape.model;

import java.util.ArrayList;

public class MSLinkFilter {

    public MSLinkMetrics.Scope scope = MSLinkMetrics.Scope.Page;
    public MSLinkMetrics.Sort sort = MSLinkMetrics.Sort.PageAuthority;
    public ArrayList<MSLinkMetrics.Filter> filters = new ArrayList<>(2);

    public MSLinkFilter() {
        filters.add(MSLinkMetrics.Filter.All);
        filters.add(MSLinkMetrics.Filter.All);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MSLinkFilter that = (MSLinkFilter) o;

        if (scope != that.scope) {
            return false;
        }
        if (sort != that.sort) {
            return false;
        }
        return filters.equals(that.filters);

    }
}
