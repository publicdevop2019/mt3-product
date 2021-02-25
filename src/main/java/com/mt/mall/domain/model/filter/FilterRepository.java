package com.mt.mall.domain.model.filter;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface FilterRepository{
    FilterId nextIdentity();

    SumPagedRep<Filter> filtersOfQuery(FilterQuery filterQuery, PageConfig defaultPaging, QueryConfig queryConfig);

    Optional<Filter> filterOfId(FilterId filterId);

    void add(Filter filter);

    void remove(Filter filter);

    SumPagedRep<Filter> filtersOfQuery(FilterQuery queryParam, PageConfig queryPagingParam);

    void remove(Set<Filter> filters);
}
