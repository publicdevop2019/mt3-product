package com.mt.mall.port.adapter.persistence.sku;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.restful.exception.UnsupportedPatchOperationException;
import com.mt.common.domain.model.restful.exception.UpdateFiledValueException;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import com.mt.mall.domain.model.sku.SkuQuery;
import com.mt.mall.domain.model.sku.SkuRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.domain.model.sku.Sku.*;
import static com.mt.mall.port.adapter.persistence.sku.SpringDataJpaSkuRepository.JpaCriteriaApiSkuAdapter.SKU_ID_LITERAL;

public interface SpringDataJpaSkuRepository extends SkuRepository, JpaRepository<Sku, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Sku> skuOfId(SkuId skuOfId) {
        return skusOfQuery(new SkuQuery(skuOfId)).findFirst();
    }

    default void add(Sku client) {
        save(client);
    }

    default void remove(Sku client) {
        softDelete(client.getId());
    }

    default void remove(Set<Sku> client) {
        softDeleteAll(client.stream().map(Sku::getId).collect(Collectors.toSet()));
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.getSkuUpdateQueryBuilder().update(commands, Sku.class);
    }

    default SumPagedRep<Sku> skusOfQuery(SkuQuery skuQuery) {
        return QueryBuilderRegistry.getSkuSelectQueryBuilder().execute(skuQuery);
    }

    @Component
    class JpaCriteriaApiSkuAdapter {
        public static final String SKU_ID_LITERAL = "skuId";

        public SumPagedRep<Sku> execute(SkuQuery skuQuery) {
            QueryUtility.QueryContext<Sku> queryContext = QueryUtility.prepareContext(Sku.class, skuQuery);
            Optional.ofNullable(skuQuery.getSkuIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(skuQuery.getSkuIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), SKU_ID_LITERAL, queryContext));
            Optional.ofNullable(skuQuery.getProductId()).ifPresent(e -> QueryUtility.addStringEqualPredicate(skuQuery.getProductId().getDomainId(), SKU_REFERENCE_ID_LITERAL, queryContext));
            Order order = null;
            if (skuQuery.getSkuSort().isById())
                order = QueryUtility.getDomainIdOrder(SKU_ID_LITERAL, queryContext, skuQuery.getSkuSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(skuQuery, queryContext);
        }
    }

    @Component
    class SkuUpdateQueryBuilder extends UpdateQueryBuilder<Sku> {

        @Override
        protected void setUpdateValue(Root<Sku> root, CriteriaUpdate<Sku> criteriaUpdate, PatchCommand e) {
            ArrayList<Boolean> booleans = new ArrayList<>();
            booleans.add(setUpdateStorageValueFor("/" + SKU_STORAGE_ORDER_LITERAL, SKU_STORAGE_ORDER_LITERAL, root, criteriaUpdate, e));
            booleans.add(setUpdateStorageValueFor("/" + SKU_STORAGE_ACTUAL_LITERAL, SKU_STORAGE_ACTUAL_LITERAL, root, criteriaUpdate, e));
            booleans.add(setUpdateStorageValueFor("/" + SKU_SALES_LITERAL, SKU_SALES_LITERAL, root, criteriaUpdate, e));
            Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
            if (!hasFieldChange) {
                throw new NoUpdatableFieldException();
            }
        }

        @Override
        protected Predicate getWhereClause(Root<Sku> root, List<String> ids, PatchCommand command) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            List<Predicate> results = new ArrayList<>();
            for (String id : ids) {
                Predicate idClause = cb.equal(root.get(SKU_ID_LITERAL).get(DOMAIN_ID), id);
                if (storagePatchOpSub(command)) {
                    //make sure if storage change, value is not negative
                    Predicate negativeClause = getStorageMustNotNegativeClause(cb, root, command);
                    Predicate and = cb.and(idClause, negativeClause);
                    results.add(and);
                } else {
                    results.add(idClause);
                }
            }
            return cb.or(results.toArray(new Predicate[0]));
        }

        protected Boolean setUpdateStorageValueFor(String fieldPath, String filedLiteral, Root<Sku> root, CriteriaUpdate<Sku> criteriaUpdate, PatchCommand e) {
            if (e.getPath().contains(fieldPath)) {
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

        private Predicate getStorageMustNotNegativeClause(CriteriaBuilder cb, Root<Sku> root, PatchCommand command) {
            String filedLiteral;
            if (command.getPath().contains(SKU_STORAGE_ORDER_LITERAL)) {
                filedLiteral = SKU_STORAGE_ORDER_LITERAL;
            } else {
                filedLiteral = SKU_STORAGE_ACTUAL_LITERAL;
            }
            Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
            return cb.greaterThanOrEqualTo(diff, 0);
        }

        private boolean storagePatchOpSub(PatchCommand command) {
            return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(SKU_STORAGE_ORDER_LITERAL) ||
                    command.getPath().contains(SKU_STORAGE_ACTUAL_LITERAL));
        }
    }
}
