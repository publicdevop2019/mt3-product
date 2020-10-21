package com.hw.aggregate.product.model;

import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.*;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public class SelectProductAttrClause<T> extends WhereClause<T> {
    /**
     * 835602958278656-女$835604723556352-粉色
     * SELECT p.*
     * FROM biz_product p
     * INNER JOIN biz_product_tag_map ON biz_product_tag_map.product_id = p.id
     * INNER JOIN biz_tag ON biz_product_tag_map.tag_id = biz_tag.id
     * WHERE (biz_tag.value IN ('835602958278656:女', '835604723556352:粉色'))
     * GROUP BY p.id
     * HAVING COUNT( p.id )=2;
     *
     * @param cb
     * @param root
     * @return
     */
    @Override
    public Predicate getWhereClause(String userInput, CriteriaBuilder cb, Root<T> root, Object query) {
        String replace = userInput.replace("-", ":");
        String[] split = replace.split("\\$");
        Join<Object, Object> tags = root.join("tags");
        CriteriaBuilder.In<Object> clause = cb.in(tags.get("value"));
        for (String str : split) {
            clause.value(str);
        }
        if (query instanceof CriteriaQuery<?>) {
            CriteriaQuery<?> query2 = (CriteriaQuery<?>) query;
            query2.groupBy(root.get(COMMON_ENTITY_ID));
            query2.having(cb.equal(cb.count(root.get(COMMON_ENTITY_ID)), split.length));
        } else {
            Subquery<?> query2 = (Subquery<?>) query;
            query2.groupBy(root.get(COMMON_ENTITY_ID));
            query2.having(cb.equal(cb.count(root.get(COMMON_ENTITY_ID)), split.length));
        }
        return clause;
    }
}
