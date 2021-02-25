package com.mt.mall.application.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.CommonConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.PatchProductCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.mall.application.product.representation.ProductRepresentation.ADMIN_REP_SKU_LITERAL;
import static com.mt.mall.domain.model.sku.Sku.SKU_REFERENCE_ID_LITERAL;

@Service
public class ProductApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateProductCommand command, String operationId) {
        ProductId productId = DomainRegistry.productRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, productId,
                () -> DomainRegistry.productService().create(
                        productId,
                        command.getName(),
                        command.getImageUrlSmall(),
                        command.getImageUrlLarge(),
                        command.getDescription(),
                        command.getStartAt(),
                        command.getEndAt(),
                        command.getSelectedOptions(),
                        command.getAttributesKey(),
                        command.getAttributesProd(),
                        command.getAttributesGen(),
                        command.getSkus(),
                        command.getAttributeSaleImages()
                ), Product.class
        );
    }

    public SumPagedRep<Product> products(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.productRepository().productsOfQuery(new ProductQuery(queryParam, false), new PageConfig(pageParam, 400), new QueryConfig(skipCount));
    }

    public Optional<Product> product(String id) {
        return DomainRegistry.productRepository().productOfId(new ProductId(id));
    }

    public SumPagedRep<Product> publicProducts(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.productRepository().productsOfQuery(new ProductQuery(queryParam, true), new PageConfig(pageParam, 200), new QueryConfig(skipCount));
    }

    public Optional<Product> publicProduct(String id) {
        return DomainRegistry.productRepository().publicProductOfId(new ProductId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateProductCommand command, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, command, changeId, (change) -> {
            Optional<Product> optionalProduct = DomainRegistry.productRepository().productOfId(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.checkVersion(command.getVersion());
                product.replace(
                        command.getName(),
                        command.getImageUrlSmall(),
                        command.getImageUrlLarge(),
                        command.getDescription(),
                        command.getStartAt(),
                        command.getEndAt(),
                        command.getSelectedOptions(),
                        command.getAttributesKey(),
                        command.getAttributesProd(),
                        command.getAttributesGen(),
                        command.getSkus(),
                        command.getAttributeSaleImages(),
                        command.getChangeId()
                );
                DomainRegistry.productRepository().add(product);
            }
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeById(String id, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, null, changeId, (change) -> {
            Optional<Product> optionalProduct = DomainRegistry.productRepository().productOfId(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                DomainRegistry.productRepository().remove(product);
                String skuQuery = SKU_REFERENCE_ID_LITERAL + CommonConstant.QUERY_DELIMITER + id;
                ApplicationServiceRegistry.skuApplicationService().removeByQuery(skuQuery, changeId);
            }
            change.setQuery(productId);
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Product> products = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.productRepository().productsOfQuery(query, page), new ProductQuery(queryParam, false));
            DomainRegistry.productRepository().remove(products);
            Set<ProductId> collect = products.stream().map(Product::getProductId).collect(Collectors.toSet());
            String join = SKU_REFERENCE_ID_LITERAL + CommonConstant.QUERY_DELIMITER + collect.stream().map(DomainId::getDomainId).collect(Collectors.joining(CommonConstant.QUERY_OR_DELIMITER));
            ApplicationServiceRegistry.skuApplicationService().removeByQuery(join, changeId);
            change.setRequestBody(products);
            change.setDeletedIds(products.stream().map(e -> e.getProductId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return products.stream().map(Product::getProductId).collect(Collectors.toSet());
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, command, changeId, (change) -> {
            Optional<Product> optionalCatalog = DomainRegistry.productRepository().productOfId(productId);
            if (optionalCatalog.isPresent()) {
                Product filter = optionalCatalog.get();
                PatchProductCommand beforePatch = new PatchProductCommand(filter);
                PatchProductCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchProductCommand.class);
                filter.replace(
                        afterPatch.getName(),
                        afterPatch.getStartAt(),
                        afterPatch.getEndAt()
                );
                change.setQuery(productId);
            }
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, commands, changeId, (ignored) -> {
            List<PatchCommand> skuChange = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
            List<PatchCommand> productChange = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
            if (!skuChange.isEmpty())
                ApplicationServiceRegistry.skuApplicationService().patchBatch(Product.convertToSkuCommands(skuChange), changeId);
            if (!productChange.isEmpty())
                DomainRegistry.productRepository().patchBatch(productChange);
        }, Product.class);
    }


    @SubscribeForEvent
    @Transactional
    public void rollback(String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotentRollback(changeId, (change) -> {
            List<PatchCommand> command = (List<PatchCommand>) CommonDomainRegistry.getCustomObjectSerializer().nativeDeserialize(change.getRequestBody());
            List<PatchCommand> patchCommands = PatchCommand.buildRollbackCommand(command);
            patchBatch(patchCommands, changeId + CommonConstant.CHANGE_REVOKED);
        }, Product.class);
    }
}
