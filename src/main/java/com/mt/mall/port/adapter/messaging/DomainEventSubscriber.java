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
import static com.mt.mall.domain.model.tag.event.TagCriticalFieldChanged.TOPIC_TAG;

@Slf4j
@Component
public class DomainEventSubscriber {
    private static final String SKU_QUEUE_NAME = "sku_queue";
    private static final String TAG_CHANGE_QUEUE_NAME = "tag_change_queue";
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void skuListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, SKU_QUEUE_NAME, (event) -> {
            try {
                ApplicationServiceRegistry.getSkuApplicationService().handleChange(event);
            } catch (UpdateQueryBuilder.PatchCommandExpectNotMatchException | AggregateOutdatedException ex) {
                //ignore above ex
                log.debug("ignore exception in event {}", ex.getClass().toString());
            }
        }, TOPIC_PRODUCT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void tagChangeListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, TAG_CHANGE_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.getMetaApplicationService().handleChange(event);
        }, TOPIC_TAG);
    }

}
