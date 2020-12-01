package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.representation.PublicProductCardRep;
import com.hw.aggregate.product.representation.PublicProductRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.tag.AppBizTagApplicationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublicProductApplicationService extends RoleBasedRestfulService<Product, PublicProductCardRep, PublicProductRep, VoidTypedClass> {
    {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
    }

    @Autowired
    private AppBizTagApplicationService appBizAttributeApplicationService;

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @Override
    public PublicProductCardRep getEntitySumRepresentation(Product product) {
        return new PublicProductCardRep(product);
    }

    @Override
    public PublicProductRep getEntityRepresentation(Product product) {
        return new PublicProductRep(product, appBizAttributeApplicationService, appBizSkuApplicationService);
    }

}

