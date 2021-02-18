package com.mt.mall.domain.model.product;

import com.mt.common.query.QueryCriteria;
import com.mt.common.validate.Validator;

public class ProductQuery extends QueryCriteria {

    public static final String AVAILABLE = "available";

    public ProductQuery(String queryParam, boolean isPublic) {
        super(queryParam);
        if (isPublic) {
            Validator.notBlank(queryParam, "product public query must have query value");
            parsed.put(AVAILABLE, "1");
        }

    }

    public ProductQuery(ProductId productId, boolean isPublic) {
        super(productId);
        if (isPublic) {
            parsed.put(AVAILABLE, "1");
        }
    }
}
