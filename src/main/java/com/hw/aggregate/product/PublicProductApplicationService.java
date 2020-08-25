package com.hw.aggregate.product;

import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductQueryRegistry;
import com.hw.aggregate.product.representation.PublicProductCardRep;
import com.hw.aggregate.product.representation.PublicProductRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PublicProductApplicationService extends DefaultRoleBasedRestfulService<Product, PublicProductCardRep, PublicProductRep, VoidTypedClass> {

    @Autowired
    private ProductRepo repo2;

    @Autowired
    private AppBizAttributeApplicationService appBizAttributeApplicationService;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private ProductQueryRegistry productDetailManager;
    @Autowired
    private ChangeRepository changeHistoryRepository;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = productDetailManager;
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
        changeRepository = changeHistoryRepository;
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
    protected Product createEntity(long id, Object command) {
        return null;
    }
}

