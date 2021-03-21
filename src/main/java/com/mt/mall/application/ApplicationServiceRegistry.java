package com.mt.mall.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.mall.application.catalog.CatalogApplicationService;
import com.mt.mall.application.filter.FilterApplicationService;
import com.mt.mall.application.product.ProductApplicationService;
import com.mt.mall.application.sku.SkuApplicationService;
import com.mt.mall.application.tag.TagApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static CatalogApplicationService catalogApplicationService;
    @Getter
    private static IdempotentService idempotentWrapper;
    @Getter
    private static FilterApplicationService filterApplicationService;
    @Getter
    private static TagApplicationService tagApplicationService;
    @Getter
    private static SkuApplicationService skuApplicationService;
    @Getter
    private static ProductApplicationService productApplicationService;
    @Getter
    private static MetaApplicationService metaApplicationService;

    @Autowired
    public void setMetaApplicationService(MetaApplicationService metaApplicationService) {
        ApplicationServiceRegistry.metaApplicationService = metaApplicationService;
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
