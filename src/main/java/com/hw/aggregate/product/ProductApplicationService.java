package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.DefaultSumPagedRep;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
public class ProductApplicationService extends DefaultApplicationService {

    @Autowired
    private ProductDetailRepo repo;

    @Autowired
    private ProductServiceLambda productServiceLambda;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @Autowired
    private BizAttributeApplicationService attributeApplicationService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerSelectQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminSelectQueryBuilder adminQueryBuilder;

    @Autowired
    private AdminUpdateQueryBuilder adminUpdateQueryBuilder;

    @Autowired
    private AdminProductDetailDeleteQueryBuilder adminDeleteQueryBuilder;

    @Autowired
    private AdminSkuDeleteQueryBuilder adminSkuDeleteQueryBuilder;

    @Autowired
    private ObjectMapper om;

    @Transactional(readOnly = true)
    public ProductAdminSumPagedRep queryForAdmin(String search, String page, String countFlag) {
        DefaultSumPagedRep<ProductDetail> select = select(adminQueryBuilder, search, page, countFlag, ProductDetail.class);
        return new ProductAdminSumPagedRep(select);
    }

    @Transactional(readOnly = true)
    public ProductCustomerSumPagedRep queryForCustomer(String search, String page, String countFlag) {
        DefaultSumPagedRep<ProductDetail> select = select(customerQueryBuilder, search, page, countFlag, ProductDetail.class);
        return new ProductCustomerSumPagedRep(select);
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param commands
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRep validateProduct(List<ProductValidationCommand> commands) {
        return new ProductValidationResultRep(ProductDetail.validate(commands, repo));
    }


    @Transactional(readOnly = true)
    public ProductDetailCustomRep getProductByIdForCustomer(Long productDetailId) {
        return new ProductDetailCustomRep(ProductDetail.readCustomer(productDetailId, repo), attributeApplicationService.adminQuery(null, null, "0"));
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRep getProductByIdForAdmin(Long id) {
        return new ProductDetailAdminRep(ProductDetail.readAdmin(id, repo));
    }

    @Transactional
    public ProductCreatedRep createProduct(CreateProductAdminCommand command) {
        return new ProductCreatedRep(ProductDetail.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void update(Long id, UpdateProductAdminCommand command) {
        ProductDetail.readAdmin(id, repo).update(command, this, repo);
    }

    @Transactional
    public void delete(Long id) {
        ProductDetail.delete(id, repo);
    }

    @Transactional
    public void rollbackTx(String txId) {
        log.info("start of rollback transaction {}", txId);
        productServiceLambda.rollbackTx.accept(txId);
    }

    @Transactional
    public ProductDetailAdminRep patch(Long id, JsonPatch patch) {
        ProductDetail original = ProductDetail.readAdmin(id, repo);
        return new ProductDetailAdminRep(ProductDetailPatchMiddleLayer.doPatch(patch, original, om, repo));
    }

    @Transactional
    public ProductAdminSumPagedRep update(String search, List<JsonPatchOperationLike> patch) {
        return new ProductAdminSumPagedRep(adminUpdateQueryBuilder.update(search, patch, ProductDetail.class).longValue());
    }

    @Transactional
    public Integer delete(String search) {
        //delete sku first
        adminSkuDeleteQueryBuilder.delete(search, ProductSku.class);
        return adminDeleteQueryBuilder.delete(search, ProductDetail.class);
    }

//    private ProductSumPagedRep select(SelectQueryBuilder<ProductDetail> queryBuilder, String search, String page, String countFlag) {
//        List<ProductDetail> query = queryBuilder.select(search, page, ProductDetail.class);
//        Long aLong = null;
//        if (!"0".equals(countFlag)) {
//            aLong = queryBuilder.selectCount(search, ProductDetail.class);
//        }
//        return new ProductSumPagedRep(query, aLong);
//    }

    @Transactional
    public void decreaseActualStorageForMappedProducts(DecreaseActualStorageCommand command) {
        productServiceLambda.decreaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void decreaseActualStorageForMappedProductsAdmin(DecreaseActualStorageCommand command) {
        productServiceLambda.adminDecreaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void decreaseOrderStorageForMappedProducts(DecreaseOrderStorageCommand command) {
        productServiceLambda.decreaseOrderStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void increaseOrderStorageForMappedProducts(IncreaseOrderStorageCommand command) {
        productServiceLambda.increaseOrderStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void increaseActualStorageForMappedProductsAdmin(IncreaseActualStorageCommand command) {
        productServiceLambda.adminIncreaseActualStorageForMappedProducts.accept(command);
    }

}

