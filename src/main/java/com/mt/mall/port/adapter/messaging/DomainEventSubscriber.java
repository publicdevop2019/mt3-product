package com.mt.mall.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.exception.AggregateOutdatedException;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.mall.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.mall.domain.model.product.event.ProductCreated.TOPIC_PRODUCT;

@Slf4j
@Component
public class DomainEventSubscriber {
    private static final String SKU_QUEUE_NAME = "sku_queue";
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void clientListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, SKU_QUEUE_NAME, (event) -> {
            try {
                ApplicationServiceRegistry.skuApplicationService().handleChange(event);
            } catch (UpdateQueryBuilder.PatchCommandExpectNotMatchException | AggregateOutdatedException ex) {
                //ignore above ex
                log.debug("ignore exception in event {}",ex.getClass().toString());
            }
        }, TOPIC_PRODUCT);
    }

}
