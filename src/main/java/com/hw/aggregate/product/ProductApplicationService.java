package com.hw.aggregate.product;

import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.AdminQueryBuilder;
import com.hw.aggregate.product.model.CustomerQueryBuilder;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductStatus;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
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
    private EntityManager entityManager;

    @Autowired
    private CustomerQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminQueryBuilder adminQueryBuilder;

    @Transactional(readOnly = true)
    public ProductAdminGetAllPaginatedSummaryRepresentation queryForAdmin(String search, String page, String countFlag) {
        PageRequest pageRequest = adminQueryBuilder.getPageRequest(page);
        Predicate queryClause = adminQueryBuilder.getQueryClause(search);
        List<ProductDetail> query = repo.query(entityManager, queryClause, pageRequest);
        Long aLong = null;
        if ("0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, queryClause);
        }
        return new ProductAdminGetAllPaginatedSummaryRepresentation(query, aLong);
    }

    @Transactional(readOnly = true)
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation queryForCustomer(String search, String page, String countFlag) {
        PageRequest pageRequest = customerQueryBuilder.getPageRequest(page);
        Predicate queryClause = customerQueryBuilder.getQueryClause(search);
        List<ProductDetail> query = repo.query(entityManager, queryClause, pageRequest);
        Long aLong = null;
        if ("0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, queryClause);
        }
        return new ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(query, aLong);
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param commands
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRepresentation validateProduct(List<ProductValidationCommand> commands) {
        return new ProductValidationResultRepresentation(ProductDetail.validate(commands, repo));
    }


    @Transactional(readOnly = true)
    public ProductDetailCustomRepresentation getProductByIdForCustomer(Long productDetailId) {
        return new ProductDetailCustomRepresentation(ProductDetail.readCustomer(productDetailId, repo), attributeApplicationService.adminQuery(null, null, null));
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRepresentation getProductByIdForAdmin(Long id) {
        return new ProductDetailAdminRepresentation(ProductDetail.readAdmin(id, repo));
    }

    @Transactional
    public ProductCreatedRepresentation createProduct(CreateProductAdminCommand command) {
        return new ProductCreatedRepresentation(ProductDetail.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void updateProduct(Long id, UpdateProductAdminCommand command) {
        ProductDetail.readAdmin(id, repo).update(command, this, repo);
    }

    @Transactional
    public void delete(Long id) {
        ProductDetail.delete(id, repo);
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
    public void increaseActualStorageForMappedProducts(IncreaseActualStorageCommand command) {
        productServiceLambda.increaseActualStorageForMappedProducts.accept(command);
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

    @Transactional
    public void updateProductStatus(Long id, ProductStatus status) {
        ProductDetail read = ProductDetail.readAdmin(id, repo);
        read.updateStatus(status, repo);
    }
}

