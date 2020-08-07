package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import com.hw.shared.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductApplicationService {

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
    private ProductDetailManager productDetailManager;
    @Autowired
    private ProductSkuManager productSkuManager;

    @Autowired
    private ObjectMapper om;

    @Transactional(readOnly = true)
    public ProductAdminSumPagedRep readForAdminByQuery(String query, String page, String countFlag) {
        SumPagedRep<ProductDetail> pagedRep = productDetailManager.readByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, page, countFlag, ProductDetail.class);
        return new ProductAdminSumPagedRep(pagedRep);
    }

    @Transactional(readOnly = true)
    public ProductCustomerSumPagedRep readForCustomerByQuery(String query, String page, String countFlag) {
        SumPagedRep<ProductDetail> pagedRep = productDetailManager.readByQuery(RestfulEntityManager.RoleEnum.CUSTOMER, query, page, countFlag, ProductDetail.class);
        return new ProductCustomerSumPagedRep(pagedRep);
    }

    @Transactional(readOnly = true)
    public ProductDetailCustomRep readForCustomerById(Long id) {
        SumPagedRep<ProductDetail> productDetailSumPagedRep = productDetailManager.readById(RestfulEntityManager.RoleEnum.CUSTOMER, id.toString(), ProductDetail.class);
        if (productDetailSumPagedRep.getData().size() == 0)
            throw new ProductNotFoundException();
        return new ProductDetailCustomRep(productDetailSumPagedRep.getData().get(0), attributeApplicationService);
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRep readForAdminById(Long id) {
        ProductDetail productDetail = getProductDetail(id);
        return new ProductDetailAdminRep(productDetail);
    }

    @Transactional
    public ProductCreatedRep createForAdmin(CreateProductAdminCommand command) {
        return new ProductCreatedRep(ProductDetail.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void replaceForAdminById(Long id, UpdateProductAdminCommand command) {
        ProductDetail productDetail = getProductDetail(id);
        productDetail.replace(command, this, repo);
    }

    @Transactional
    public Integer deleteForAdminById(Long id) {
        productSkuManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN,id.toString(),ProductSku.class);
        return productDetailManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductDetail.class);
    }

    @Transactional
    public ProductDetailAdminRep patchForAdminById(Long id, JsonPatch patch) {
        ProductDetail productDetail = getProductDetail(id);
        return new ProductDetailAdminRep(ProductDetailPatchMiddleLayer.doPatch(patch, productDetail, om, repo));
    }

    @Transactional
    public ProductAdminSumPagedRep patchForAdmin(List<PatchCommand> patch) {
        Integer update = productDetailManager.update(RestfulEntityManager.RoleEnum.ADMIN, patch, ProductDetail.class);
        return new ProductAdminSumPagedRep(update.longValue());
    }

    @Transactional
    public Integer deleteForAdminByQuery(String query) {
        //delete sku first
        productSkuManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN,query,ProductSku.class);
        return productDetailManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, ProductDetail.class);
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param commands
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRep validate(List<ProductValidationCommand> commands) {
        return new ProductValidationResultRep(ProductDetail.validate(commands, repo));
    }

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

    @Transactional
    public void rollbackTx(String txId) {
        log.info("start of rollback transaction {}", txId);
        productServiceLambda.rollbackTx.accept(txId);
    }


    private ProductDetail getProductDetail(Long id) {
        SumPagedRep<ProductDetail> productDetailSumPagedRep = productDetailManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductDetail.class);
        if (productDetailSumPagedRep.getData().size() == 0)
            throw new ProductNotFoundException();
        return productDetailSumPagedRep.getData().get(0);
    }

}

