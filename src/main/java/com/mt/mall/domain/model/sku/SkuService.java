package com.mt.mall.domain.model.sku;

import com.mt.mall.domain.DomainRegistry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SkuService {

    public SkuId create(SkuId skuId, String referenceId, String description, Integer storageOrder, Integer storageActual, BigDecimal price, Integer sales) {
        Sku sku = new Sku(skuId, referenceId, description, storageOrder, storageActual, price, sales);
        DomainRegistry.getSkuRepository().add(sku);
        return skuId;
    }
}
