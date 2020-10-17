package com.hw.aggregate.sku.model;


import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.builder.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.sku.model.BizSku.*;

@Component
public class AppBizSkuUpdateQueryBuilder extends UpdateQueryBuilder<BizSku> {
    @Autowired
    private AdminBizSkuUpdateQueryBuilder adminBizSkuUpdateQueryBuilder;

    @Override
    protected void setUpdateValue(Root<BizSku> root, CriteriaUpdate<BizSku> criteriaUpdate, PatchCommand e) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(adminBizSkuUpdateQueryBuilder.setUpdateStorageValueFor("/" + SKU_STORAGE_ORDER_LITERAL, SKU_STORAGE_ORDER_LITERAL, root, criteriaUpdate, e));
        booleans.add(adminBizSkuUpdateQueryBuilder.setUpdateStorageValueFor("/" + SKU_STORAGE_ACTUAL_LITERAL, SKU_STORAGE_ACTUAL_LITERAL, root, criteriaUpdate, e));
        booleans.add(adminBizSkuUpdateQueryBuilder.setUpdateStorageValueFor("/" + SKU_SALES_LITERAL, SKU_SALES_LITERAL, root, criteriaUpdate, e));
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    @Override
    protected Predicate getWhereClause(Root<BizSku> root, List<String> ids, PatchCommand command) {
        return adminBizSkuUpdateQueryBuilder.getWhereClause(root, ids, command);
    }

}
