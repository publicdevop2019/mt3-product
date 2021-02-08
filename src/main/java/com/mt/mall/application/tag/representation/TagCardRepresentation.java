package com.mt.mall.application.tag.representation;

import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class TagCardRepresentation {
    private Long id;
    private String name;
    private String description;
    private Set<String> selectValues;
    private TagValueType method;
    public transient static final String ADMIN_REP_METHOD_LITERAL = "method";
    private Type type;
    public transient static final String ADMIN_REP_TYPE_LITERAL = "type";
    private Integer version;

    public TagCardRepresentation(Object bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}