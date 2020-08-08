package com.hw.aggregate.product.model;


import com.hw.aggregate.product.exception.NoUpdatableFieldException;
import com.hw.aggregate.product.exception.UnsupportedPatchOperationException;
import com.hw.aggregate.product.exception.UpdateFiledValueException;
import com.hw.shared.PatchCommand;
import com.hw.shared.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.ProductDetailAdminRep.*;
import static com.hw.shared.AppConstant.*;

@Component
public class AdminProductUpdateQueryBuilder extends UpdateQueryBuilder<Product> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    //    [
    //    {"op":"add","path":"/storageOrder","value":"1"},
    //    {"op":"sub","path":"/storageActual","value":"2"}
    //    ]
    @Override
    protected void setUpdateValue(Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(setUpdateValueFor("/" + ADMIN_REP_START_AT_LITERAL, START_AT_LITERAL, criteriaUpdate, e));
        booleans.add(setUpdateValueFor("/" + ADMIN_REP_END_AT_LITERAL, END_AT_LITERAL, criteriaUpdate, e));
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_STORAGE_ORDER_LITERAL, STORAGE_ORDER_LITERAL, root, criteriaUpdate, e));
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_STORAGE_ACTUAL_LITERAL, STORAGE_ACTUAL_LITERAL, root, criteriaUpdate, e));
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
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


    private boolean setUpdateValueFor(String fieldPath, String fieldLiteral, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
        if (e.getPath().equalsIgnoreCase(fieldPath)) {
            if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                criteriaUpdate.set(fieldLiteral, null);
                return true;
            } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_SUM) || e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
                if (e.getValue() != null) {
                    criteriaUpdate.set(fieldLiteral, parseLong(e.getValue()));
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


    @Override
    public Predicate getWhereClause(Root<Product> root, List<String> search, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : search) {
            //make sure if storage change, value is not negative
            Predicate equal = cb.equal(root.get(ID_LITERAL), Long.parseLong(str));
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
        String filedLiteral;
        if (command.getPath().equalsIgnoreCase(ADMIN_REP_STORAGE_ORDER_LITERAL)) {
            filedLiteral = STORAGE_ORDER_LITERAL;
        } else {
            filedLiteral = STORAGE_ACTUAL_LITERAL;
        }
        Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
        return cb.greaterThanOrEqualTo(diff, 0);
    }

    private boolean storagePatchOpSub(PatchCommand command) {
        return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(ADMIN_REP_STORAGE_ORDER_LITERAL) ||
                command.getPath().contains(ADMIN_REP_STORAGE_ACTUAL_LITERAL));
    }

}
