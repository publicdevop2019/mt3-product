package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;

@Component
public class AppBizSkuSelectQueryBuilder extends SelectQueryBuilder<BizSku> {
    AppBizSkuSelectQueryBuilder() {
        allowEmptyClause = true;
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause(SKU_REFERENCE_ID_LITERAL));
    }

}
