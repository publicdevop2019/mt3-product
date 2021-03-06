package com.mt.mall.domain.model.product;

import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ProductQuery extends QueryCriteria {
    private Set<ProductId> productIds;
    private ProductSort productSort;
    private String tagSearch;
    private String priceSearch;
    private String name;
    private boolean isAvailable = false;
    public static final String AVAILABLE = "available";

    public ProductQuery(String queryParam, boolean isPublic) {
        if (isPublic) {
            Validator.notBlank(queryParam, "product public query must have query value");
            isAvailable = true;
        }

    }

    public ProductQuery(ProductId productId, boolean isPublic) {
        this.productIds = new HashSet<>(List.of(productId));
        if (isPublic) {
            isAvailable = true;
        }
    }

    public ProductSort getProductSort() {
        return productSort;
    }

    @Getter
    public class ProductSort {
        private boolean isById;
        private boolean isByName;
        private boolean isByTotalSale;
        private boolean isByLowestPrice;
        private boolean isByEndAt;
        private boolean isAsc;

    }
}
