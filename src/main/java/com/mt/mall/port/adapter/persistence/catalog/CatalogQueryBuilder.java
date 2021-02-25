package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
import com.mt.mall.domain.model.catalog.Catalog;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;

@Component
public
class CatalogQueryBuilder extends SelectQueryBuilder<Catalog> {
    public transient static final String NAME_LITERAL = "name";
    public transient static final String PARENT_ID_LITERAL = "parentId";
    public transient static final String TYPE_LITERAL = "type";
    public transient static final String CATALOG_ID_LITERAL = "catalogId";

    {
        supportedSort.put(NAME_LITERAL, NAME_LITERAL);
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(CATALOG_ID_LITERAL));
        supportedWhere.put(TYPE_LITERAL, new FieldStringEqualClause<>(TYPE_LITERAL));
        supportedWhere.put(PARENT_ID_LITERAL, new FieldStringEqualClause<>(PARENT_ID_LITERAL));
    }
}
