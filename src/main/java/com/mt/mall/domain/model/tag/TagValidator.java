package com.mt.mall.domain.model.tag;

import com.mt.common.validate.ValidationNotificationHandler;

public class TagValidator {
    private final Tag tag;
    private final ValidationNotificationHandler handler;

    public TagValidator(Tag tag, ValidationNotificationHandler handler) {
        this.tag = tag;
        this.handler = handler;
    }

    public void validate() {
        if (TagValueType.MANUAL.equals(tag.getMethod())) {
            if (!(tag.getSelectValues() == null || tag.getSelectValues().size() == 0))
                handler.handleError("manual tag can not have values");
        }
    }
}
