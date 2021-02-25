package com.mt.mall.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.mall.application.catalog.CatalogApplicationService;
import com.mt.mall.application.filter.FilterApplicationService;
import com.mt.mall.application.product.ProductApplicationService;
import com.mt.mall.application.sku.SkuApplicationService;
import com.mt.mall.application.tag.TagApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static CatalogApplicationService catalogApplicationService;
    private static IdempotentService idempotentWrapper;
    private static FilterApplicationService filterApplicationService;
    private static TagApplicationService tagApplicationService;
    private static SkuApplicationService skuApplicationService;
    private static ProductApplicationService productApplicationService;

    public static SkuApplicationService skuApplicationService() {
        return skuApplicationService;
    }

    public static ProductApplicationService productApplicationService() {
        return productApplicationService;
    }

    public static TagApplicationService tagApplicationService() {
        return tagApplicationService;
    }

    public static CatalogApplicationService catalogApplicationService() {
        return catalogApplicationService;
    }

    public static FilterApplicationService filterApplicationService() {
        return filterApplicationService;
    }

    public static IdempotentService idempotentWrapper() {
        return idempotentWrapper;
    }

    @Autowired
    public void setProductApplicationService(ProductApplicationService productApplicationService) {
        ApplicationServiceRegistry.productApplicationService = productApplicationService;
    }

    @Autowired
    public void setTagApplicationService(TagApplicationService tagApplicationService) {
        ApplicationServiceRegistry.tagApplicationService = tagApplicationService;
    }

    @Autowired
    public void setSkuApplicationService(SkuApplicationService skuApplicationService) {
        ApplicationServiceRegistry.skuApplicationService = skuApplicationService;
    }

    @Autowired
    public void setCatalogApplicationService(CatalogApplicationService catalogApplicationService) {
        ApplicationServiceRegistry.catalogApplicationService = catalogApplicationService;
    }

    @Autowired
    public void setFilterApplicationService(FilterApplicationService filterApplicationService) {
        ApplicationServiceRegistry.filterApplicationService = filterApplicationService;
    }

    @Autowired
    public void setApplicationServiceIdempotentWrapper(IdempotentService idempotentWrapper) {
        ApplicationServiceRegistry.idempotentWrapper = idempotentWrapper;
    }
}
