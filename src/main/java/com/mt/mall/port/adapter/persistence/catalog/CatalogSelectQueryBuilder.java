package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.DomainIdQueryClause;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.mall.domain.model.catalog.Catalog;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;


@Component
public class CatalogSelectQueryBuilder extends SelectQueryBuilder<Catalog> {
    public transient static final String NAME_LITERAL = "name";
    public transient static final String PARENT_ID_LITERAL = "parentId";
    public transient static final String TYPE_LITERAL = "type";
    public transient static final String CATALOG_ID_LITERAL = "catalogId";

    {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        supportedWhereField.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(CATALOG_ID_LITERAL));
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause<>(TYPE_LITERAL));
        supportedWhereField.put(PARENT_ID_LITERAL, new SelectFieldStringEqualClause<>(PARENT_ID_LITERAL));
        allowEmptyClause = true;
    }
}
