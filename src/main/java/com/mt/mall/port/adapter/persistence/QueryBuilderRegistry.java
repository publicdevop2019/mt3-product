package com.mt.mall.port.adapter.persistence;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.port.adapter.persistence.catalog.CatalogQueryBuilder;
import com.mt.mall.port.adapter.persistence.filter.FilterQueryBuilder;
import com.mt.mall.port.adapter.persistence.product.ProductQueryBuilder;
import com.mt.mall.port.adapter.persistence.product.ProductUpdateQueryBuilder;
import com.mt.mall.port.adapter.persistence.sku.SkuQueryBuilder;
import com.mt.mall.port.adapter.persistence.sku.SkuUpdateQueryBuilder;
import com.mt.mall.port.adapter.persistence.tag.TagQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    private static CatalogQueryBuilder catalogSelectQueryBuilder;
    private static FilterQueryBuilder filterSelectQueryBuilder;
    private static SkuQueryBuilder skuSelectQueryBuilder;
    private static TagQueryBuilder tagSelectQueryBuilder;
    private static ProductQueryBuilder productSelectQueryBuilder;
    private static ProductUpdateQueryBuilder productUpdateQueryBuilder;
    private static SkuUpdateQueryBuilder skuUpdateQueryBuilder;

    public static TagQueryBuilder tagSelectQueryBuilder() {
        return tagSelectQueryBuilder;
    }

    public static SkuUpdateQueryBuilder skuUpdateQueryBuilder() {
        return skuUpdateQueryBuilder;
    }

    public static ProductUpdateQueryBuilder productUpdateQueryBuilder() {
        return productUpdateQueryBuilder;
    }

    public static ProductQueryBuilder productSelectQueryBuilder() {
        return productSelectQueryBuilder;
    }

    public static CatalogQueryBuilder catalogSelectQueryBuilder() {
        return catalogSelectQueryBuilder;
    }

    public static SkuQueryBuilder skuSelectQueryBuilder() {
        return skuSelectQueryBuilder;
    }

    public static SelectQueryBuilder<Filter> filterSelectQueryBuilder() {
        return filterSelectQueryBuilder;
    }

    @Autowired
    public void setSkuUpdateQueryBuilder(SkuUpdateQueryBuilder skuUpdateQueryBuilder) {
        QueryBuilderRegistry.skuUpdateQueryBuilder = skuUpdateQueryBuilder;
    }

    @Autowired
    public void setProductUpdateQueryBuilder(ProductUpdateQueryBuilder productUpdateQueryBuilder) {
        QueryBuilderRegistry.productUpdateQueryBuilder = productUpdateQueryBuilder;
    }

    @Autowired
    public void setProductSelectQueryBuilder(ProductQueryBuilder productSelectQueryBuilder) {
        QueryBuilderRegistry.productSelectQueryBuilder = productSelectQueryBuilder;
    }

    @Autowired
    public void setTagSelectQueryBuilder(TagQueryBuilder tagSelectQueryBuilder) {
        QueryBuilderRegistry.tagSelectQueryBuilder = tagSelectQueryBuilder;
    }

    @Autowired
    public void setSkuSelectQueryBuilder(SkuQueryBuilder skuSelectQueryBuilder) {
        QueryBuilderRegistry.skuSelectQueryBuilder = skuSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(CatalogQueryBuilder catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(FilterQueryBuilder filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
