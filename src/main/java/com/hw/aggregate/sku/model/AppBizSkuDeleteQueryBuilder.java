package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;

@Component
public class AppBizSkuDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizSku> {
    AppBizSkuDeleteQueryBuilder(){
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }

}
