package com.mt.mall.port.adapter.persistence.sku;

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
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
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
import static com.mt.mall.port.adapter.persistence.sku.SpringDataJpaSkuRepository.SkuQueryBuilder.SKU_ID_LITERAL;

public interface SpringDataJpaSkuRepository extends SkuRepository, JpaRepository<Sku, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default SkuId nextIdentity() {
        return new SkuId();
    }

    default Optional<Sku> skuOfId(SkuId skuOfId) {
        return getSkuOfId(skuOfId);
    }

    private Optional<Sku> getSkuOfId(SkuId skuId) {
        SqlSelectQueryConverter<Sku> skuSelectQueryBuilder = QueryBuilderRegistry.skuSelectQueryBuilder();
        List<Sku> select = skuSelectQueryBuilder.select(new SkuQuery(skuId), new PageConfig(), Sku.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
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

    default SumPagedRep<Sku> skusOfQuery(SkuQuery query, PageConfig clientPaging, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.skuSelectQueryBuilder(), query, clientPaging, queryConfig, Sku.class);
    }

    default void patchBatch(List<PatchCommand> commands) {
        QueryBuilderRegistry.skuUpdateQueryBuilder().update(commands, Sku.class);
    }

    default SumPagedRep<Sku> skusOfQuery(SkuQuery query, PageConfig clientPaging) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.skuSelectQueryBuilder(), query, clientPaging, new QueryConfig(), Sku.class);
    }

    @Component
    class SkuQueryBuilder extends SqlSelectQueryConverter<Sku> {
        public static final String SKU_ID_LITERAL = "skuId";

        {
            supportedSort.put("id",SKU_ID_LITERAL);
            supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(SKU_ID_LITERAL));
            supportedWhere.put(SKU_REFERENCE_ID_LITERAL, new FieldStringEqualClause<>(SKU_REFERENCE_ID_LITERAL));
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
