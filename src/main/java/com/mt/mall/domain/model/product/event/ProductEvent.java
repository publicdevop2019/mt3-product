package com.mt.mall.domain.model.product.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.mall.domain.model.product.ProductId;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProductEvent extends DomainEvent {
    public static final String TOPIC_PRODUCT = "product";

    public ProductEvent(ProductId productId) {
        super(productId);
        setTopic(TOPIC_PRODUCT);
    }

    public ProductEvent() {
        super();
        setTopic(TOPIC_PRODUCT);
    }

    public ProductEvent(Set<ProductId> productIds) {
        super(productIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        setTopic(TOPIC_PRODUCT);
    }
}
