package com.mt.mall.domain.model.product;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.product.ProductQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    SumPagedRep<Product> productsOfQuery(ProductQuery queryParam, DefaultPaging queryPagingParam);

    void add(Product product);

    ProductId nextIdentity();

    SumPagedRep<Product> productsOfQuery(ProductQuery productQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Product> productOfId(ProductId productId);

    void remove(Product product);

    void remove(Set<Product> products);

    void patchBatch(List<PatchCommand> commandList);
}
