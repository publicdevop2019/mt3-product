package com.mt.mall.port.adapter.persistence.sku;


import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.builder.UpdateQueryBuilder;
import com.mt.mall.domain.model.sku.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.mt.mall.domain.model.sku.Sku.*;

@Component
public class AppBizSkuUpdateQueryBuilder extends UpdateQueryBuilder<Sku> {
    @Autowired
    private AdminBizSkuUpdateQueryBuilder adminBizSkuUpdateQueryBuilder;

    @Override
    protected void setUpdateValue(Root<Sku> root, CriteriaUpdate<Sku> criteriaUpdate, PatchCommand e) {
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
    protected Predicate getWhereClause(Root<Sku> root, List<String> ids, PatchCommand command) {
        return adminBizSkuUpdateQueryBuilder.getWhereClause(root, ids, command);
    }

}
