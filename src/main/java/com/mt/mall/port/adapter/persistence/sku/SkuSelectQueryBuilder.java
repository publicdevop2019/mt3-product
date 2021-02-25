package com.mt.mall.port.adapter.persistence.sku;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
import com.mt.mall.domain.model.sku.Sku;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.mall.domain.model.sku.Sku.SKU_REFERENCE_ID_LITERAL;

@Component
public class SkuSelectQueryBuilder extends SelectQueryBuilder<Sku> {
    public static final String SKU_ID_LITERAL = "skuId";

    {
//        allowEmptyClause = true;
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(SKU_ID_LITERAL));
        supportedWhere.put(SKU_REFERENCE_ID_LITERAL, new FieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }
}
