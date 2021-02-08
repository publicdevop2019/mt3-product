package com.mt.mall.domain.model.product;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.product.ProductQuery;
import com.mt.mall.domain.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    SumPagedRep<Product> productsOfQuery(ProductQuery queryParam, DefaultPaging queryPagingParam);

    void add(Product product);

    ProductId nextIdentity();

    SumPagedRep<Product> productsOfQuery(ProductQuery productQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Product> productOfId(ProductId productId);

    void remove(Product product);

    void remove(Set<Product> products);
}
