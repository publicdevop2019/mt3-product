package com.mt.mall.domain.service;

import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.catalog.CatalogQuery;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.Type;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class CatalogService {
    public Set<Catalog> getCatalogsOfQuery(CatalogQuery queryParam) {
        DefaultPaging queryPagingParam = new DefaultPaging();
        SumPagedRep<Catalog> tSumPagedRep = DomainRegistry.catalogRepository().catalogsOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new HashSet<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<Catalog> data = new HashSet<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.catalogRepository().catalogsOfQuery(queryParam, queryPagingParam.pageOf(a)).getData());
        }
        return data;
    }

    public CatalogId create(CatalogId catalogId, String name, CatalogId parentId, Set<String> attributes, Type catalogType) {
        Catalog catalog = new Catalog(catalogId, name, parentId, attributes, catalogType);
        DomainRegistry.catalogRepository().add(catalog);
        return catalog.getCatalogId();
    }
}
