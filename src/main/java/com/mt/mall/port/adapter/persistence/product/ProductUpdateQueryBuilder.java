package com.mt.mall.port.adapter.persistence.product;


import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.restful.exception.UnsupportedPatchOperationException;
import com.mt.common.domain.model.restful.exception.UpdateFiledValueException;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.mall.domain.model.product.Product;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.application.product.representation.ProductRepresentation.*;
import static com.mt.mall.domain.model.product.Product.*;

@Component
public class ProductUpdateQueryBuilder extends UpdateQueryBuilder<Product> {
    protected Map<String, String> filedMap = new HashMap<>();
    protected Map<String, Function<Object, ?>> filedTypeMap = new HashMap<>();

    {
        filedMap.put(ADMIN_REP_START_AT_LITERAL, PRODUCT_START_AT_LITERAL);
        filedMap.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
        filedTypeMap.put(ADMIN_REP_START_AT_LITERAL, this::parseLong);
        filedTypeMap.put(ADMIN_REP_END_AT_LITERAL, this::parseLong);
    }

    //    [
    //    {"op":"add","path":"/storageOrder","value":"1"},
    //    {"op":"sub","path":"/storageActual","value":"2"}
    //    ]
    @Override
    protected void setUpdateValue(Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand command) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL, root, criteriaUpdate, command));
        filedMap.keySet().forEach(e -> {
            booleans.add(setUpdateValueFor(e, filedMap.get(e), criteriaUpdate, command));
        });
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    @Override
    public Predicate getWhereClause(Root<Product> root, List<String> search, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : search) {
            //make sure if storage change, value is not negative
            Predicate equal = cb.equal(root.get(PRODUCT_PRODUCT_ID).get(DOMAIN_ID), str);
            if (storagePatchOpSub(command)) {
                Predicate negativeClause = getStorageMustNotNegativeClause(cb, root, command);
                Predicate and = cb.and(equal, negativeClause);
                results.add(and);
            } else {
                results.add(equal);
            }
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Predicate getStorageMustNotNegativeClause(CriteriaBuilder cb, Root<Product> root, PatchCommand command) {
        String filedLiteral = PRODUCT_TOTAL_SALES_LITERAL;
        Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
        return cb.greaterThanOrEqualTo(diff, 0);
    }

    private boolean storagePatchOpSub(PatchCommand command) {
        return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(ADMIN_REP_SALES_LITERAL));
    }

    private Boolean setUpdateStorageValueFor(String fieldPath, String filedLiteral, Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
        if (e.getPath().equalsIgnoreCase(fieldPath)) {
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

    private boolean setUpdateValueFor(String fieldPath, String fieldLiteral, CriteriaUpdate<Product> criteriaUpdate, PatchCommand command) {
        if (command.getPath().contains(fieldPath)) {
            if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                criteriaUpdate.set(fieldLiteral, null);
                return true;
            } else if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_ADD) || command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
                if (command.getValue() != null) {
                    criteriaUpdate.set(fieldLiteral, filedTypeMap.get(fieldPath).apply(command.getValue()));
                } else {
                    criteriaUpdate.set(fieldLiteral, null);
                }
                return true;
            } else {
                throw new UnsupportedPatchOperationException();
            }
        } else {
            return false;
        }
    }

}
