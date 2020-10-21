package com.hw.shared.sql.builder;

import com.hw.shared.Auditable;
import com.hw.shared.sql.clause.SelectFieldIdWhereClause;
import com.hw.shared.sql.clause.SelectNotDeletedClause;
import com.hw.shared.sql.exception.EmptyWhereClauseException;
import com.hw.shared.sql.exception.MaxPageSizeExceedException;
import com.hw.shared.sql.exception.UnsupportedQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public abstract class SelectQueryBuilder<T extends Auditable> extends PredicateConfig<T> {
    protected Integer DEFAULT_PAGE_SIZE = 10;
    protected Integer MAX_PAGE_SIZE = 20;
    protected Integer DEFAULT_PAGE_NUM = 0;
    protected String DEFAULT_SORT_BY = COMMON_ENTITY_ID;
    protected Map<String, String> mappedSortBy = new HashMap<>();
    protected Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;
    @Autowired
    protected EntityManager em;

    protected SelectQueryBuilder() {
        mappedSortBy.put(COMMON_ENTITY_ID, COMMON_ENTITY_ID);
        supportedWhereField.put(COMMON_ENTITY_ID, new SelectFieldIdWhereClause());
    }

    public List<T> select(String search, String page, Class<T> clazz) {
        if (search == null && !allowEmptyClause) {
            throw new EmptyWhereClauseException();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        PageRequest pageRequest = getPageRequest(page);
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Set<Order> orders = pageRequest.getSort().get().map(e -> {
            if (e.getDirection().isAscending()) {
                return cb.asc(root.get(e.getProperty()));
            } else {
                return cb.desc(root.get(e.getProperty()));
            }
        }).collect(Collectors.toSet());
        query.select(root);
        if (search != null) {
            Predicate and = getPredicateEx(Arrays.stream(search.split(",")).collect(Collectors.toList()), cb, root, query);
            query.where(and);
        }
        query.orderBy(orders.toArray(Order[]::new));

        TypedQuery<T> query1 = em.createQuery(query)
                .setFirstResult(BigDecimal.valueOf(pageRequest.getOffset()).intValue())
                .setMaxResults(pageRequest.getPageSize());
        return query1.getResultList();
    }

    private Predicate getPredicateEx(List<String> search, CriteriaBuilder cb, Root<T> root, Object query) {
        Predicate predicateEx = super.getPredicate(search, cb, root, query);
        //force to select only not deleted entity
        Predicate notSoftDeleted = new SelectNotDeletedClause<T>().getWhereClause(cb, root);
        return cb.and(predicateEx, notSoftDeleted);
    }

    public Long selectCount(String search, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Subquery<T> subquery = query.subquery(clazz);
        Root<T> root = query.from(clazz);
        query.select(cb.count(root));
        Root<T> from = subquery.from(clazz);
        subquery.select(from.get(COMMON_ENTITY_ID));
        if (search != null) {
            Predicate and = getPredicateEx(Arrays.stream(search.split(",")).collect(Collectors.toList()), cb, from, subquery);
            subquery.where(and);
        }
        query.where(cb.in(root).value(subquery));
        return em.createQuery(query).getSingleResult();
    }

    private PageRequest getPageRequest(String page) {
        if (page == null) {
            Sort sort = new Sort(DEFAULT_SORT_ORDER, mappedSortBy.get(DEFAULT_SORT_BY));
            return PageRequest.of(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, sort);
        }
        String[] params = page.split(",");
        Integer pageNumber = DEFAULT_PAGE_NUM;
        Integer pageSize = DEFAULT_PAGE_SIZE;
        String sortBy = mappedSortBy.get(DEFAULT_SORT_BY);
        Sort.Direction sortOrder = DEFAULT_SORT_ORDER;
        for (String param : params) {
            String[] values = param.split(":");
            if (values[0].equals("num") && values[1] != null) {
                pageNumber = Integer.parseInt(values[1]);
            }
            if (values[0].equals("size") && values[1] != null) {
                pageSize = Integer.parseInt(values[1]);
            }
            if (values[0].equals("by") && values[1] != null) {
                sortBy = mappedSortBy.get(values[1]);
                if (sortBy == null)
                    throw new UnsupportedQueryException();
            }
            if (values[0].equals("order") && values[1] != null) {
                sortOrder = Sort.Direction.fromString(values[1]);
            }
        }
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        Sort sort = new Sort(sortOrder, sortBy);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

}
