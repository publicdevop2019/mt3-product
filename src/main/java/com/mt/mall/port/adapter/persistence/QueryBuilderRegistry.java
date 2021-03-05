package com.mt.mall.port.adapter.persistence;

import com.mt.common.domain.model.sql.builder.SqlSelectQueryConverter;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.port.adapter.persistence.catalog.SpringDataJpaCatalogRepository;
import com.mt.mall.port.adapter.persistence.filter.SpringDataJpaFilterRepository;
import com.mt.mall.port.adapter.persistence.product.SpringDataJpaProductRepository;
import com.mt.mall.port.adapter.persistence.sku.SpringDataJpaSkuRepository;
import com.mt.mall.port.adapter.persistence.tag.SpringDataJpaTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    private static SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogExecutor catalogSelectQueryBuilder;
    private static SpringDataJpaFilterRepository.FilterQueryBuilder filterSelectQueryBuilder;
    private static SpringDataJpaSkuRepository.SkuQueryBuilder skuSelectQueryBuilder;
    private static SpringDataJpaTagRepository.TagQueryBuilder tagSelectQueryBuilder;
    private static SpringDataJpaProductRepository.ProductQueryBuilder productSelectQueryBuilder;
    private static SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder;
    private static SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder;

    public static SpringDataJpaTagRepository.TagQueryBuilder tagSelectQueryBuilder() {
        return tagSelectQueryBuilder;
    }

    public static SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder() {
        return skuUpdateQueryBuilder;
    }

    public static SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder() {
        return productUpdateQueryBuilder;
    }

    public static SpringDataJpaProductRepository.ProductQueryBuilder productSelectQueryBuilder() {
        return productSelectQueryBuilder;
    }

    public static SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogExecutor catalogSelectQueryBuilder() {
        return catalogSelectQueryBuilder;
    }

    public static SpringDataJpaSkuRepository.SkuQueryBuilder skuSelectQueryBuilder() {
        return skuSelectQueryBuilder;
    }

    public static SqlSelectQueryConverter<Filter> filterSelectQueryBuilder() {
        return filterSelectQueryBuilder;
    }

    @Autowired
    public void setSkuUpdateQueryBuilder(SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder) {
        QueryBuilderRegistry.skuUpdateQueryBuilder = skuUpdateQueryBuilder;
    }

    @Autowired
    public void setProductUpdateQueryBuilder(SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder) {
        QueryBuilderRegistry.productUpdateQueryBuilder = productUpdateQueryBuilder;
    }

    @Autowired
    public void setProductSelectQueryBuilder(SpringDataJpaProductRepository.ProductQueryBuilder productSelectQueryBuilder) {
        QueryBuilderRegistry.productSelectQueryBuilder = productSelectQueryBuilder;
    }

    @Autowired
    public void setTagSelectQueryBuilder(SpringDataJpaTagRepository.TagQueryBuilder tagSelectQueryBuilder) {
        QueryBuilderRegistry.tagSelectQueryBuilder = tagSelectQueryBuilder;
    }

    @Autowired
    public void setSkuSelectQueryBuilder(SpringDataJpaSkuRepository.SkuQueryBuilder skuSelectQueryBuilder) {
        QueryBuilderRegistry.skuSelectQueryBuilder = skuSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogExecutor catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(SpringDataJpaFilterRepository.FilterQueryBuilder filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
