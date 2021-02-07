package com.mt.mall.port.adapter.persistence;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.port.adapter.persistence.catalog.CatalogSelectQueryBuilder;
import com.mt.mall.port.adapter.persistence.filter.FilterSelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    private static CatalogSelectQueryBuilder catalogSelectQueryBuilder;
    private static FilterSelectQueryBuilder filterSelectQueryBuilder;

    public static CatalogSelectQueryBuilder catalogSelectQueryBuilder() {
        return catalogSelectQueryBuilder;
    }

    public static SelectQueryBuilder<Filter> filterSelectQueryBuilder() {
        return filterSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(CatalogSelectQueryBuilder catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(FilterSelectQueryBuilder filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
