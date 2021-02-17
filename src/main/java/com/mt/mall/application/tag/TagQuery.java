package com.mt.mall.application.tag;

import com.mt.common.query.DefaultQuery;

public class TagQuery extends DefaultQuery {
    private final String value;

    public TagQuery(String queryParam) {
        super(queryParam);
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
