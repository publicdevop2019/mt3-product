package com.mt.mall.domain.service;

import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import com.mt.mall.domain.model.catalog.Type;
import com.mt.mall.domain.model.filter.FilterItem;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterValidationService {
    public void validateCatalogs(Set<String> catalogs, HttpValidationNotificationHandler handler) {
        // filter can only be attached to frontend catalog
        Set<Catalog> allByQuery = QueryUtility.getAllByQuery((query, pageConfig) -> DomainRegistry.catalogRepository().catalogsOfQuery(query, pageConfig), new CatalogQuery(catalogs));
        if (allByQuery.size() != catalogs.size())
            handler.handleError("can not find all catalogs");
        Optional<Catalog> any = allByQuery.stream().filter(e -> Type.FRONTEND.equals(e.getType())).findAny();
        if (any.isPresent())
            handler.handleError("filter can only be attached to frontend catalog");

    }

    public void validateTags(Set<FilterItem> filterItems, HttpValidationNotificationHandler handler) {
        Set<String> collect = filterItems.stream().map(FilterItem::getTagId).collect(Collectors.toSet());
        Set<Tag> tagSet = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.tagRepository().tagsOfQuery(query, page), new TagQuery(collect));
        if (collect.size() == tagSet.size())
            handler.handleError("can not find all tags");
    }
}
