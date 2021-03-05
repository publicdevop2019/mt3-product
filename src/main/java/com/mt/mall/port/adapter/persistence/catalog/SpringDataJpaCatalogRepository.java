package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.builder.SqlSelectQueryConverter;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
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

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;

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
        SqlSelectQueryConverter<Catalog> catalogs = QueryBuilderRegistry.catalogSelectQueryBuilder();
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

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query) {
        return QueryBuilderRegistry.catalogSelectQueryBuilder().execute(query);
    }

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query, PageConfig pageConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.catalogSelectQueryBuilder(), query, pageConfig, new QueryConfig(), Catalog.class);
    }

    @Component
    class JpaCriteriaApiCatalogExecutor extends SqlSelectQueryConverter<Catalog> {
        public transient static final String NAME_LITERAL = "name";
        public transient static final String PARENT_ID_LITERAL = "parentId";
        public transient static final String TYPE_LITERAL = "type";
        public transient static final String CATALOG_ID_LITERAL = "catalogId";

        public SumPagedRep<Catalog> execute(CatalogQuery catalogQuery) {
            QueryUtility.QueryContext<Catalog> queryContext = QueryUtility.prepareContext(Catalog.class);
            Predicate stringEqualPredicate = QueryUtility.getStringEqualPredicate(catalogQuery.getType().name(), TYPE_LITERAL, queryContext);
            Predicate predicate = QueryUtility.combinePredicate(queryContext, stringEqualPredicate);
            return QueryUtility.pagedQuery(predicate,null,catalogQuery,queryContext);
        }

//        {
//            supportedSort.put(NAME_LITERAL, NAME_LITERAL);
//            supportedSort.put("id", CATALOG_ID_LITERAL);
//            supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(CATALOG_ID_LITERAL));
//            supportedWhere.put(TYPE_LITERAL, new FieldStringEqualClause<>(TYPE_LITERAL));
//            supportedWhere.put(PARENT_ID_LITERAL, new FieldStringEqualClause<>(PARENT_ID_LITERAL));
//        }
    }
}
