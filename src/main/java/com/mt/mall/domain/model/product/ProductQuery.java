package com.mt.mall.domain.model.product;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.tag.TagId;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ProductQuery extends QueryCriteria {
    private Set<ProductId> productIds;
    private ProductSort productSort;
    private String tagSearch;
    private String priceSearch;
    private Set<String> names;
    private boolean isAvailable = false;
    private boolean isPublic = false;
    private TagId tagId;
    public static final String AVAILABLE = "available";

    public ProductQuery(String queryParam) {
        updateQueryParam(queryParam);
        setPageConfig();
        setQueryConfig(QueryConfig.countRequired());
        setProductSort(this.pageConfig);
    }

    public ProductQuery(ProductId productId, boolean isPublic) {
        this.isPublic = isPublic;
        if (this.isPublic) {
            this.isAvailable = true;
        }
        this.productIds = new HashSet<>(List.of(productId));
        setPageConfig();
        setQueryConfig(QueryConfig.skipCount());
        setProductSort(this.pageConfig);
    }

    public ProductQuery(String queryParam, String pageParam, String skipCount, boolean isPublic) {
        this.isPublic = isPublic;
        updateQueryParam(queryParam);
        setPageConfig(pageParam);
        setQueryConfig(new QueryConfig(skipCount));
        setProductSort(this.pageConfig);
    }

    public ProductQuery(TagId tagId) {
        setPageConfig();
        setQueryConfig(QueryConfig.countRequired());
        this.tagId = tagId;
    }

    private void setPageConfig() {
        if (isPublic) {
            this.pageConfig = PageConfig.limited(null, 20);
        } else {
            this.pageConfig = PageConfig.limited(null, 100);
        }
    }

    private void setProductSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id"))
            this.productSort = ProductSort.isById(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase("name"))
            this.productSort = ProductSort.isByName(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase("totalSales"))
            this.productSort = ProductSort.isByTotalSale(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase("lowestPrice"))
            this.productSort = ProductSort.isByLowestPrice(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase("endAt"))
            this.productSort = ProductSort.isByEndAt(pageConfig.isSortOrderAsc());
    }

    private void setPageConfig(String pageParam) {
        if (isPublic) {
            this.pageConfig = PageConfig.limited(pageParam, 20);
        } else {
            this.pageConfig = PageConfig.limited(pageParam, 100);
        }
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        Optional.ofNullable(stringStringMap.get("attr")).ifPresent(e -> tagSearch = e);
        Optional.ofNullable(stringStringMap.get("attributes")).ifPresent(e -> tagSearch = e);
        Optional.ofNullable(stringStringMap.get("id")).ifPresent(e -> {
            String[] split = e.split("\\.");
            this.productIds = Arrays.stream(split).map(ProductId::new).collect(Collectors.toSet());
        });
        priceSearch = stringStringMap.get("lowestPrice");
        Optional.ofNullable(stringStringMap.get("name")).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        if (isPublic) {
            isAvailable = true;
        }
        if (isPublic) {
            if (names == null && priceSearch == null && tagSearch == null && (productIds == null || productIds.isEmpty()))
                throw new IllegalArgumentException("public query must have value");
        }
    }

    @Getter
    public static class ProductSort {
        private boolean isById;
        private boolean isByName;
        private boolean isByTotalSale;
        private boolean isByLowestPrice;
        private boolean isByEndAt;
        private final boolean isAsc;

        public static ProductSort isById(boolean isAsc) {
            ProductSort productSort = new ProductSort(isAsc);
            productSort.isById = true;
            return productSort;
        }

        public static ProductSort isByName(boolean isAsc) {
            ProductSort productSort = new ProductSort(isAsc);
            productSort.isByName = true;
            return productSort;
        }

        public static ProductSort isByTotalSale(boolean isAsc) {
            ProductSort productSort = new ProductSort(isAsc);
            productSort.isByTotalSale = true;
            return productSort;
        }

        public static ProductSort isByLowestPrice(boolean isAsc) {
            ProductSort productSort = new ProductSort(isAsc);
            productSort.isByLowestPrice = true;
            return productSort;
        }

        public static ProductSort isByEndAt(boolean isAsc) {
            ProductSort productSort = new ProductSort(isAsc);
            productSort.isByEndAt = true;
            return productSort;
        }

        private ProductSort(boolean isAsc) {
            this.isAsc = isAsc;
        }
    }
}
