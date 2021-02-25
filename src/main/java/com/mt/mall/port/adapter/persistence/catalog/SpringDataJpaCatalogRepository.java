package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import com.mt.mall.domain.model.catalog.CatalogRepository;
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
public interface SpringDataJpaCatalogRepository extends CatalogRepository, JpaRepository<Catalog, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default CatalogId nextIdentity() {
        return new CatalogId();
    }

    default Optional<Catalog> catalogOfId(CatalogId catalogOfId) {
        return getCatalogOfId(catalogOfId);
    }

    private Optional<Catalog> getCatalogOfId(CatalogId catalogId) {
        SelectQueryBuilder<Catalog> catalogs = QueryBuilderRegistry.catalogSelectQueryBuilder();
        List<Catalog> select = catalogs.select(new CatalogQuery(catalogId), new PageConfig(), Catalog.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
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

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query, PageConfig pageConfig, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.catalogSelectQueryBuilder(), query, pageConfig, queryConfig, Catalog.class);
    }

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query, PageConfig pageConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.catalogSelectQueryBuilder(), query, pageConfig, new QueryConfig(), Catalog.class);
    }

}
