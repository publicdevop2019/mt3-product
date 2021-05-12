package com.mt.mall.domain.model.sku.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuPatchedReplyEvent extends DomainEvent {
    public static String CREATE_NEW_ORDER_REPLY = "CREATE_NEW_ORDER_REPLY";
    private boolean success;
    private long taskId;

    public SkuPatchedReplyEvent(boolean result, long taskId) {
        setSuccess(result);
        setTaskId(taskId);
        setInternal(false);
        setTopic(CREATE_NEW_ORDER_REPLY);
        setName("DECREASE_REPLY");
    }
}
