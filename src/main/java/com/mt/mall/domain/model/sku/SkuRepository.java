package com.mt.mall.domain.model.sku;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SkuRepository {
    SumPagedRep<Sku> skusOfQuery(SkuQuery queryParam, PageConfig pageOf);

    void add(Sku sku);

    SumPagedRep<Sku> skusOfQuery(SkuQuery skuQuery, PageConfig defaultPaging, QueryConfig queryConfig);

    Optional<Sku> skuOfId(SkuId skuId);

    void remove(Sku sku);

    void remove(Set<Sku> skus);

    void patchBatch(List<PatchCommand> commands);
}
