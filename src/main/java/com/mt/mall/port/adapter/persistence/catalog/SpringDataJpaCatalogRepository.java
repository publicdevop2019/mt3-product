package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import com.mt.mall.domain.model.catalog.CatalogRepository;
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

@Repository
public interface SpringDataJpaCatalogRepository extends CatalogRepository, JpaRepository<Catalog, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Catalog> catalogOfId(CatalogId catalogId) {
        SumPagedRep<Catalog> execute = catalogsOfQuery(new CatalogQuery(catalogId));
        return execute.findFirst();
    }

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query) {
        return QueryBuilderRegistry.getCatalogSelectQueryBuilder().execute(query);
    }

    default void add(Catalog client) {
        save(client);
    }

    default void remove(Catalog client) {
        softDelete(client.getId());
    }

    default void remove(Set<Catalog> client) {
        softDeleteAll(client.stream().map(Catalog::getId).collect(Collectors.toSet()));
    }

    @Component
    class JpaCriteriaApiCatalogAdaptor {
        public transient static final String NAME_LITERAL = "name";
        public transient static final String PARENT_ID_LITERAL = "parentId";
        public transient static final String TYPE_LITERAL = "type";
        public transient static final String CATALOG_ID_LITERAL = "catalogId";

        public SumPagedRep<Catalog> execute(CatalogQuery catalogQuery) {
            QueryUtility.QueryContext<Catalog> queryContext = QueryUtility.prepareContext(Catalog.class);
            Predicate typePredicate = QueryUtility.getStringEqualPredicate(catalogQuery.getType().name(), TYPE_LITERAL, queryContext);
            Predicate parentIdPredicate = QueryUtility.getStringEqualPredicate(catalogQuery.getParentId().getDomainId(), PARENT_ID_LITERAL, queryContext);
            Predicate domainIdPredicate = QueryUtility.getStringInPredicate(catalogQuery.getCatalogIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), CATALOG_ID_LITERAL, queryContext);
            Predicate predicate = QueryUtility.combinePredicate(queryContext, typePredicate, domainIdPredicate, parentIdPredicate);
            Order order = null;
            if (catalogQuery.getCatalogSort().isById())
                order = QueryUtility.getOrder(CATALOG_ID_LITERAL, queryContext, catalogQuery.getCatalogSort().isAscending());
            if (catalogQuery.getCatalogSort().isById())
                order = QueryUtility.getOrder(NAME_LITERAL, queryContext, catalogQuery.getCatalogSort().isAscending());
            return QueryUtility.pagedQuery(predicate, order, catalogQuery, queryContext);
        }
    }
}
