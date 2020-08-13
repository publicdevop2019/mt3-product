package com.hw.aggregate.catalog.model;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.math.BigInteger;

import static com.hw.aggregate.catalog.model.BizCatalog.PARENT_ID_LITERAL;
import static com.hw.aggregate.catalog.model.BizCatalog.TYPE_LITERAL;
import static com.hw.aggregate.catalog.representation.AdminBizCatalogCardRep.ADMIN_REP_CATALOG_TYPE_LITERAL;
import static com.hw.aggregate.catalog.representation.AdminBizCatalogCardRep.ADMIN_REP_PARENT_ID_LITERAL;


@Component
public class AdminBizCatalogUpdateQueryBuilder extends UpdateByIdQueryBuilder<BizCatalog> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @PostConstruct
    private void setUp() {
        filedMap.put(ADMIN_REP_PARENT_ID_LITERAL, PARENT_ID_LITERAL);
        filedMap.put(ADMIN_REP_CATALOG_TYPE_LITERAL, TYPE_LITERAL);
        filedTypeMap.put(ADMIN_REP_PARENT_ID_LITERAL, this::parseLong);
        filedTypeMap.put(ADMIN_REP_CATALOG_TYPE_LITERAL, this::parseType);
    }

    private BizCatalog.CatalogType parseType(Object o) {
        return BizCatalog.CatalogType.valueOf((String) o);
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
