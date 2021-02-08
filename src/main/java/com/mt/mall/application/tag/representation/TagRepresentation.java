package com.mt.mall.application.tag.representation;

import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class TagRepresentation {
    private Long id;
    private String name;
    private String description;
    private TagValueType method;
    private Set<String> selectValues;
    private Type type;
    private Integer version;

    public TagRepresentation(Tag bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}
