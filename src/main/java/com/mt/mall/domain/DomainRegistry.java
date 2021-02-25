package com.mt.mall.domain;

import com.mt.mall.domain.model.catalog.CatalogRepository;
import com.mt.mall.domain.model.filter.FilterRepository;
import com.mt.mall.domain.model.product.ProductRepository;
import com.mt.mall.domain.model.product.ProductTagRepository;
import com.mt.mall.domain.model.sku.SkuRepository;
import com.mt.mall.domain.model.tag.TagRepository;
import com.mt.mall.domain.service.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static CatalogRepository catalogRepository;
    private static CatalogService catalogService;
    private static FilterRepository filterRepository;
    private static FilterService filterService;
    private static TagService tagService;
    private static TagRepository tagRepository;
    private static SkuService skuService;
    private static SkuRepository skuRepository;
    private static ProductRepository productRepository;
    private static ProductService productService;
    private static ProductTagRepository productTagRepository;
    @Getter
    private static CatalogValidationService catalogValidationService;
    @Getter
    private static FilterValidationService filterValidationService;
    @Getter
    private static ProductValidationService productValidationService;

    public static ProductRepository productRepository() {
        return productRepository;
    }

    public static ProductTagRepository productTagRepository() {
        return productTagRepository;
    }

    public static ProductService productService() {
        return productService;
    }

    public static TagRepository tagRepository() {
        return tagRepository;
    }

    public static SkuRepository skuRepository() {
        return skuRepository;
    }

    public static SkuService skuService() {
        return skuService;
    }

    public static TagService tagService() {
        return tagService;
    }

    public static CatalogRepository catalogRepository() {
        return catalogRepository;
    }

    public static FilterService filterService() {
        return filterService;
    }

    public static FilterRepository filterRepository() {
        return filterRepository;
    }


    @Autowired
    private void setCatalogValidationService(FilterValidationService catalogValidationService) {
        DomainRegistry.filterValidationService = catalogValidationService;
    }

    @Autowired
    private void setProductValidationService(ProductValidationService productValidationService) {
        DomainRegistry.productValidationService = productValidationService;
    }

    @Autowired
    private void setProductRepository(ProductRepository productRepository) {
        DomainRegistry.productRepository = productRepository;
    }

    @Autowired
    private void setTagValidationService(CatalogValidationService tagValidationService) {
        DomainRegistry.catalogValidationService = tagValidationService;
    }

    @Autowired
    private void setProductTagRepository(ProductTagRepository productTagRepository) {
        DomainRegistry.productTagRepository = productTagRepository;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        DomainRegistry.productService = productService;
    }

    @Autowired
    private void setCatalogRepository(CatalogRepository catalogRepository) {
        DomainRegistry.catalogRepository = catalogRepository;
    }

    @Autowired
    private void setSkuRepository(SkuRepository skuRepository) {
        DomainRegistry.skuRepository = skuRepository;
    }

    @Autowired
    private void setSkuService(SkuService skuService) {
        DomainRegistry.skuService = skuService;
    }

    @Autowired
    private void setFilterService(FilterService filterService) {
        DomainRegistry.filterService = filterService;
    }

    @Autowired
    private void setTagService(TagService tagService) {
        DomainRegistry.tagService = tagService;
    }

    @Autowired
    private void setTagRepository(TagRepository tagRepository) {
        DomainRegistry.tagRepository = tagRepository;
    }

    @Autowired
    private void setFilterRepository(FilterRepository filterRepository) {
        DomainRegistry.filterRepository = filterRepository;
    }


    public static CatalogService catalogService() {
        return catalogService;
    }

    @Autowired
    private void setCatalogService(CatalogService catalogService) {
        DomainRegistry.catalogService = catalogService;
    }
}
