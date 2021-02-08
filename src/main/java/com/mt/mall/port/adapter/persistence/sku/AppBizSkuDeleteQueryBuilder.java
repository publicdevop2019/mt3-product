package com.mt.mall.port.adapter.persistence.sku;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import com.mt.mall.domain.model.sku.Sku;
import org.springframework.stereotype.Component;

import static com.mt.mall.domain.model.sku.Sku.SKU_REFERENCE_ID_LITERAL;

@Component
public class AppBizSkuDeleteQueryBuilder extends SoftDeleteQueryBuilder<Sku> {
    {
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }
}
