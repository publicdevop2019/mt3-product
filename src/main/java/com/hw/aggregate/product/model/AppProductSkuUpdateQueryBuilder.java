package com.hw.aggregate.product.model;


import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.rest.exception.UnsupportedPatchOperationException;
import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.builder.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.ProductSku.*;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SALES_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ProductSkuAdminRepresentation.*;
import static com.hw.shared.AppConstant.*;

@Component
public class AppProductSkuUpdateQueryBuilder extends UpdateQueryBuilder<ProductSku> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    //    [
    //    {"op":"add","path":"/001/skus/?query=attributeSales:8001-foo,8002-bar/storageOrder","value":"1"}
    //    ]
    @Override
    protected void setUpdateValue(Root<ProductSku> root, CriteriaUpdate<ProductSku> criteriaUpdate, PatchCommand e) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_SKU_STORAGE_ORDER_LITERAL, SKU_STORAGE_ORDER_LITERAL, root, criteriaUpdate, e));
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL, SKU_STORAGE_ACTUAL_LITERAL, root, criteriaUpdate, e));
        booleans.add(setUpdateStorageValueFor("/" + ADMIN_REP_SKU_SALES_LITERAL, SKU_SALES_LITERAL, root, criteriaUpdate, e));
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    // productId + salesAttributes can lock a sku entity
    @Override
    protected Predicate getWhereClause(Root<ProductSku> root, List<String> parentIds, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : parentIds) {
            Predicate parentIdClause = cb.equal(root.get(SKU_PRODUCT_ID_LITERAL), Long.parseLong(str));
            Predicate saleAttrClause = cb.equal(root.get(SKU_ATTR_SALES_LITERAL).as(String.class), parseAttrSales(command));
            Predicate combined = cb.and(parentIdClause, saleAttrClause);
            if (storagePatchOpSub(command)) {
                //make sure if storage change, value is not negative
                Predicate negativeClause = getStorageMustNotNegativeClause(cb, root, command);
                Predicate and = cb.and(combined, negativeClause);
                results.add(and);
            } else {
                results.add(combined);
            }
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    /**
     * @param command [{"op":"add","path":"/837195323695104/skus?query=attributesSales:835604723556352-淡粉色,835604663263232-185~/100A~/XXL/storageActual","value":"1"}]
     * @return 835604723556352:淡粉色,835604663263232:185/100A/XXL
     */
    private String parseAttrSales(PatchCommand command) {
        String replace = command.getPath().replace("/" + ADMIN_REP_SKU_LITERAL + "?" + HTTP_PARAM_QUERY + "=" + ADMIN_REP_ATTR_SALES_LITERAL + ":", "");
        String replace1 = replace.replace("~/", "$");
        String[] split = replace1.split("/");
        if (split.length != 2)
            throw new NoUpdatableFieldException();
        String $ = split[0].replace("-", ":").replace("$", "/");
        return Arrays.stream($.split(",")).sorted((a, b) -> {
            long l = Long.parseLong(a.split(":")[0]);
            long l1 = Long.parseLong(b.split(":")[0]);
            return Long.compare(l, l1);
        }).collect(Collectors.joining(","));
    }

    private Boolean setUpdateStorageValueFor(String fieldPath, String filedLiteral, Root<ProductSku> root, CriteriaUpdate<ProductSku> criteriaUpdate, PatchCommand e) {
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

    private Predicate getStorageMustNotNegativeClause(CriteriaBuilder cb, Root<ProductSku> root, PatchCommand command) {
        String filedLiteral;
        if (command.getPath().contains(ADMIN_REP_SKU_STORAGE_ORDER_LITERAL)) {
            filedLiteral = SKU_STORAGE_ORDER_LITERAL;
        } else if (command.getPath().contains(ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL)) {
            filedLiteral = SKU_STORAGE_ACTUAL_LITERAL;
        } else {
            filedLiteral = SKU_SALES_LITERAL;
        }
        Expression<Integer> diff = cb.diff(root.get(filedLiteral), parseInteger(command.getValue()));
        return cb.greaterThanOrEqualTo(diff, 0);
    }


    private boolean storagePatchOpSub(PatchCommand command) {
        return command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF) && (command.getPath().contains(ADMIN_REP_SALES_LITERAL));
    }
}
