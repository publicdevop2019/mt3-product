package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AppBizAttributeSelectQueryBuilder extends SelectQueryBuilder<BizAttribute> {

    AppBizAttributeSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 20;
    }
}
