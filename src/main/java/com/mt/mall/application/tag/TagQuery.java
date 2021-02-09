package com.mt.mall.application.tag;

public class TagQuery {
    private final String value;

    public TagQuery(String queryParam) {
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
