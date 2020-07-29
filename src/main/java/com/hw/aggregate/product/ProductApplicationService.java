package com.hw.aggregate.product;

import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.AdminQueryConfig;
import com.hw.aggregate.product.model.CustomerQueryConfig;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductStatus;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
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
    private CustomerQueryConfig customerQueryConfig;

    @Transactional(readOnly = true)
    public ProductAdminGetAllPaginatedSummaryRepresentation getAllForAdmin(Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        Page<ProductDetail> all = repo.findAll(customerQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder));
        return new ProductAdminGetAllPaginatedSummaryRepresentation(all.getContent(), all.getTotalPages(), all.getTotalElements());
    }


    @Transactional(readOnly = true)
    public ProductCustomerSearchByNameSummaryPaginatedRepresentation searchProductByNameForCustomer(String key, Integer pageNumber, Integer pageSize, CustomerQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        Page<ProductDetail> pd = repo.searchProductByNameForCustomer(key, Instant.now().toEpochMilli(), customerQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder));
        return new ProductCustomerSearchByNameSummaryPaginatedRepresentation(pd.getContent(), pd.getTotalPages(), pd.getTotalElements());
    }


    @Transactional(readOnly = true)
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForCustomer(String attributes, Integer pageNumber, Integer pageSize, CustomerQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest of = customerQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        return new ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(
                repo.searchByAttributesDynamic(entityManager, attributes, true, of), null, null);
    }

    @Transactional(readOnly = true)
    public ProductAdminSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForAdmin(String tags, Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest of = customerQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        return new ProductAdminSearchByAttributesSummaryPaginatedRepresentation(repo.searchByAttributesDynamic(entityManager, tags, false, of), null, null);
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
        return new ProductDetailCustomRepresentation(ProductDetail.readCustomer(productDetailId, repo), attributeApplicationService.getAllAttributes(null, null, null, null));
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRepresentation getProductByIdForAdmin(Long productDetailId) {
        return new ProductDetailAdminRepresentation(ProductDetail.readAdmin(productDetailId, repo));
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

