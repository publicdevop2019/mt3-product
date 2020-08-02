package com.hw.aggregate.attribute.model;

import com.hw.shared.SelectQueryBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;

import static com.hw.aggregate.attribute.model.BizAttribute.*;


@Component("attributeAdmin")
public class AdminQueryBuilder extends SelectQueryBuilder<BizAttribute> {
    @Override
    public Predicate getQueryClause(CriteriaBuilder cb, Root<BizAttribute> root, String search) {
        return null;
    }

    AdminQueryBuilder() {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 200;
        DEFAULT_SORT_BY = "id";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("id", ID_LITERAL);
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("type", TYPE_LITERAL);
    }

}
