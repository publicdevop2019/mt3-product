package com.hw.aggregate.product.model;


import com.hw.aggregate.product.exception.NoUpdatableFieldException;
import com.hw.aggregate.product.exception.UnsupportedPatchOperationException;
import com.hw.aggregate.product.exception.UpdateFiledValueException;
import com.hw.shared.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaUpdate;
import java.math.BigInteger;

import static com.hw.aggregate.product.model.ProductDetail.END_AT_LITERAL;
import static com.hw.aggregate.product.model.ProductDetail.START_AT_LITERAL;

@Component
public class AdminUpdateQueryBuilder extends UpdateQueryBuilder<ProductDetail> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    protected void setUpdateValue(CriteriaUpdate<ProductDetail> criteriaUpdate, PatchCommand e) {
        if (e.getOp().equalsIgnoreCase("remove")) {
            int count = 0;
            if (e.getPath().contains("endAt")) {
                criteriaUpdate.set(END_AT_LITERAL, null);
                count++;
            }
            if (e.getPath().contains("startAt")) {
                criteriaUpdate.set(START_AT_LITERAL, null);
                count++;
            }
            if (count == 0)
                throw new NoUpdatableFieldException();
        } else if (e.getOp().equalsIgnoreCase("add") || e.getOp().equalsIgnoreCase("replace")) {
            int count = 0;
            if (e.getPath().contains("endAt")) {
                if (e.getValue() != null) {
                    criteriaUpdate.set(END_AT_LITERAL, parseLong(e.getValue()));
                } else {
                    criteriaUpdate.set(END_AT_LITERAL, null);
                }
                count++;
            }
            if (e.getPath().contains("startAt")) {
                if (e.getValue() != null) {
                    criteriaUpdate.set(START_AT_LITERAL, parseLong(e.getValue()));
                } else {
                    criteriaUpdate.set(START_AT_LITERAL, null);
                }
                count++;
            }
            if (count == 0)
                throw new NoUpdatableFieldException();
        } else {
            throw new UnsupportedPatchOperationException();
        }
    }

    private Long parseLong(Object input) {
        try {
            if (input.getClass().equals(Integer.class))
                return ((Integer) input).longValue();
            if (input.getClass().equals(BigInteger.class))
                return ((BigInteger) input).longValue();
            return Long.parseLong((String) input);
        } catch (NumberFormatException ex) {
            throw new UpdateFiledValueException();
        }
    }

}
