package com.mt.mall.domain.service;

import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.sku.SkuQuery;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class SkuService {

    public SkuId create(SkuId skuId, String referenceId, String description, Integer storageOrder, Integer storageActual, BigDecimal price, Integer sales) {
        Sku sku = new Sku(skuId, referenceId, description, storageOrder, storageActual, price, sales);
        DomainRegistry.skuRepository().add(sku);
        return skuId;
    }
}
