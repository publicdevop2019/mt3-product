package com.mt.mall.port.adapter.persistence.sku;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.mall.domain.model.sku.Sku;
import org.springframework.stereotype.Component;

import static com.mt.mall.domain.model.sku.Sku.SKU_REFERENCE_ID_LITERAL;

@Component
public class SkuSelectQueryBuilder extends SelectQueryBuilder<Sku> {
    {
        allowEmptyClause = true;
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }
}
