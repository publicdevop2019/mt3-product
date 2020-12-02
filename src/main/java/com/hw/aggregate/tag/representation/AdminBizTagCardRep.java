package com.hw.aggregate.tag.representation;

import com.hw.aggregate.tag.model.BizTag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizTagCardRep {
    private Long id;
    private String name;
    private String description;
    private Set<String> selectValues;
    private BizTag.AttributeMethod method;
    public transient static final String ADMIN_REP_METHOD_LITERAL = "method";
    private BizTag.BizAttributeType type;
    public transient static final String ADMIN_REP_TYPE_LITERAL = "type";
    private Integer version;

    public AdminBizTagCardRep(BizTag bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}