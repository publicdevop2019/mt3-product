package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizAttributeCardRep {
    private Long id;
    private String name;
    private String description;
    private Set<String> selectValues;
    private BizAttribute.AttributeMethod method;
    private BizAttribute.BizAttributeType type;

    public AdminBizAttributeCardRep(BizAttribute attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
        this.description = attribute.getDescription();
        this.selectValues = attribute.getSelectValues();
        this.method = attribute.getMethod();
        this.type = attribute.getType();
    }
}