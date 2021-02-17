package com.mt.mall.application.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.PatchProductCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductId;
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
        return DomainRegistry.productRepository().productsOfQuery(new ProductQuery(queryParam), new DefaultPaging(pageParam), new QueryConfig(skipCount));
    }

    public Optional<Product> product(String id) {
        return DomainRegistry.productRepository().productOfId(new ProductId(id));
    }

    public SumPagedRep<Product> publicProducts(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.productRepository().publicProductsOfQuery(new ProductQuery(queryParam), new DefaultPaging(pageParam), new QueryConfig(skipCount));
    }

    public Optional<Product> publicProduct(String id) {
        return DomainRegistry.productRepository().publicProductOfId(new ProductId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateProductCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            ProductId ProductId = new ProductId(id);
            Optional<Product> optionalProduct = DomainRegistry.productRepository().productOfId(ProductId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
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
        ApplicationServiceRegistry.idempotentWrapper().idempotent(id, changeId, (change) -> {
            ProductId ProductId = new ProductId(id);
            Optional<Product> optionalProduct = DomainRegistry.productRepository().productOfId(ProductId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                DomainRegistry.productRepository().remove(product);
                String skuQuery = SKU_REFERENCE_ID_LITERAL + ":" + id;
                ApplicationServiceRegistry.skuApplicationService().removeByQuery(skuQuery, changeId);
            }
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(null, changeId, (change) -> {
            Set<Product> products = DomainRegistry.productService().getProductsOfQuery(new ProductQuery(queryParam));
            DomainRegistry.productRepository().remove(products);
            Set<Product> productsOfQuery = DomainRegistry.productService().getProductsOfQuery(new ProductQuery(queryParam));
            Set<ProductId> collect = productsOfQuery.stream().map(Product::getProductId).collect(Collectors.toSet());
            String join = SKU_REFERENCE_ID_LITERAL + ":" + collect.stream().map(Object::toString).collect(Collectors.joining(","));
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
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            ProductId ProductId = new ProductId(id);
            Optional<Product> optionalCatalog = DomainRegistry.productRepository().productOfId(ProductId);
            if (optionalCatalog.isPresent()) {
                Product filter = optionalCatalog.get();
                PatchProductCommand beforePatch = new PatchProductCommand(filter);
                PatchProductCommand afterPatch = CommonDomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, PatchProductCommand.class);
                filter.replace(
                        afterPatch.getName(),
                        afterPatch.getStartAt(),
                        afterPatch.getEndAt()
                );
            }
        }, Product.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(commands, changeId, (ignored) -> {
            List<PatchCommand> skuChange = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
            List<PatchCommand> productChange = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
            if (!skuChange.isEmpty())
                ApplicationServiceRegistry.skuApplicationService().patchBatch(Product.convertToSkuCommands(skuChange), changeId);
            if (!productChange.isEmpty())
                DomainRegistry.productRepository().patchBatch(productChange);
        }, Product.class);
    }

    public void rollback(String id) {

    }
}
