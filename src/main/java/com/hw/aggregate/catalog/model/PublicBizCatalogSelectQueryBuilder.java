package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectEqualClause;
import com.hw.shared.sql.clause.SelectFieldEnumStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.catalog.model.BizCatalog.TYPE_LITERAL;

@Component
public class PublicBizCatalogSelectQueryBuilder extends SelectQueryBuilder<BizCatalog> {

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    PublicBizCatalogSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 1500;
//        supportedWhereField.put("type", new SelectFieldEnumStringEqualClause<>(TYPE_LITERAL));
        defaultWhereField.add(new SelectEqualClause<>(TYPE_LITERAL, BizCatalog.CatalogType.FRONTEND.name()));
    }

}
