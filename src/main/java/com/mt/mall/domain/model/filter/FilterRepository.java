package com.mt.mall.domain.model.filter;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface FilterRepository {

    Optional<Filter> filterOfId(FilterId filterId);

    void add(Filter filter);

    void remove(Filter filter);

    SumPagedRep<Filter> filtersOfQuery(FilterQuery queryParam);

    void remove(Set<Filter> filters);
}
