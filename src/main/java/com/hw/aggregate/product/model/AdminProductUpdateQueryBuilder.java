package com.hw.aggregate.product.model;


import com.hw.aggregate.product.exception.NoUpdatableFieldException;
import com.hw.aggregate.product.exception.UnsupportedPatchOperationException;
import com.hw.aggregate.product.exception.UpdateFiledValueException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.builder.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_END_AT_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_START_AT_LITERAL;
import static com.hw.shared.AppConstant.*;

@Component
public class AdminProductUpdateQueryBuilder extends UpdateQueryBuilder<Product> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @Override
    protected void setUpdateValue(Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(setUpdateValueFor("/" + ADMIN_REP_START_AT_LITERAL, START_AT_LITERAL, criteriaUpdate, e));
        booleans.add(setUpdateValueFor("/" + ADMIN_REP_END_AT_LITERAL, END_AT_LITERAL, criteriaUpdate, e));
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    private boolean setUpdateValueFor(String fieldPath, String fieldLiteral, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
        if (e.getPath().equalsIgnoreCase(fieldPath)) {
            if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                criteriaUpdate.set(fieldLiteral, null);
                return true;
            } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_ADD) || e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
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


    @Override
    public Predicate getWhereClause(Root<Product> root, List<String> search, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : search) {
            Predicate equal = cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str));
            results.add(equal);
        }
        return cb.or(results.toArray(new Predicate[0]));
    }


}
