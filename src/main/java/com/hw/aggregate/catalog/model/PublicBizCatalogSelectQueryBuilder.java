package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.catalog.model.BizCatalog.TYPE_LITERAL;

@Component
public class PublicBizCatalogSelectQueryBuilder extends SelectQueryBuilder<BizCatalog> {
    {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 1500;
        defaultWhereField.add(new SelectEqualClause(TYPE_LITERAL, BizCatalog.CatalogType.FRONTEND.name()));
    }

}
