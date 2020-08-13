package com.hw.aggregate.product;

import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductManager;
import com.hw.aggregate.product.representation.PublicProductCardRep;
import com.hw.aggregate.product.representation.PublicProductRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulEntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PublicProductApplicationService extends DefaultRoleBasedRestfulService<Product, PublicProductCardRep, PublicProductRep> {

    @Autowired
    private ProductRepo repo2;

    @Autowired
    private AppBizAttributeApplicationService appBizAttributeApplicationService;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private ProductManager productDetailManager;


    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = productDetailManager;
        entityClass = Product.class;
        role = RestfulEntityManager.RoleEnum.PUBLIC;
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
        return new PublicProductRep(product, appBizAttributeApplicationService);
    }

    @Override
    protected <S extends CreatedRep> S getCreatedEntityRepresentation(Product created) {
        return null;
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return null;
    }
}

