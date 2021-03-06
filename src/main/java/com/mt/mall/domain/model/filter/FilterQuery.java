package com.mt.mall.domain.model.filter;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;

import java.util.Set;

@Getter
public class FilterQuery extends QueryCriteria {
    private Set<FilterId> filterIds;
    private String catalog;
    private String catalogs;
    private PageConfig clientPaging = PageConfig.defaultConfig();
    private QueryConfig queryConfig = QueryConfig.skipCount();

    public FilterQuery(String queryParam, boolean isPublic) {
        if (isPublic) {
            Validator.notBlank(queryParam, "filter public query must have query value");
        }
    }

    public FilterQuery(FilterId productId) {

    }

    public FilterSort getFilterSort() {
        return null;
    }

    @Getter
    public static class FilterSort {
        private boolean isById;
        private boolean isAsc;
    }
}
