package com.mt.mall.port.adapter.persistence.filter;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterQuery;
import com.mt.mall.domain.model.filter.FilterRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaFilterRepository extends FilterRepository, JpaRepository<Filter, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default FilterId nextIdentity() {
        return new FilterId();
    }

    default Optional<Filter> filterOfId(FilterId filterId) {
        return getFilterOfId(filterId);
    }

    private Optional<Filter> getFilterOfId(FilterId filterId) {
        SelectQueryBuilder<Filter> filterSelectQueryBuilder = QueryBuilderRegistry.filterSelectQueryBuilder();
        List<Filter> select = filterSelectQueryBuilder.select(new FilterQuery(filterId), new PageConfig(), Filter.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
    }

    default void add(Filter client) {
        save(client);
    }

    default void remove(Filter client) {
        softDelete(client.getId());
    }

    default void remove(Set<Filter> filters) {
        softDeleteAll(filters.stream().map(Filter::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Filter> filtersOfQuery(FilterQuery query, PageConfig clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(query, clientPaging, queryConfig);
    }

    default SumPagedRep<Filter> filtersOfQuery(FilterQuery query, PageConfig clientPaging) {
        return getSumPagedRep(query, clientPaging, new QueryConfig());
    }

    private SumPagedRep<Filter> getSumPagedRep(FilterQuery query, PageConfig page, QueryConfig config) {
        SelectQueryBuilder<Filter> selectQueryBuilder = QueryBuilderRegistry.filterSelectQueryBuilder();
        List<Filter> select = selectQueryBuilder.select(query, page, Filter.class);
        Long aLong = null;
        if (!config.isSkipCount()) {
            aLong = selectQueryBuilder.count(query, Filter.class);
        }
        return new SumPagedRep<>(select, aLong);
    }
}
