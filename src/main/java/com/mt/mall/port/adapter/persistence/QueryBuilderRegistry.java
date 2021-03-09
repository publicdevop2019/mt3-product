package com.mt.mall.port.adapter.persistence;

import com.mt.mall.port.adapter.persistence.catalog.SpringDataJpaCatalogRepository;
import com.mt.mall.port.adapter.persistence.filter.SpringDataJpaFilterRepository;
import com.mt.mall.port.adapter.persistence.product.SpringDataJpaProductRepository;
import com.mt.mall.port.adapter.persistence.sku.SpringDataJpaSkuRepository;
import com.mt.mall.port.adapter.persistence.tag.SpringDataJpaTagRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogAdaptor catalogSelectQueryBuilder;
    @Getter
    private static SpringDataJpaFilterRepository.JpaCriteriaApiFilterAdaptor filterSelectQueryBuilder;
    @Getter
    private static SpringDataJpaSkuRepository.JpaCriteriaApiSkuAdapter skuSelectQueryBuilder;
    @Getter
    private static SpringDataJpaTagRepository.JpaCriteriaApiTagAdaptor tagSelectQueryBuilder;
    @Getter
    private static SpringDataJpaProductRepository.JpaCriteriaApiProductAdaptor productSelectQueryBuilder;
    @Getter
    private static SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder;
    @Getter
    private static SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder;

    @Autowired
    public void setSkuUpdateQueryBuilder(SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder) {
        QueryBuilderRegistry.skuUpdateQueryBuilder = skuUpdateQueryBuilder;
    }

    @Autowired
    public void setProductUpdateQueryBuilder(SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder) {
        QueryBuilderRegistry.productUpdateQueryBuilder = productUpdateQueryBuilder;
    }

    @Autowired
    public void setProductSelectQueryBuilder(SpringDataJpaProductRepository.JpaCriteriaApiProductAdaptor productSelectQueryBuilder) {
        QueryBuilderRegistry.productSelectQueryBuilder = productSelectQueryBuilder;
    }

    @Autowired
    public void setTagSelectQueryBuilder(SpringDataJpaTagRepository.JpaCriteriaApiTagAdaptor tagSelectQueryBuilder) {
        QueryBuilderRegistry.tagSelectQueryBuilder = tagSelectQueryBuilder;
    }

    @Autowired
    public void setSkuSelectQueryBuilder(SpringDataJpaSkuRepository.JpaCriteriaApiSkuAdapter skuSelectQueryBuilder) {
        QueryBuilderRegistry.skuSelectQueryBuilder = skuSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogAdaptor catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(SpringDataJpaFilterRepository.JpaCriteriaApiFilterAdaptor filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
