package com.mt.mall.domain.model.product;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    SumPagedRep<Product> productsOfQuery(ProductQuery queryParam, PageConfig queryPagingParam);

    void add(Product product);

    ProductId nextIdentity();

    SumPagedRep<Product> productsOfQuery(ProductQuery productQuery, PageConfig defaultPaging, QueryConfig queryConfig);

    Optional<Product> productOfId(ProductId productId);

    Optional<Product> publicProductOfId(ProductId productId);

    void remove(Product product);

    void remove(Set<Product> products);

    void patchBatch(List<PatchCommand> commandList);
}
