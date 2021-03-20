package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;

import java.util.Optional;

public class CatalogValidator {
    private final Catalog catalog;
    private final ValidationNotificationHandler handler;

    public CatalogValidator(Catalog catalog, ValidationNotificationHandler handler) {
        this.catalog = catalog;
        this.handler = handler;
    }

    protected void validate() {
        validateRelationOfParentIdNCatalogId();
    }

    private void validateRelationOfParentIdNCatalogId() {
        CatalogId catalogId = catalog.getCatalogId();
        CatalogId parentId = catalog.getParentId();
        if (catalogId.equals(parentId))
            handler.handleError("parentId can not same as catalogId");
        if (parentId != null && !parentId.getDomainId().isBlank()) {
            Optional<Catalog> parent = DomainRegistry.getCatalogRepository().catalogOfId(parentId);
            if (parent.isEmpty())
                handler.handleError("parentId not found");
            if (parent.isPresent()) {
                Catalog catalog1 = parent.get();
                if (catalog1.getType().equals(catalog.getType()))
                    handler.handleError("parent catalog must be same type as child");
            }
        }
    }
}
