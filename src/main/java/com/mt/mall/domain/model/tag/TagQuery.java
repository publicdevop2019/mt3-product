package com.mt.mall.domain.model.tag;

import com.mt.common.query.QueryCriteria;

public class TagQuery extends QueryCriteria {
    public TagQuery(String queryParam) {
        super(queryParam);
    }

    public TagQuery(TagId tagId) {
        super(tagId);
    }
}
