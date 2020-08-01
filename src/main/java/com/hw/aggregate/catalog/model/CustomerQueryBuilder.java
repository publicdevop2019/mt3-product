package com.hw.aggregate.catalog.model;

import com.hw.shared.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;

import static com.hw.aggregate.catalog.model.Catalog.ID_LITERAL;

@Component("catalogCustomer")
public class CustomerQueryBuilder extends QueryBuilder<Catalog> {
    @Autowired
    private AdminQueryBuilder adminQueryBuilder;

    @Override
    public Predicate getQueryClause(CriteriaBuilder cb, Root<Catalog> root, String search) {
        return adminQueryBuilder.getQueryClause(cb, root, "type:" + CatalogType.FRONTEND.name());
    }

    CustomerQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 1500;
        DEFAULT_SORT_BY = "id";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("id", ID_LITERAL);
    }

}
