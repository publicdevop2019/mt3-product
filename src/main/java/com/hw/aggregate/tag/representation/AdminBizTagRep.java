package com.hw.aggregate.tag.representation;

import com.hw.aggregate.tag.model.BizTag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizTagRep {
    private Long id;
    private String name;
    private String description;
    private BizTag.AttributeMethod method;
    private Set<String> selectValues;
    private BizTag.BizAttributeType type;
    private Integer version;

    public AdminBizTagRep(BizTag bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}
