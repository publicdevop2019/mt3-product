package com.mt.mall.domain.model.filter.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.filter.FilterId;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class FilterEvent extends DomainEvent {
    public static final String TOPIC_FILTER = "filter";

    public FilterEvent(FilterId filterId) {
        super(filterId);
        setTopic(TOPIC_FILTER);
    }

    public FilterEvent() {
        super();
        setTopic(TOPIC_FILTER);
    }

    public FilterEvent(Set<FilterId> filterIds) {
        super(filterIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        setTopic(TOPIC_FILTER);
    }
}
