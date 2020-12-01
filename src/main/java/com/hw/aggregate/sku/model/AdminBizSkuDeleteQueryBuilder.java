package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;

@Component
public class AdminBizSkuDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizSku> {
    {
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }
}
