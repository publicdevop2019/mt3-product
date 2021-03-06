package com.mt.mall.domain.model.sku;

import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.mall.domain.model.product.ProductId;
import lombok.Getter;

import java.util.Set;

@Getter
public class SkuQuery extends QueryCriteria {
    private Set<SkuId> skuIds;
    private ProductId productId;
    private SkuSort skuSort;

    public SkuQuery(String queryParam) {

    }

    public SkuQuery(SkuId skuId) {
    }

    public SkuSort getSkuSort() {
        return skuSort;
    }

    @Getter
    public static class SkuSort {
        private boolean isById;
        private boolean isAsc;
    }
}
