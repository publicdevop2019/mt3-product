package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.restful.query.QueryCriteria;

import java.util.Set;

public class TagQuery extends QueryCriteria {
    public TagQuery(String queryParam) {
        super(queryParam);
    }

    public TagQuery(TagId tagId) {
        super(tagId);
    }

    public TagQuery(Set<String> collect) {
        super(collect);
    }
}
