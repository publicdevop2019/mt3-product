package com.mt.mall.domain.model.sku;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.product.ProductId;
import lombok.Getter;

import java.util.*;

@Getter
public class SkuQuery extends QueryCriteria {
    private Set<SkuId> skuIds;
    private ProductId productId;
    private SkuSort skuSort;

    public SkuQuery(String queryParam) {
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig(PageConfig.defaultConfig());
        updateQueryParam(queryParam);
        setSkuSort(pageConfig);
    }

    public SkuQuery(SkuId skuId) {
        this.skuIds = new HashSet<>(List.of(skuId));
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
        setSkuSort(pageConfig);
    }

    public SkuQuery(String queryParam, String pageParam, String skipCount) {
        setQueryConfig(new QueryConfig(skipCount));
        setPageConfig(PageConfig.limited(pageParam, 200));
        updateQueryParam(queryParam);
        setSkuSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        Optional.ofNullable(stringStringMap.get("referenceId")).ifPresent(e -> {
            productId = new ProductId(e);
        });
    }

    private void setSkuSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.skuSort = SkuSort.byId(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class SkuSort {
        private boolean isById;
        private final boolean isAsc;

        private SkuSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static SkuSort byId(boolean isAsc) {
            SkuSort skuSort = new SkuSort(isAsc);
            skuSort.isById = true;
            return skuSort;
        }
    }
}
