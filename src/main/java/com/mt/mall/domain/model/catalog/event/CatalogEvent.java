package com.mt.mall.domain.model.catalog.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.mall.domain.model.catalog.CatalogId;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class CatalogEvent extends DomainEvent {
    public static final String TOPIC_CATALOG = "catalog";

    public CatalogEvent(CatalogId catalogId) {
        super(catalogId);
        setTopic(TOPIC_CATALOG);
    }

    public CatalogEvent() {
        super();
        setTopic(TOPIC_CATALOG);
    }

    public CatalogEvent(Set<CatalogId> catalogIds) {
        super(catalogIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        setTopic(TOPIC_CATALOG);
    }
}
