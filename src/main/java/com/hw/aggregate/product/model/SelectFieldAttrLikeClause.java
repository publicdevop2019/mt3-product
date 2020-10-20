package com.hw.aggregate.product.model;

import com.hw.shared.sql.clause.WhereClause;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SelectFieldAttrLikeClause extends WhereClause<Product> {
    @Autowired
    private EntityManager em;

    /**
     * 835716999307264-连衣$835658702675968-裙$835658045743104-下装$835604081303552-服装$835602958278656-女$835604723556352-粉色.白色
     * SELECT p.*
     * FROM biz_product p
     * INNER JOIN biz_product_tag_map ON biz_product_tag_map.product_id = p.id
     * INNER JOIN biz_tag ON biz_product_tag_map.tag_id = biz_tag.id
     * WHERE (biz_tag.value IN ('835716999307264:连衣', '835658702675968:裙'))
     * GROUP BY p.id
     * HAVING COUNT( p.id )=2;
     * @param cb
     * @param root
     * @return
     */
    @Override
    public Predicate getWhereClause(String userInput, CriteriaBuilder cb, Root<Product> root) {
        String replace = userInput.replace("-", ":");
        String[] split = replace.split("\\$");

        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Tag> tagRoot = query.from(Tag.class);
        CriteriaBuilder.In<Object> clause = cb.in(tagRoot.get("value"));
        for (String str : split) {
            clause.value(str);
        }
        query.where(clause);
        Join<Tag, Product> tags = tagRoot.join("products");
        CriteriaQuery<Product> select = query.select(tags);
        TypedQuery<Product> query1 = em.createQuery(select);
        List<Product> resultList = query1.getResultList();


//        Session unwrap = em.unwrap(Session.class);
//        Criteria c = unwrap.createCriteria(Product.class, "u");
//        c.createAlias("u.tags","ut");
//        c.add(Restrictions.in("ut.value",split));
//        List list = c.list();

        List<Predicate> predicates = new ArrayList<>();
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
