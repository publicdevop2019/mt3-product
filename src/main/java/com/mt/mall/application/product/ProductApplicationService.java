package com.mt.mall.application.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.CommonConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.PatchProductCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.product.ProductQuery;
import com.mt.mall.domain.model.product.event.ProductBatchDeleted;
import com.mt.mall.domain.model.product.event.ProductDeleted;
import com.mt.mall.domain.model.product.event.ProductPatchBatched;
import com.mt.mall.domain.model.sku.SkuId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.mt.mall.application.product.representation.ProductRepresentation.ADMIN_REP_SKU_LITERAL;

@Service
public class ProductApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateProductCommand command, String operationId) {
        ProductId productId = new ProductId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, productId,
                () -> DomainRegistry.getProductService().create(
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
        return DomainRegistry.getProductRepository().productsOfQuery(new ProductQuery(queryParam, pageParam, skipCount, false));
    }

    public Optional<Product> product(String id) {
        return DomainRegistry.getProductRepository().productOfId(new ProductId(id));
    }

    public SumPagedRep<Product> publicProducts(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getProductRepository().productsOfQuery(new ProductQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<Product> publicProduct(String id) {
        return DomainRegistry.getProductRepository().publicProductOfId(new ProductId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateProductCommand command, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, command, changeId, (change) -> {
            Optional<Product> optionalProduct = DomainRegistry.getProductRepository().productOfId(productId);
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
                DomainRegistry.getProductRepository().add(product);
            }
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeById(String id, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, null, changeId, (change) -> {
            Optional<Product> optionalProduct = DomainRegistry.getProductRepository().productOfId(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                DomainRegistry.getProductRepository().remove(product);
                Set<SkuId> collect = product.getAttrSalesMap().values().stream().map(SkuId::new).collect(Collectors.toSet());
                DomainEventPublisher.instance().publish(new ProductDeleted(productId, collect, UUID.randomUUID().toString()));
            }
            change.setQuery(productId);
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Product> products = QueryUtility.getAllByQuery((query) -> DomainRegistry.getProductRepository().productsOfQuery((ProductQuery) query), new ProductQuery(queryParam));
            DomainRegistry.getProductRepository().remove(products);
            change.setRequestBody(products);
            change.setDeletedIds(products.stream().map(e -> e.getProductId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            Set<SkuId> collect1 = products.stream().map(e -> e.getAttrSalesMap().values()).flatMap(Collection::stream).map(SkuId::new).collect(Collectors.toSet());
            DomainEventPublisher.instance().publish(
                    new ProductBatchDeleted(
                            products.stream().map(Product::getProductId).collect(Collectors.toSet()),
                            collect1,
                            UUID.randomUUID().toString()));
            return products.stream().map(Product::getProductId).collect(Collectors.toSet());
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(productId, command, changeId, (change) -> {
            Optional<Product> optionalCatalog = DomainRegistry.getProductRepository().productOfId(productId);
            if (optionalCatalog.isPresent()) {
                Product product = optionalCatalog.get();
                PatchProductCommand beforePatch = new PatchProductCommand(product);
                PatchProductCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchProductCommand.class);
                product.replace(
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
            if (!productChange.isEmpty())
                DomainRegistry.getProductRepository().patchBatch(productChange);
            if (!skuChange.isEmpty()) {
                DomainEventPublisher.instance().publish(new ProductPatchBatched(Product.convertToSkuCommands(skuChange), changeId));
            }
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
