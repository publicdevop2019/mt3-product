package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductQuery;
import com.mt.mall.domain.model.product.ProductRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        ProductSelectQueryBuilder publicProductSelectQueryBuilder = QueryBuilderRegistry.productSelectQueryBuilder();
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
        return getSumPagedRep(query, clientPaging, queryConfig);
    }

    default SumPagedRep<Product> productsOfQuery(ProductQuery query, PageConfig clientPaging) {
        return getSumPagedRep(query, clientPaging, new QueryConfig());
    }

    private SumPagedRep<Product> getSumPagedRep(ProductQuery query, PageConfig page, QueryConfig config) {
        SelectQueryBuilder<Product> selectQueryBuilder = QueryBuilderRegistry.productSelectQueryBuilder();
        List<Product> select = selectQueryBuilder.select(query, page, Product.class);
        Long aLong = null;
        if (!config.isSkipCount()) {
            aLong = selectQueryBuilder.count(query, Product.class);
        }
        return new SumPagedRep<>(select, aLong);
    }
}
