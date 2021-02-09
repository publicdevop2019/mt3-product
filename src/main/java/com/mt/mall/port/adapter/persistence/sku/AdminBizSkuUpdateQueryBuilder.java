package com.mt.mall.port.adapter.persistence.sku;


import com.mt.common.rest.exception.NoUpdatableFieldException;
import com.mt.common.rest.exception.UnsupportedPatchOperationException;
import com.mt.common.rest.exception.UpdateFiledValueException;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.builder.UpdateQueryBuilder;
import com.mt.mall.domain.model.sku.Sku;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.domain.model.sku.Sku.SKU_STORAGE_ACTUAL_LITERAL;
import static com.mt.mall.domain.model.sku.Sku.SKU_STORAGE_ORDER_LITERAL;

@Component
public class AdminBizSkuUpdateQueryBuilder extends UpdateQueryBuilder<Sku> {

    @Override
    protected void setUpdateValue(Root<Sku> root, CriteriaUpdate<Sku> criteriaUpdate, PatchCommand e) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(setUpdateStorageValueFor("/" + SKU_STORAGE_ORDER_LITERAL, SKU_STORAGE_ORDER_LITERAL, root, criteriaUpdate, e));
        booleans.add(setUpdateStorageValueFor("/" + SKU_STORAGE_ACTUAL_LITERAL, SKU_STORAGE_ACTUAL_LITERAL, root, criteriaUpdate, e));
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    @Override
    protected Predicate getWhereClause(Root<Sku> root, List<String> ids, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String id : ids) {
            Predicate idClause = cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(id));
            if (storagePatchOpSub(command)) {
                //make sure if storage change, value is not negative
                Predicate negativeClause = getStorageMustNotNegativeClause(cb, root, command);
                Predicate and = cb.and(idClause, negativeClause);
                results.add(and);
            } else {
                results.add(idClause);
            }
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    protected Boolean setUpdateStorageValueFor(String fieldPath, String filedLiteral, Root<Sku> root, CriteriaUpdate<Sku> criteriaUpdate, PatchCommand e) {
        if (e.getPath().contains(fieldPath)) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_SUM)) {
                criteriaUpdate.set(root.<Integer>get(filedLiteral), cb.sum(root.get(filedLiteral), parseInteger(e.getValue())));
                return true;
            } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF)) {
                criteriaUpdate.set(root.<Integer>get(filedLiteral), cb.diff(root.get(filedLiteral), parseInteger(e.getValue())));
                return true;
            } else {
                throw new UnsupportedPatchOperationException();
            }
        } else {
            return false;
        }
    }

    private Long parseLong(@Nullable Object input) {
        try {
            if (input == null)
                throw new UpdateFiledValueException();
            if (input.getClass().equals(Integer.class))
                return ((Integer) input).longValue();
            if (input.getClass().equals(BigInteger.class))
                return ((BigInteger) input).longValue();
            return Long.parseLong((String) input);
        } catch (NumberFormatException ex) {
            throw new UpdateFiledValueException();
        }
    }

    private Integer parseInteger(@Nullable Object input) {
        return parseLong(input).intValue();
    }

    private Predicate getStorageMustNotNegativeClause(CriteriaBuilder cb, Root<Sku> root, PatchCommand command) {
        String filedLiteral;
        if (command.getPath().contains(SKU_STORAGE_ORDER_LITERAL)) {
            filedLiteral = SKU_STORAGE_ORDER_LITERAL;
        } else {
            filedLiteral = SKU_STORAGE_ACTUAL_LITERAL;
        }
        Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
        return cb.greaterThanOrEqualTo(diff, 0);
    }

    private boolean storagePatchOpSub(PatchCommand command) {
        return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(SKU_STORAGE_ORDER_LITERAL) ||
                command.getPath().contains(SKU_STORAGE_ACTUAL_LITERAL));
    }
}
