package com.mt.mall.domain.model.filter;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.filter.FilterQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    FilterId nextIdentity();

    SumPagedRep<Filter> filtersOfQuery(FilterQuery filterQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Filter> filterOfId(FilterId filterId);

    void add(Filter filter);

    void remove(Filter filter);

    SumPagedRep<Filter> filtersOfQuery(FilterQuery queryParam, DefaultPaging queryPagingParam);

    void remove(Set<Filter> filters);
}
