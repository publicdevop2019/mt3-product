package com.mt.mall.domain.model.sku.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuPatchedReplyEvent extends DomainEvent {
    private boolean success;
    private long taskId;

    public SkuPatchedReplyEvent(boolean result, long taskId) {
        setSuccess(result);
        setTaskId(taskId);
        setInternal(false);
        setTopic("decrease_sku_for_order_reply_event");
    }
}
