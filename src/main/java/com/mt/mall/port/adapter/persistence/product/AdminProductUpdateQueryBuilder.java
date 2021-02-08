package com.mt.mall.port.adapter.persistence.product;


import com.mt.mall.domain.model.product.Product;
import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigInteger;

import static com.mt.mall.domain.model.product.Product.PRODUCT_END_AT_LITERAL;
import static com.mt.mall.domain.model.product.Product.PRODUCT_START_AT_LITERAL;
import static com.mt.mall.application.product.representation.ProductRepresentation.ADMIN_REP_END_AT_LITERAL;
import static com.mt.mall.application.product.representation.ProductRepresentation.ADMIN_REP_START_AT_LITERAL;

@Component
public class AdminProductUpdateQueryBuilder extends UpdateByIdQueryBuilder<Product> {
    {
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
