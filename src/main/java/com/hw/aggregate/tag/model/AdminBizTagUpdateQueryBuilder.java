package com.hw.aggregate.tag.model;

import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.hw.aggregate.tag.model.BizTag.METHOD_LITERAL;
import static com.hw.aggregate.tag.model.BizTag.TYPE_LITERAL;
import static com.hw.aggregate.tag.representation.AdminBizTagCardRep.ADMIN_REP_METHOD_LITERAL;
import static com.hw.aggregate.tag.representation.AdminBizTagCardRep.ADMIN_REP_TYPE_LITERAL;

@Component
public class AdminBizTagUpdateQueryBuilder extends UpdateByIdQueryBuilder<BizTag> {
    {
        filedMap.put(ADMIN_REP_TYPE_LITERAL, TYPE_LITERAL);
        filedMap.put(ADMIN_REP_METHOD_LITERAL, METHOD_LITERAL);
        filedTypeMap.put(ADMIN_REP_TYPE_LITERAL, this::parseType);
        filedTypeMap.put(ADMIN_REP_METHOD_LITERAL, this::parseMethod);
    }

    private BizTag.BizAttributeType parseType(Object o) {
        return BizTag.BizAttributeType.valueOf((String)o);
    }
    private BizTag.AttributeMethod parseMethod(Object o) {
        return BizTag.AttributeMethod.valueOf((String)o);
    }
}
