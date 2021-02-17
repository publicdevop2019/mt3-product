package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import com.mt.mall.domain.model.catalog.CatalogRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaCatalogRepository extends CatalogRepository, JpaRepository<Catalog, Long> {
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Catalog> findByCatalogIdAndDeletedFalse(CatalogId clientId);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default CatalogId nextIdentity() {
        return new CatalogId();
    }

    default Optional<Catalog> catalogOfId(CatalogId clientId) {
        return findByCatalogIdAndDeletedFalse(clientId);
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

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery clientQuery, PageConfig clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value(), clientPaging, queryConfig);
    }

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery clientQuery, PageConfig clientPaging) {
        return getSumPagedRep(clientQuery.value(), clientPaging, new QueryConfig());
    }

    private SumPagedRep<Catalog> getSumPagedRep(String query, PageConfig page, QueryConfig config) {
        SelectQueryBuilder<Catalog> selectQueryBuilder = QueryBuilderRegistry.catalogSelectQueryBuilder();
        List<Catalog> select = selectQueryBuilder.select(query, page, Catalog.class);
        Long aLong = null;
        if (!config.isSkipCount()) {
            aLong = selectQueryBuilder.count(query, Catalog.class);
        }
        return new SumPagedRep<>(select, aLong);
    }
}
