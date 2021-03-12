package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterQuery;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductQuery;
import com.mt.mall.domain.model.tag.TagId;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MetaService {
    public void findImpactedEntities(TagId tagId) {
        Set<Catalog> allByQuery = QueryUtility.getAllByQuery((e) -> DomainRegistry.getCatalogRepository().catalogsOfQuery((CatalogQuery) e), new CatalogQuery(tagId));
        Set<Filter> allByQuery1 = QueryUtility.getAllByQuery((e) -> DomainRegistry.getFilterRepository().filtersOfQuery((FilterQuery) e), new FilterQuery(tagId));
        Set<Product> allByQuery2 = QueryUtility.getAllByQuery((e) -> DomainRegistry.getProductRepository().productsOfQuery((ProductQuery) e), new ProductQuery(tagId));
        Set<DomainId> collect = allByQuery.stream().map(e -> (DomainId) e.getCatalogId()).collect(Collectors.toSet());
        Set<DomainId> collect1 = allByQuery1.stream().map(e -> (DomainId) e.getFilterId()).collect(Collectors.toSet());
        Set<DomainId> collect2 = allByQuery2.stream().map(e -> (DomainId) e.getProductId()).collect(Collectors.toSet());
        collect.addAll(collect1);
        collect.addAll(collect2);
        Set<Meta> allByQuery3 = QueryUtility.getAllByQuery(e -> DomainRegistry.getMetaRepository().metaOfQuery((MetaQuery) e), new MetaQuery(collect));
        //update impacted catalogs
        allByQuery.forEach(e -> {
            Optional<Meta> first = allByQuery3.stream().filter(ee -> ee.getDomainId().getDomainId().equalsIgnoreCase(e.getCatalogId().getDomainId())).findFirst();
            if (first.isPresent()) {
                first.get().addChangeTag(tagId);
                DomainRegistry.getMetaRepository().save(first.get());
            } else {
                Meta meta = new Meta(e.getCatalogId(), Meta.MetaType.CATALOG, true, new HashSet<>(List.of(tagId)));
                DomainRegistry.getMetaRepository().save(meta);
            }
        });
        //update impacted filter
        allByQuery1.forEach(e -> {
            Optional<Meta> first = allByQuery3.stream().filter(ee -> ee.getDomainId().getDomainId().equalsIgnoreCase(e.getFilterId().getDomainId())).findFirst();
            if (first.isPresent()) {
                first.get().addChangeTag(tagId);
                DomainRegistry.getMetaRepository().save(first.get());
            } else {
                Meta meta = new Meta(e.getFilterId(), Meta.MetaType.PRODUCT, true, new HashSet<>(List.of(tagId)));
                DomainRegistry.getMetaRepository().save(meta);
            }
        });
        //update impacted products
        allByQuery2.forEach(e -> {
            Optional<Meta> first = allByQuery3.stream().filter(ee -> ee.getDomainId().getDomainId().equalsIgnoreCase(e.getProductId().getDomainId())).findFirst();
            if (first.isPresent()) {
                first.get().addChangeTag(tagId);
                DomainRegistry.getMetaRepository().save(first.get());
            } else {
                Meta meta = new Meta(e.getProductId(), Meta.MetaType.PRODUCT, true, new HashSet<>(List.of(tagId)));
                DomainRegistry.getMetaRepository().save(meta);
            }
        });
    }
}
