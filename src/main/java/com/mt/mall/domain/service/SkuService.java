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
    public Set<Sku> getSkusOfQuery(SkuQuery queryParam) {
        DefaultPaging queryPagingParam = new DefaultPaging();
        SumPagedRep<Sku> tSumPagedRep = DomainRegistry.skuRepository().skusOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new HashSet<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<Sku> data = new HashSet<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.skuRepository().skusOfQuery(queryParam, queryPagingParam.pageOf(a)).getData());
        }
        return data;
    }

    public SkuId create(SkuId skuId, String referenceId, String description, Integer storageOrder, Integer storageActual, BigDecimal price, Integer sales) {
        Sku sku = new Sku(skuId, referenceId, description, storageOrder, storageActual, price, sales);
        DomainRegistry.skuRepository().add(sku);
        return skuId;
    }
}
