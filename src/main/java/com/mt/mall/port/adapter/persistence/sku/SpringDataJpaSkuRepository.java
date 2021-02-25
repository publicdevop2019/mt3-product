package com.mt.mall.port.adapter.persistence.sku;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import com.mt.mall.domain.model.sku.SkuQuery;
import com.mt.mall.domain.model.sku.SkuRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaSkuRepository extends SkuRepository, JpaRepository<Sku, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default SkuId nextIdentity() {
        return new SkuId();
    }

    default Optional<Sku> skuOfId(SkuId skuOfId) {
        return getSkuOfId(skuOfId);
    }

    private Optional<Sku> getSkuOfId(SkuId skuId) {
        SelectQueryBuilder<Sku> skuSelectQueryBuilder = QueryBuilderRegistry.skuSelectQueryBuilder();
        List<Sku> select = skuSelectQueryBuilder.select(new SkuQuery(skuId), new PageConfig(), Sku.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
    }

    default void add(Sku client) {
        save(client);
    }

    default void remove(Sku client) {
        softDelete(client.getId());
    }

    default void remove(Set<Sku> client) {
        softDeleteAll(client.stream().map(Sku::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Sku> skusOfQuery(SkuQuery query, PageConfig clientPaging, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.skuSelectQueryBuilder(), query, clientPaging, queryConfig, Sku.class);
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.skuUpdateQueryBuilder().update(commands, Sku.class);
    }

    default SumPagedRep<Sku> skusOfQuery(SkuQuery query, PageConfig clientPaging) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.skuSelectQueryBuilder(), query, clientPaging, new QueryConfig(), Sku.class);
    }
}
