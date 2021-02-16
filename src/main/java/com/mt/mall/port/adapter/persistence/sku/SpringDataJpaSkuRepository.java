package com.mt.mall.port.adapter.persistence.sku;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.application.sku.SkuQuery;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import com.mt.mall.domain.model.sku.SkuRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaSkuRepository extends SkuRepository, JpaRepository<Sku, Long> {
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Sku> findBySkuIdAndDeletedFalse(SkuId skuId);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default SkuId nextIdentity() {
        return new SkuId();
    }

    default Optional<Sku> skuOfId(SkuId clientId) {
        return findBySkuIdAndDeletedFalse(clientId);
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

    default SumPagedRep<Sku> skusOfQuery(SkuQuery clientQuery, DefaultPaging clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), queryConfig.value());
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.skuUpdateQueryBuilder().update(commands, Sku.class);
    }

    default SumPagedRep<Sku> skusOfQuery(SkuQuery clientQuery, DefaultPaging clientPaging) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), null);
    }

    private SumPagedRep<Sku> getSumPagedRep(String query, String page, String config) {
        SelectQueryBuilder<Sku> selectQueryBuilder = QueryBuilderRegistry.skuSelectQueryBuilder();
        List<Sku> select = selectQueryBuilder.select(query, page, Sku.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, Sku.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
