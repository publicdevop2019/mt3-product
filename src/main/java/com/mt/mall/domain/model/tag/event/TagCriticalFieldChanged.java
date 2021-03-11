package com.mt.mall.domain.model.tag.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.mall.domain.model.tag.TagId;

public class TagCriticalFieldChanged extends DomainEvent {
    public static final String TOPIC_TAG = "tag";
    public TagCriticalFieldChanged(TagId tagId) {
        super(tagId);
        setTopic(TOPIC_TAG);
    }
}
