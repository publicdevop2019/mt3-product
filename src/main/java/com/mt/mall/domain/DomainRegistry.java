package com.mt.mall.domain;

import com.mt.mall.domain.model.catalog.CatalogRepository;
import com.mt.mall.domain.model.catalog.CatalogService;
import com.mt.mall.domain.model.catalog.CatalogValidationService;
import com.mt.mall.domain.model.filter.FilterRepository;
import com.mt.mall.domain.model.filter.FilterService;
import com.mt.mall.domain.model.filter.FilterValidationService;
import com.mt.mall.domain.model.meta.MetaRepository;
import com.mt.mall.domain.model.product.ProductRepository;
import com.mt.mall.domain.model.product.ProductService;
import com.mt.mall.domain.model.product.ProductTagRepository;
import com.mt.mall.domain.model.product.ProductValidationService;
import com.mt.mall.domain.model.sku.SkuRepository;
import com.mt.mall.domain.model.sku.SkuService;
import com.mt.mall.domain.model.tag.TagRepository;
import com.mt.mall.domain.model.tag.TagService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static CatalogRepository catalogRepository;
    @Getter
    private static CatalogService catalogService;
    @Getter
    private static FilterRepository filterRepository;
    @Getter
    private static FilterService filterService;
    @Getter
    private static TagService tagService;
    @Getter
    private static TagRepository tagRepository;
    @Getter
    private static SkuService skuService;
    @Getter
    private static SkuRepository skuRepository;
    @Getter
    private static ProductRepository productRepository;
    @Getter
    private static ProductService productService;
    @Getter
    private static ProductTagRepository productTagRepository;
    @Getter
    private static CatalogValidationService catalogValidationService;
    @Getter
    private static FilterValidationService filterValidationService;
    @Getter
    private static ProductValidationService productValidationService;
    @Getter
    private static MetaRepository metaRepository;

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

    @Autowired
    private void setMetaRepository(MetaRepository metaRepository) {
        DomainRegistry.metaRepository = metaRepository;
    }

    @Autowired
    private void setCatalogService(CatalogService catalogService) {
        DomainRegistry.catalogService = catalogService;
    }
}
