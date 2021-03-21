package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.catalog.*;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if (query.getTagId() != null) {
            //in-memory search
            List<Catalog> all2 = findAll();
            List<Catalog> collect = all2.stream().filter(e -> e.getLinkedTags().stream().anyMatch(ee -> ee.getTagId().equals(query.getTagId()))).collect(Collectors.toList());
            long offset = query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
            List<Catalog> collect1 = IntStream.range(0, collect.size()).filter(i -> i >= offset && i < (offset + query.getPageConfig().getPageSize())).boxed().map(collect::get).collect(Collectors.toList());
            return new SumPagedRep<>(collect1, (long) collect.size());
        }
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

        public SumPagedRep<Catalog> execute(CatalogQuery catalogQuery) {
            QueryUtility.QueryContext<Catalog> queryContext = QueryUtility.prepareContext(Catalog.class, catalogQuery);
            Optional.ofNullable(catalogQuery.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(catalogQuery.getType().name(), Catalog_.TYPE, queryContext));
            Optional.ofNullable(catalogQuery.getParentId()).ifPresent(e -> QueryUtility.addStringEqualPredicate(catalogQuery.getParentId().getDomainId(), Catalog_.PARENT_ID, queryContext));
            Optional.ofNullable(catalogQuery.getCatalogIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(catalogQuery.getCatalogIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Catalog_.CATALOG_ID, queryContext));
            Order order = null;
            if (catalogQuery.getCatalogSort().isById())
                order = QueryUtility.getDomainIdOrder(Catalog_.CATALOG_ID, queryContext, catalogQuery.getCatalogSort().isAscending());
            if (catalogQuery.getCatalogSort().isByName())
                order = QueryUtility.getOrder(Catalog_.NAME, queryContext, catalogQuery.getCatalogSort().isAscending());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(catalogQuery, queryContext);
        }
    }
}
