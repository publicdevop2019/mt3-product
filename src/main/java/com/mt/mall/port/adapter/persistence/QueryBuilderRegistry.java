package com.mt.mall.port.adapter.persistence;

import com.mt.mall.port.adapter.persistence.catalog.CatalogSelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    private static CatalogSelectQueryBuilder catalogSelectQueryBuilder;

    public static CatalogSelectQueryBuilder catalogSelectQueryBuilder() {
        return catalogSelectQueryBuilder;
    }

    @Autowired
    public void SetCatalogSelectQueryBuilder(CatalogSelectQueryBuilder catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }
}
