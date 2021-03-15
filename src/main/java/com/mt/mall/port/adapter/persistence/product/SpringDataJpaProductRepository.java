package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.restful.exception.UnsupportedPatchOperationException;
import com.mt.common.domain.model.restful.exception.UpdateFiledValueException;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
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
import java.util.stream.IntStream;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.application.product.representation.ProductRepresentation.*;
import static com.mt.mall.domain.model.product.Product.*;

public interface SpringDataJpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Product> productOfId(ProductId productId) {
        return getProductOfId(productId, false);
    }

    default Optional<Product> publicProductOfId(ProductId productId) {
        return getProductOfId(productId, true);
    }

    private Optional<Product> getProductOfId(ProductId productId, boolean isPublic) {
        return productsOfQuery(new ProductQuery(productId, isPublic)).findFirst();
    }

    default void add(Product client) {
        save(client);
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.getProductUpdateQueryBuilder().update(commands, Product.class);
    }

    default void remove(Product client) {
        softDelete(client.getId());
    }

    default void remove(Set<Product> products) {
        softDeleteAll(products.stream().map(Product::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Product> productsOfQuery(ProductQuery query) {
        if (query.getTagId() != null) {
            //in-memory search
            List<Product> all2 = findAll();
            List<Product> collect = all2.stream().filter(e -> e.getTags().stream().anyMatch(ee -> ee.getTagId().equals(query.getTagId()))).collect(Collectors.toList());
            long offset = query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
            List<Product> collect1 = IntStream.range(0, collect.size()).filter(i -> i >= offset && i < (offset + query.getPageConfig().getPageSize())).boxed().map(collect::get).collect(Collectors.toList());
            return new SumPagedRep<>(collect1, (long) collect.size());
        }
        return QueryBuilderRegistry.getProductSelectQueryBuilder().execute(query);
    }

    @Component
    class JpaCriteriaApiProductAdaptor {
        private static final String PRODUCT_ID_LITERAL = "productId";

        public SumPagedRep<Product> execute(ProductQuery productQuery) {
            QueryUtility.QueryContext<Product> queryContext = QueryUtility.prepareContext(Product.class, productQuery);
            Optional.ofNullable(productQuery.getNames()).ifPresent(e -> {
                QueryUtility.addStringInPredicate(productQuery.getNames(), PRODUCT_NAME_LITERAL, queryContext);
            });
            Optional.ofNullable(productQuery.getProductIds()).ifPresent(e -> {
                QueryUtility.addDomainIdInPredicate(productQuery.getProductIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), PRODUCT_ID_LITERAL, queryContext);
            });
            Optional.ofNullable(productQuery.getTagSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(ProductTagPredicateConverter.getPredicate(productQuery.getTagSearch(), queryContext.getCriteriaBuilder(), queryContext.getRoot(), queryContext.getQuery()));
                queryContext.getCountPredicates().add(ProductTagPredicateConverter.getPredicate(productQuery.getTagSearch(), queryContext.getCriteriaBuilder(), queryContext.getCountRoot(), queryContext.getCountQuery()));
            });
            Optional.ofNullable(productQuery.getPriceSearch()).ifPresent(e -> {
                QueryUtility.addNumberRagePredicate(productQuery.getPriceSearch(), PRODUCT_LOWEST_PRICE_LITERAL, queryContext);
            });
            if (productQuery.isAvailable()) {
                queryContext.getPredicates().add(ProductStatusPredicateConverter.getPredicate(queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                queryContext.getCountPredicates().add(ProductStatusPredicateConverter.getPredicate(queryContext.getCriteriaBuilder(), queryContext.getCountRoot()));
            }
            Order order = null;
            if (productQuery.getProductSort().isById())
                order = QueryUtility.getDomainIdOrder(PRODUCT_ID_LITERAL, queryContext, productQuery.getProductSort().isAsc());
            if (productQuery.getProductSort().isByName())
                order = QueryUtility.getOrder(PRODUCT_NAME_LITERAL, queryContext, productQuery.getProductSort().isAsc());
            if (productQuery.getProductSort().isByTotalSale())
                order = QueryUtility.getOrder(PRODUCT_TOTAL_SALES_LITERAL, queryContext, productQuery.getProductSort().isAsc());
            if (productQuery.getProductSort().isByLowestPrice())
                order = QueryUtility.getOrder(PRODUCT_LOWEST_PRICE_LITERAL, queryContext, productQuery.getProductSort().isAsc());
            if (productQuery.getProductSort().isByEndAt())
                order = QueryUtility.getOrder(PRODUCT_END_AT_LITERAL, queryContext, productQuery.getProductSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(productQuery, queryContext);
        }

        public static class ProductTagPredicateConverter {
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
             * @return
             */
            public static Predicate getPredicate(String userInput, CriteriaBuilder cb, Root<Product> root, AbstractQuery<?> query) {
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

        public static class ProductStatusPredicateConverter {
            public static Predicate getPredicate(CriteriaBuilder cb, Root<Product> root) {
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
