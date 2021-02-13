package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.application.product.ProductQuery;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Product> findByProductIdAndDeletedFalse(ProductId productId);

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
        return findByProductIdAndDeletedFalse(productId);
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

    default SumPagedRep<Product> productsOfQuery(ProductQuery clientQuery, DefaultPaging clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), queryConfig.value());
    }

    default SumPagedRep<Product> productsOfQuery(ProductQuery clientQuery, DefaultPaging clientPaging) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), null);
    }

    private SumPagedRep<Product> getSumPagedRep(String query, String page, String config) {
        SelectQueryBuilder<Product> selectQueryBuilder = QueryBuilderRegistry.productSelectQueryBuilder();
        List<Product> select = selectQueryBuilder.select(query, page, Product.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, Product.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
