package com.mt.mall.domain.model.filter;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.filter.FilterQuery;

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
