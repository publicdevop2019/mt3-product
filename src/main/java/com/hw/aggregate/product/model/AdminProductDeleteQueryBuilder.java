package com.hw.aggregate.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AdminProductDeleteQueryBuilder extends DeleteByIdQueryBuilder<Product> {

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
