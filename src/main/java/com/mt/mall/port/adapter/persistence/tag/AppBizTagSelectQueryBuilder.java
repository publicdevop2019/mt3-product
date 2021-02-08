package com.mt.mall.port.adapter.persistence.tag;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

@Component
public class AppBizTagSelectQueryBuilder extends SelectQueryBuilder<Tag> {
    {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 20;
    }
}
