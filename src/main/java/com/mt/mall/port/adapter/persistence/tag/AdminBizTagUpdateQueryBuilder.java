package com.mt.mall.port.adapter.persistence.tag;

import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import org.springframework.stereotype.Component;

import static com.mt.mall.domain.model.tag.Tag.METHOD_LITERAL;
import static com.mt.mall.domain.model.tag.Tag.TYPE_LITERAL;
import static com.mt.mall.application.tag.representation.TagCardRepresentation.ADMIN_REP_METHOD_LITERAL;
import static com.mt.mall.application.tag.representation.TagCardRepresentation.ADMIN_REP_TYPE_LITERAL;

@Component
public class AdminBizTagUpdateQueryBuilder extends UpdateByIdQueryBuilder<Tag> {
    {
        filedMap.put(ADMIN_REP_TYPE_LITERAL, TYPE_LITERAL);
        filedMap.put(ADMIN_REP_METHOD_LITERAL, METHOD_LITERAL);
        filedTypeMap.put(ADMIN_REP_TYPE_LITERAL, this::parseType);
        filedTypeMap.put(ADMIN_REP_METHOD_LITERAL, this::parseMethod);
    }

    private Type parseType(Object o) {
        return Type.valueOf((String)o);
    }
    private TagValueType parseMethod(Object o) {
        return TagValueType.valueOf((String)o);
    }
}
