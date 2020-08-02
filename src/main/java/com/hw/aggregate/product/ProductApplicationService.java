package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
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
    private CustomerSelectQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminSelectQueryBuilder adminQueryBuilder;

    @Autowired
    private AdminUpdateQueryBuilder adminUpdateQueryBuilder;

    @Autowired
    private AdminDeleteQueryBuilder adminDeleteQueryBuilder;

    @Autowired
    private ObjectMapper om;

    @Transactional(readOnly = true)
    public ProductAdminGetAllPaginatedSummaryRepresentation queryForAdmin(String search, String page, String countFlag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query0 = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> root = query0.from(ProductDetail.class);
        PageRequest pageRequest = adminQueryBuilder.getPageRequest(page);
        Predicate queryClause = adminQueryBuilder.getQueryClause(cb, root, search);
        List<ProductDetail> query = repo.query(entityManager, cb, query0, root, queryClause, pageRequest);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, cb, queryClause);
        }
        return new ProductAdminGetAllPaginatedSummaryRepresentation(query, aLong);
    }

    @Transactional(readOnly = true)
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation queryForCustomer(String search, String page, String countFlag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query0 = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> root = query0.from(ProductDetail.class);
        PageRequest pageRequest = customerQueryBuilder.getPageRequest(page);
        Predicate queryClause = customerQueryBuilder.getQueryClause(cb, root, search);
        List<ProductDetail> query = repo.query(entityManager, cb, query0, root, queryClause, pageRequest);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, cb, queryClause);
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
    public ProductDetailAdminRepresentation patchProduct(Long id, JsonPatch patch) {
        ProductDetail original = ProductDetail.readAdmin(id, repo);
        return new ProductDetailAdminRepresentation(ProductDetailPatchMiddleLayer.doPatch(patch, original, om, repo));
    }

    @Transactional
    public Integer batchUpdateProducts(String search, List<JsonPatchOperationLike> patch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<ProductDetail> criteriaUpdate = cb.createCriteriaUpdate(ProductDetail.class);
        Root<ProductDetail> root = criteriaUpdate.from(ProductDetail.class);
        Predicate whereClause = adminUpdateQueryBuilder.getWhereClause(cb, root, search);
        return repo.update(entityManager, criteriaUpdate, whereClause, patch, adminUpdateQueryBuilder);
    }

    @Transactional
    public Integer delete(String search) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        //remove sku constrain first
        CriteriaDelete<ProductSku> criteriaDeleteSku = cb.createCriteriaDelete(ProductSku.class);
        Root<ProductSku> rootSku = criteriaDeleteSku.from(ProductSku.class);
        Predicate skuWhereClause = adminDeleteQueryBuilder.getSkuWhereClause(cb, rootSku, search);
        Integer deleteSku = repo.delete(entityManager, criteriaDeleteSku, skuWhereClause, adminDeleteQueryBuilder);

        CriteriaDelete<ProductDetail> criteriaDelete = cb.createCriteriaDelete(ProductDetail.class);
        Root<ProductDetail> root = criteriaDelete.from(ProductDetail.class);
        Predicate whereClause = adminDeleteQueryBuilder.getWhereClause(cb, root, search);
        Integer delete = repo.delete(entityManager, criteriaDelete, whereClause, adminDeleteQueryBuilder);
        return delete;
    }
}

