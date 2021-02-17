package com.mt.mall.domain.service;

import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.product.ProductQuery;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductOption;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    public ProductId create(ProductId productId,
                            String name,
                            String imageUrlSmall,
                            Set<String> imageUrlLarge,
                            String description,
                            Long startAt,
                            Long endAt,
                            List<ProductOption> selectedOptions,
                            Set<String> attributesKey,
                            Set<String> attributesProd,
                            Set<String> attributesGen,
                            List<CreateProductCommand.CreateProductSkuAdminCommand> skus,
                            List<CreateProductCommand.CreateProductAttrImageAdminCommand> attributeSaleImages
                            ) {
        Product product = new Product(productId, name, imageUrlSmall, imageUrlLarge,
                description, startAt, endAt, selectedOptions, attributesKey, attributesProd, attributesGen, skus, attributeSaleImages);
        DomainRegistry.productRepository().add(product);
        return product.getProductId();
    }
}
