package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.domain.model.sql.clause.WhereClause;
import com.mt.mall.domain.model.product.Product;

import javax.persistence.criteria.*;
import java.util.HashSet;

public class SelectProductAttrClause<T> extends WhereClause<T> {
    /**
     * SELECT  * from biz_product bp where bp.id in
     * (
     * SELECT product0_.id FROM biz_product product0_ inner join biz_product_tag_map tags1_ on product0_.id=tags1_.product_id
     * inner join biz_tag tag2_ on tags1_.tag_id=tag2_.id
     * where tag2_.value in ('835604081303552:cloth')
     * )
     * and bp.id in
     * (
     * SELECT  bp3.id FROM  biz_product bp3 inner join biz_product_tag_map tags1_ on bp3.id=tags1_.product_id
     * inner join biz_tag tag2_ on tags1_.tag_id=tag2_.id
     * where tag2_.value in ('835602958278656:women','835602958278656:man')
     * ) ORDER BY bp.id DESC
     *
     * @param cb
     * @param root
     * @return
     */
    @Override
    public Predicate getWhereClause(String userInput, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> query) {
        String[] split = userInput.split("\\$");
        Predicate id = null;
        for (String s : split) {
            //835604723556352-粉色.白色.灰色
            String[] split1 = s.split("-");
            String[] split2 = split1[1].split("\\.");
            HashSet<String> strings = new HashSet<>();
            for (String str : split2) {
                strings.add(split1[0] + ":" + str);
            }
            Subquery<Product> subquery;
            if (query instanceof CriteriaQuery<?>) {

                CriteriaQuery<?> query2 = (CriteriaQuery<?>) query;
                subquery = query2.subquery(Product.class);
            } else {
                Subquery<?> query2 = (Subquery<?>) query;
                subquery = query2.subquery(Product.class);
            }
            Root<Product> from = subquery.from(Product.class);
            subquery.select(from.get("id"));
            Join<Object, Object> tags = from.join("tags");
            CriteriaBuilder.In<Object> clause = cb.in(tags.get("value"));
            for (String str : strings) {
                clause.value(str);
            }
            subquery.where(clause);
            if (id != null)
                id = cb.and(id, cb.in(root.get("id")).value(subquery));
            else
                id = cb.in(root.get("id")).value(subquery);
        }
        return id;
    }
}
