package com.mt.mall.port.adapter.persistence.tag;

import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

@Component
public class BizTagQueryRegistry extends RestfulQueryRegistry<Tag> {

    @Override
    public Class<Tag> getEntityClass() {
        return Tag.class;
    }
}
