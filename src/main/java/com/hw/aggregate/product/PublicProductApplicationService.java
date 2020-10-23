package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.representation.PublicProductCardRep;
import com.hw.aggregate.product.representation.PublicProductRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.tag.AppBizTagApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class PublicProductApplicationService extends DefaultRoleBasedRestfulService<Product, PublicProductCardRep, PublicProductRep, VoidTypedClass> {


    public static final String STRING_DEL = ":";
    @Autowired
    private AppBizTagApplicationService appBizAttributeApplicationService;

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @PostConstruct
    private void setUp() {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        return null;
    }

    @Override
    public PublicProductCardRep getEntitySumRepresentation(Product product) {
        return new PublicProductCardRep(product);
    }

    @Override
    public PublicProductRep getEntityRepresentation(Product product) {
        return new PublicProductRep(product, appBizAttributeApplicationService, appBizSkuApplicationService);
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(Product product) {

    }

    @Override
    public void postDelete(Product product) {

    }

    @Override
    protected void prePatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}

