package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.DeleteByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import static com.hw.aggregate.product.model.ProductSku.SKU_PRODUCT_ID_LITERAL;

@Component
public class AdminProductSkuDeleteQueryBuilder extends DeleteByIdQueryBuilder<ProductSku> {
    @PostConstruct
    private void setUp() {
        mappedSqlFieldLiteral = SKU_PRODUCT_ID_LITERAL;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
