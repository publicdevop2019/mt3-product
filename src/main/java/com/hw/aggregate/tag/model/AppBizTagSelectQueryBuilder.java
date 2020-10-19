package com.hw.aggregate.tag.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AppBizTagSelectQueryBuilder extends SelectQueryBuilder<BizTag> {

    AppBizTagSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 20;
    }
}
