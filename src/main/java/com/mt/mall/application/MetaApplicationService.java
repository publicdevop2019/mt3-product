package com.mt.mall.application;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.mall.domain.model.meta.Meta;
import com.mt.mall.domain.model.tag.event.TagCriticalFieldChanged;
import com.mt.mall.domain.model.tag.event.TagDeleted;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MetaApplicationService {
    @SubscribeForEvent
    @Transactional
    public void handleChange(StoredEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(null, null, event.getId().toString(), (ignored) -> {
            if (TagDeleted.class.getName().equals(event.getName())) {
                TagDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), TagDeleted.class);
            }
            if (TagCriticalFieldChanged.class.getName().equals(event.getName())) {
                TagCriticalFieldChanged deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), TagCriticalFieldChanged.class);
            }
        }, Meta.class);
    }
}
