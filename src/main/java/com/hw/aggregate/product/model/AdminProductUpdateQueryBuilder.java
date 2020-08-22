package com.hw.aggregate.product.model;


import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.math.BigInteger;

import static com.hw.aggregate.product.model.Product.PRODUCT_END_AT_LITERAL;
import static com.hw.aggregate.product.model.Product.PRODUCT_START_AT_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_END_AT_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_START_AT_LITERAL;

@Component
public class AdminProductUpdateQueryBuilder extends UpdateByIdQueryBuilder<Product> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @PostConstruct
    private void setUp() {
        filedMap.put(ADMIN_REP_START_AT_LITERAL, PRODUCT_START_AT_LITERAL);
        filedMap.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
        filedTypeMap.put(ADMIN_REP_START_AT_LITERAL, this::parseLong);
        filedTypeMap.put(ADMIN_REP_END_AT_LITERAL, this::parseLong);
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


}
