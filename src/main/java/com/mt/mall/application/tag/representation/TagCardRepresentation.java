package com.mt.mall.application.tag.representation;

import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import lombok.Data;

import java.util.Set;

@Data
public class TagCardRepresentation {
    private String id;
    private String name;
    private String description;
    private Set<String> selectValues;
    private TagValueType method;
    private Type type;
    private Integer version;

    public TagCardRepresentation(Object obj) {
        Tag tag = (Tag) obj;
        setId(tag.getTagId().getDomainId());
        setName(tag.getName());
        setDescription(tag.getDescription());
        setSelectValues(tag.getSelectValues());
        setMethod(tag.getMethod());
        setType(tag.getType());
        setVersion(tag.getVersion());
    }
}