package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import com.hw.shared.sql.exception.EmptyWhereClauseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;
import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
@Qualifier("admin")
public class AdminBizSkuDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizSku> {
    AdminBizSkuDeleteQueryBuilder(){
        supportedWhereField.put(SKU_REFERENCE_ID_LITERAL, new SelectFieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
    }
    @Override
    protected Predicate getWhereClause(Root<BizSku> root, String fieldName) {
        return null;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
