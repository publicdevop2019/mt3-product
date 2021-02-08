package com.mt.mall.application.tag.representation;

import com.mt.mall.domain.model.tag.Tag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class InternalTagCardRepresentation {
    private Long id;
    private String name;

    public InternalTagCardRepresentation(Tag bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}