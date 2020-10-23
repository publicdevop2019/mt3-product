package com.hw.aggregate.tag.representation;

import com.hw.aggregate.tag.model.BizTag;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizTagRep {
    private Long id;
    private String name;
    private String description;
    private BizTag.AttributeMethod method;
    private Set<String> selectValues;
    private BizTag.BizAttributeType type;

    public AdminBizTagRep(BizTag attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
        this.description = attribute.getDescription();
        this.selectValues = attribute.getSelectValues();
        this.method = attribute.getMethod();
        this.type = attribute.getType();
    }
}
