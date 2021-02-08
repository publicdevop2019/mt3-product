package com.mt.mall.domain.model.sku;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.sku.SkuQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SkuRepository {
    SumPagedRep<Sku> skusOfQuery(SkuQuery queryParam, DefaultPaging pageOf);

    void add(Sku sku);

    SkuId nextIdentity();

    SumPagedRep<Sku> skusOfQuery(SkuQuery skuQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Sku> skuOfId(SkuId skuId);

    void remove(Sku sku);

    void remove(Set<Sku> skus);
}
