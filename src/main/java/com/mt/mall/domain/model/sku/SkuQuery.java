package com.mt.mall.domain.model.sku;

import com.mt.common.query.QueryCriteria;

public class SkuQuery extends QueryCriteria {

    public SkuQuery(String queryParam) {
        super(queryParam);
    }

    public SkuQuery(SkuId skuId) {
        super(skuId);
    }
}
