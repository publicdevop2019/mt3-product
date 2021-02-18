package com.mt.mall.domain.model.filter;

import com.mt.common.query.QueryCriteria;
import com.mt.common.validate.Validator;

public class FilterQuery extends QueryCriteria {

    public FilterQuery(String queryParam, boolean isPublic) {
        super(queryParam);
        if (isPublic) {
            Validator.notBlank(queryParam, "filter public query must have query value");
        }
    }

    public FilterQuery(FilterId productId) {
        super(productId);
    }
}
