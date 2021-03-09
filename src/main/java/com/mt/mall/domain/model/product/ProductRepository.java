package com.mt.mall.domain.model.product;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    SumPagedRep<Product> productsOfQuery(ProductQuery queryParam);

    void add(Product product);

    Optional<Product> productOfId(ProductId productId);

    Optional<Product> publicProductOfId(ProductId productId);

    void remove(Product product);

    void remove(Set<Product> products);

    void patchBatch(List<PatchCommand> commandList);
}
