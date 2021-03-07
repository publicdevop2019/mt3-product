package com.mt.mall.port.adapter.persistence.filter;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterQuery;
import com.mt.mall.domain.model.filter.FilterRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.mall.port.adapter.persistence.catalog.SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogAdaptor.CATALOG_ID_LITERAL;

@Repository
public interface SpringDataJpaFilterRepository extends FilterRepository, JpaRepository<Filter, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Filter> filterOfId(FilterId filterId) {
        return getFilterOfId(filterId);
    }

    private Optional<Filter> getFilterOfId(FilterId filterId) {
        SumPagedRep<Filter> execute = filtersOfQuery(new FilterQuery(filterId));
        return execute.findFirst();
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

    default SumPagedRep<Filter> filtersOfQuery(FilterQuery query) {
        return QueryBuilderRegistry.getFilterSelectQueryBuilder().execute(query);
    }

    @Component
    class JpaCriteriaApiFilterAdaptor {
        public transient static final String ENTITY_CATALOG_LITERAL = "catalogs";
        private static final String FILTER_ID_LITERAL = "filterId";

        public SumPagedRep<Filter> execute(FilterQuery filterQuery) {
            QueryUtility.QueryContext<Filter> queryContext = QueryUtility.prepareContext(Filter.class);
            Optional.ofNullable(filterQuery.getCatalog()).ifPresent(e -> queryContext.getPredicates().add(QueryUtility.getStringEqualPredicate(filterQuery.getCatalog(), ENTITY_CATALOG_LITERAL, queryContext)));
            Optional.ofNullable(filterQuery.getCatalogs()).ifPresent(e -> queryContext.getPredicates().add(QueryUtility.getStringLikePredicate(filterQuery.getCatalogs(), ENTITY_CATALOG_LITERAL, queryContext)));
            Optional.ofNullable(filterQuery.getFilterIds()).ifPresent(e -> queryContext.getPredicates().add(QueryUtility.getDomainIdInPredicate(filterQuery.getFilterIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), FILTER_ID_LITERAL, queryContext)));
            Predicate predicate = QueryUtility.combinePredicate(queryContext, queryContext.getPredicates());
            Order order = null;
            if (filterQuery.getFilterSort().isById())
                order = QueryUtility.getOrder(CATALOG_ID_LITERAL, queryContext, filterQuery.getFilterSort().isAsc());
            return QueryUtility.pagedQuery(predicate, order, filterQuery, queryContext);
        }
    }
}
