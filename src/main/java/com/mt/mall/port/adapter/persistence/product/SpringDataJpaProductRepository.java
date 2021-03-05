package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.restful.exception.UnsupportedPatchOperationException;
import com.mt.common.domain.model.restful.exception.UpdateFiledValueException;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SqlSelectQueryConverter;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldNumberRangeClause;
import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;
import com.mt.common.domain.model.sql.clause.WhereClause;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductQuery;
import com.mt.mall.domain.model.product.ProductRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.application.product.representation.ProductRepresentation.*;
import static com.mt.mall.domain.model.product.Product.*;
import static com.mt.mall.domain.model.product.ProductQuery.AVAILABLE;

public interface SpringDataJpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default ProductId nextIdentity() {
        return new ProductId();
    }

    default Optional<Product> productOfId(ProductId productId) {
        return getProductOfId(productId, false);
    }

    default Optional<Product> publicProductOfId(ProductId productId) {
        return getProductOfId(productId, true);
    }

    private Optional<Product> getProductOfId(ProductId productId, boolean isPublic) {
        ProductQueryBuilder publicProductSelectQueryBuilder = QueryBuilderRegistry.productSelectQueryBuilder();
        List<Product> select = publicProductSelectQueryBuilder.select(new ProductQuery(productId, isPublic), new PageConfig(), Product.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
    }

    default void add(Product client) {
        save(client);
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.productUpdateQueryBuilder().update(commands, Product.class);
    }

    default void remove(Product client) {
        softDelete(client.getId());
    }

    default void remove(Set<Product> products) {
        softDeleteAll(products.stream().map(Product::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Product> productsOfQuery(ProductQuery query, PageConfig clientPaging, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.productSelectQueryBuilder(), query, clientPaging, queryConfig, Product.class);
    }

    default SumPagedRep<Product> productsOfQuery(ProductQuery query, PageConfig clientPaging) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.productSelectQueryBuilder(), query, clientPaging, new QueryConfig(), Product.class);
    }

    @Component
    class ProductQueryBuilder extends SqlSelectQueryConverter<Product> {
        public static final String PUBLIC_ATTR = "attr";
        private static final String PRODUCT_ID_LITERAL = "productId";

        {
            supportedSort.put("id", PRODUCT_ID_LITERAL);
            supportedSort.put(ADMIN_REP_NAME_LITERAL, PRODUCT_NAME_LITERAL);
            supportedSort.put(ADMIN_REP_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL);
            supportedSort.put(ADMIN_REP_PRICE_LITERAL, PRODUCT_LOWEST_PRICE_LITERAL);
            supportedSort.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
            supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(PRODUCT_ID_LITERAL));
            supportedWhere.put(PUBLIC_ATTR, new SelectProductAttrClause<>());
            supportedWhere.put("attributes", new SelectProductAttrClause<>());
            supportedWhere.put(ADMIN_REP_NAME_LITERAL, new FieldStringLikeClause<>(PRODUCT_NAME_LITERAL));
            supportedWhere.put(ADMIN_REP_PRICE_LITERAL, new FieldNumberRangeClause<>(PRODUCT_LOWEST_PRICE_LITERAL));
            supportedWhere.put(AVAILABLE, new SelectStatusClause<>());
        }

        public static class SelectProductAttrClause<T> extends WhereClause<T> {
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

        public static class SelectStatusClause<T> extends WhereClause<T> {
            @Override
            public Predicate getWhereClause(String str, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> query) {
                Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get(PRODUCT_START_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
                Predicate startAtNotNull = cb.isNotNull(root.get(PRODUCT_START_AT_LITERAL).as(Long.class));
                Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
                Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get(PRODUCT_END_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
                Predicate endAtIsNull = cb.isNull(root.get(PRODUCT_END_AT_LITERAL).as(Long.class));
                Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
                return cb.and(and, or);
            }
        }
    }

    @Component
    class ProductUpdateQueryBuilder extends UpdateQueryBuilder<Product> {
        protected Map<String, String> filedMap = new HashMap<>();
        protected Map<String, Function<Object, ?>> filedTypeMap = new HashMap<>();

        {
            filedMap.put(ADMIN_REP_START_AT_LITERAL, PRODUCT_START_AT_LITERAL);
            filedMap.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
            filedTypeMap.put(ADMIN_REP_START_AT_LITERAL, this::parseLong);
            filedTypeMap.put(ADMIN_REP_END_AT_LITERAL, this::parseLong);
        }

        //    [
        //    {"op":"add","path":"/storageOrder","value":"1"},
        //    {"op":"sub","path":"/storageActual","value":"2"}
        //    ]
        @Override
        protected void setUpdateValue(Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand command) {
            ArrayList<Boolean> booleans = new ArrayList<>();
            booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL, root, criteriaUpdate, command));
            filedMap.keySet().forEach(e -> {
                booleans.add(setUpdateValueFor(e, filedMap.get(e), criteriaUpdate, command));
            });
            Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
            if (!hasFieldChange) {
                throw new NoUpdatableFieldException();
            }
        }

        @Override
        public Predicate getWhereClause(Root<Product> root, List<String> search, PatchCommand command) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            List<Predicate> results = new ArrayList<>();
            for (String str : search) {
                //make sure if storage change, value is not negative
                Predicate equal = cb.equal(root.get(PRODUCT_PRODUCT_ID).get(DOMAIN_ID), str);
                if (storagePatchOpSub(command)) {
                    Predicate negativeClause = getStorageMustNotNegativeClause(cb, root, command);
                    Predicate and = cb.and(equal, negativeClause);
                    results.add(and);
                } else {
                    results.add(equal);
                }
            }
            return cb.or(results.toArray(new Predicate[0]));
        }

        private Predicate getStorageMustNotNegativeClause(CriteriaBuilder cb, Root<Product> root, PatchCommand command) {
            String filedLiteral = PRODUCT_TOTAL_SALES_LITERAL;
            Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
            return cb.greaterThanOrEqualTo(diff, 0);
        }

        private boolean storagePatchOpSub(PatchCommand command) {
            return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(ADMIN_REP_SALES_LITERAL));
        }

        private Boolean setUpdateStorageValueFor(String fieldPath, String filedLiteral, Root<Product> root, CriteriaUpdate<Product> criteriaUpdate, PatchCommand e) {
            if (e.getPath().equalsIgnoreCase(fieldPath)) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_SUM)) {
                    criteriaUpdate.set(root.<Integer>get(filedLiteral), cb.sum(root.get(filedLiteral), parseInteger(e.getValue())));
                    return true;
                } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF)) {
                    criteriaUpdate.set(root.<Integer>get(filedLiteral), cb.diff(root.get(filedLiteral), parseInteger(e.getValue())));
                    return true;
                } else {
                    throw new UnsupportedPatchOperationException();
                }
            } else {
                return false;
            }
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

        private Integer parseInteger(@Nullable Object input) {
            return parseLong(input).intValue();
        }

        private boolean setUpdateValueFor(String fieldPath, String fieldLiteral, CriteriaUpdate<Product> criteriaUpdate, PatchCommand command) {
            if (command.getPath().contains(fieldPath)) {
                if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                    criteriaUpdate.set(fieldLiteral, null);
                    return true;
                } else if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_ADD) || command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
                    if (command.getValue() != null) {
                        criteriaUpdate.set(fieldLiteral, filedTypeMap.get(fieldPath).apply(command.getValue()));
                    } else {
                        criteriaUpdate.set(fieldLiteral, null);
                    }
                    return true;
                } else {
                    throw new UnsupportedPatchOperationException();
                }
            } else {
                return false;
            }
        }

    }
}
