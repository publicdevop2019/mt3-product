package com.mt.mall.port.adapter.persistence;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.port.adapter.persistence.catalog.CatalogQueryBuilder;
import com.mt.mall.port.adapter.persistence.filter.FilterSelectQueryBuilder;
import com.mt.mall.port.adapter.persistence.product.ProductSelectQueryBuilder;
import com.mt.mall.port.adapter.persistence.product.ProductUpdateQueryBuilder;
import com.mt.mall.port.adapter.persistence.sku.SkuSelectQueryBuilder;
import com.mt.mall.port.adapter.persistence.sku.SkuUpdateQueryBuilder;
import com.mt.mall.port.adapter.persistence.tag.TagSelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    private static CatalogQueryBuilder catalogSelectQueryBuilder;
    private static FilterSelectQueryBuilder filterSelectQueryBuilder;
    private static SkuSelectQueryBuilder skuSelectQueryBuilder;
    private static TagSelectQueryBuilder tagSelectQueryBuilder;
    private static ProductSelectQueryBuilder productSelectQueryBuilder;
    private static ProductUpdateQueryBuilder productUpdateQueryBuilder;
    private static SkuUpdateQueryBuilder skuUpdateQueryBuilder;

    public static TagSelectQueryBuilder tagSelectQueryBuilder() {
        return tagSelectQueryBuilder;
    }

    public static SkuUpdateQueryBuilder skuUpdateQueryBuilder() {
        return skuUpdateQueryBuilder;
    }

    public static ProductUpdateQueryBuilder productUpdateQueryBuilder() {
        return productUpdateQueryBuilder;
    }

    public static ProductSelectQueryBuilder productSelectQueryBuilder() {
        return productSelectQueryBuilder;
    }

    public static CatalogQueryBuilder catalogSelectQueryBuilder() {
        return catalogSelectQueryBuilder;
    }

    public static SkuSelectQueryBuilder skuSelectQueryBuilder() {
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
    public void setProductSelectQueryBuilder(ProductSelectQueryBuilder productSelectQueryBuilder) {
        QueryBuilderRegistry.productSelectQueryBuilder = productSelectQueryBuilder;
    }

    @Autowired
    public void setTagSelectQueryBuilder(TagSelectQueryBuilder tagSelectQueryBuilder) {
        QueryBuilderRegistry.tagSelectQueryBuilder = tagSelectQueryBuilder;
    }

    @Autowired
    public void setSkuSelectQueryBuilder(SkuSelectQueryBuilder skuSelectQueryBuilder) {
        QueryBuilderRegistry.skuSelectQueryBuilder = skuSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(CatalogQueryBuilder catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(FilterSelectQueryBuilder filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
