package com.mt.mall.domain.model.product;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.exception.AggregateNotExistException;
import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.ProductOptionCommand;
import com.mt.mall.application.product.command.ProductSalesImageCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.application.product.exception.NoLowestPriceFoundException;
import com.mt.mall.application.product.exception.SkuAlreadyExistException;
import com.mt.mall.application.product.exception.SkuNotExistException;
import com.mt.mall.application.sku.command.CreateSkuCommand;
import com.mt.mall.application.sku.command.UpdateSkuCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.event.ProductCreated;
import com.mt.mall.domain.model.product.event.ProductUpdated;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.*;
import static com.mt.mall.application.product.representation.ProductRepresentation.ADMIN_REP_SKU_LITERAL;
import static com.mt.mall.application.product.representation.ProductRepresentation.ProductSkuAdminRepresentation.*;


@Getter
@Slf4j
@Entity
@Table(name = "product_")
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product extends Auditable {
    public transient static final String PRODUCT_NAME_LITERAL = "name";
    public transient static final String PRODUCT_END_AT_LITERAL = "endAt";
    public transient static final String PRODUCT_START_AT_LITERAL = "startAt";
    public transient static final String PRODUCT_SELECTED_OPTIONS_LITERAL = "selectedOptions";
    public transient static final String PRODUCT_IMAGE_URL_LARGE_LITERAL = "imageUrlLarge";
    public transient static final String PRODUCT_LOWEST_PRICE_LITERAL = "lowestPrice";
    public transient static final String PRODUCT_TOTAL_SALES_LITERAL = "totalSales";
    public transient static final String PRODUCT_PRODUCT_ID = "productId";
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    private String imageUrlSmall;
    private String name;
    private String description;
    private Long endAt;
    private Long startAt;

    @Column(length = 10000)
    @Convert(converter = ProductOption.ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;

    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "productId", unique = true, updatable = false, nullable = false))
    })
    private ProductId productId;
    @Column(length = 10000)
    @Setter(AccessLevel.PRIVATE)
    private HashMap<String, String> attrSalesMap;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "product_tag_map",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ProductTag> tags = new HashSet<>();

    private BigDecimal lowestPrice;

    @Column
    private Integer totalSales;

    private void setLowestPrice(BigDecimal lowestPrice) {
        Validator.greaterThanOrEqualTo(lowestPrice, BigDecimal.ZERO);
        lowestPrice = lowestPrice.setScale(2, RoundingMode.CEILING);
        if (this.lowestPrice == null) {
            this.lowestPrice = lowestPrice;
            return;
        }
        BigDecimal bigDecimal = this.lowestPrice.setScale(2, RoundingMode.CEILING);
        if (bigDecimal.compareTo(lowestPrice) != 0)
            this.lowestPrice = lowestPrice;
    }

    private void setTotalSales(Integer totalSales) {
        Validator.greaterThanOrEqualTo(totalSales, 0);
        this.totalSales = totalSales;
    }

    private void setEndAt(Long endAt) {
        this.endAt = endAt;
    }

    private void setSelectedOptions(List<ProductOptionCommand> selectedOptions) {
        if (selectedOptions != null)
            this.selectedOptions = selectedOptions.stream().map(ProductOption::new).collect(Collectors.toList());
    }

    private void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    private void setImageUrlSmall(String imageUrlSmall) {
        Validator.isHttpUrl(imageUrlSmall);
        this.imageUrlSmall = imageUrlSmall;
    }

    private void setDescription(String description) {
        Validator.lengthLessThanOrEqualTo(description, 50);
        Validator.whitelistOnly(description);
        this.description = description;
    }

    private void setName(String name) {
        Validator.notBlank(name);
        Validator.lengthLessThanOrEqualTo(name, 75);
        Validator.whitelistOnly(name);
        this.name = name;
    }

    private void setImageUrlLarge(@Nullable Set<String> imageUrlLarge) {
        if (imageUrlLarge != null)
            imageUrlLarge.forEach(Validator::isHttpUrl);
        if (imageUrlLarge == null && this.imageUrlLarge != null && this.imageUrlLarge.isEmpty())
            return;
        this.imageUrlLarge = imageUrlLarge;
    }

    private void setAttributeSaleImages(ArrayList<ProductAttrSaleImages> attributeSaleImages) {
        DomainRegistry.getProductValidationService().validate(attributeSaleImages, new HttpValidationNotificationHandler());
        this.attributeSaleImages = attributeSaleImages;
    }

    private void addTag(ProductTag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    private void removeTag(ProductTag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }

    public void replace(String name,
                        String imageUrlSmall,
                        Set<String> imageUrlLarge,
                        String description,
                        Long startAt,
                        Long endAt,
                        List<ProductOptionCommand> selectedOptions,
                        Set<String> attributesKey,
                        Set<String> attributesProd,
                        Set<String> attributesGen,
                        List<UpdateProductCommand.UpdateProductAdminSkuCommand> skus,
                        List<ProductSalesImageCommand> attributeSaleImages,
                        String changeId
    ) {
        setImageUrlSmall(imageUrlSmall);
        setName(name);
        setDescription(description);
        setSelectedOptions(selectedOptions);
        setImageUrlLarge(imageUrlLarge);
        setStartAt(startAt);
        setEndAt(endAt);
        Validator.notEmpty(skus);
        skus.forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            Validator.notNull(e.getAttributesSales());
        });
        adjustSku(skus, changeId);
        updateAttributeSaleImages(attributeSaleImages);
        setLowestPrice(findLowestPrice2(skus));
        Set<String> sales = skus.stream().map(UpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (!getTags().equals(getProductTags(sales, attributesKey, attributesGen, attributesProd))) {
            this.tags = new HashSet<>();
            sales.forEach(getStringConsumer(TagType.SALES));
            if (attributesProd != null)
                attributesProd.forEach(getStringConsumer(TagType.PROD));
            if (attributesKey != null) {
                attributesKey.forEach(getStringConsumer(TagType.KEY));
            }
            if (attributesGen != null)
                attributesGen.forEach(getStringConsumer(TagType.GEN));
        }
    }

    private Set<ProductTag> getProductTags(Set<String> sales, Set<String> attributesKey, Set<String> attributesGen, Set<String> attributesProd) {
        Set<ProductTag> productTags = new HashSet<>();
        if (sales != null) {
            sales.forEach(e -> {
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagValueAndType(e, TagType.SALES);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.SALES);
                    productTags.add(tag);
                }
            });
        }
        if (attributesKey != null) {
            attributesKey.forEach(e -> {
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagValueAndType(e, TagType.KEY);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.KEY);
                    productTags.add(tag);
                }
            });
        }
        if (attributesGen != null) {
            attributesGen.forEach(e -> {
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagValueAndType(e, TagType.GEN);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.GEN);
                    productTags.add(tag);
                }
            });
        }
        if (attributesProd != null) {
            attributesProd.forEach(e -> {
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagValueAndType(e, TagType.PROD);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.PROD);
                    productTags.add(tag);
                }
            });
        }
        return productTags;
    }

    private void updateAttributeSaleImages(List<ProductSalesImageCommand> attributeSaleImages) {
        if (attributeSaleImages != null) {
            ArrayList<ProductAttrSaleImages> collect = attributeSaleImages.stream().map(e ->
                    new ProductAttrSaleImages(e.getAttributeSales(), (LinkedHashSet<String>) e.getImageUrls())
            ).collect(Collectors.toCollection(ArrayList::new));
            setAttributeSaleImages(collect);
        }
    }

    private Consumer<String> getStringConsumer(TagType key) {
        return e -> {
            Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagValueAndType(e, key);
            if (byValue.isPresent()) {
                addTag(byValue.get());
            } else {
                ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, key);
                addTag(tag);
            }
        };
    }

    public HashMap<String, String> getAttrSalesMap() {
        if (attrSalesMap == null) {
            attrSalesMap = new HashMap<>();
        }
        return attrSalesMap;
    }

    private void adjustSku(List<UpdateProductCommand.UpdateProductAdminSkuCommand> commands, String changeId) {
        List<CreateSkuCommand> createSkuCommands = new ArrayList<>();
        List<UpdateSkuCommand> updateSkuCommands = new ArrayList<>();
        Set<SkuId> removeSkuCommands = new HashSet<>();
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                if (getAttrSalesMap().containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuAlreadyExistException();
                }

                CreateSkuCommand command1 = new CreateSkuCommand();
                SkuId skuId = new SkuId();
                command1.setSkuId(skuId);
                command1.setPrice(command.getPrice());
                command1.setReferenceId(productId);
                command1.setStorageOrder(command.getStorageOrder());
                command1.setStorageActual(command.getStorageActual());
                command1.setSales(command.getSales() == null ? 0 : command.getSales());
                createSkuCommands.add(command1);
                getAttrSalesMap().put(String.join(",", command.getAttributesSales()), skuId.getDomainId());
            } else {
                //existing sku
                if (!getAttrSalesMap().containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuNotExistException();
                }
                //update price
                String s = getAttrSalesMap().get(getAttrSalesKey(command.getAttributesSales()));
                Optional<Sku> sku = ApplicationServiceRegistry.getSkuApplicationService().sku(s);
                if (sku.isPresent() && sku.get().getPrice().compareTo(command.getPrice()) != 0) {
                    UpdateSkuCommand updateSkuCommand = new UpdateSkuCommand();
                    updateSkuCommand.setPrice(command.getPrice());
                    updateSkuCommand.setVersion(command.getVersion());
                    updateSkuCommand.setSkuId(sku.get().getSkuId());
                    //price will be update in a different changeId
                    updateSkuCommands.add(updateSkuCommand);

                }
                updateStorage(command, changeId);

            }
        });
        // find skus not in update command & remove
        List<String> collect = getAttrSalesMap().keySet().stream().filter(e -> commands.stream().noneMatch(command -> getAttrSalesKey(command.getAttributesSales()).equals(e))).collect(Collectors.toList());
        Set<String> collect1 = collect.stream().map(e -> getAttrSalesMap().get(e)).collect(Collectors.toSet());
        if (collect1.size() > 0) {
            removeSkuCommands.addAll(collect1.stream().map(SkuId::new).collect(Collectors.toSet()));
        }
        DomainEventPublisher.instance().publish(new ProductUpdated(productId, createSkuCommands, updateSkuCommands, removeSkuCommands, UUID.randomUUID().toString()));
    }

    private String getAttrSalesKey(Set<String> attributesSales) {
        return String.join(",", attributesSales);
    }

    private void updateStorage(UpdateProductCommand.UpdateProductAdminSkuCommand command, String changeId) {
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        if (command.getDecreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseOrderStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getDecreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseActualStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseOrderStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseActualStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (patchCommands.size() > 0)
            ApplicationServiceRegistry.getProductApplicationService().patchBatch(patchCommands, changeId);
    }

    private String toSkuQueryPath(UpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType) {
        String s = getAttrSalesMap().get(getAttrSalesKey(command.getAttributesSales()));
        return "/" + s + "/" + storageType;
    }

    public Product(ProductId productId,
                   String name,
                   String imageUrlSmall,
                   Set<String> imageUrlLarge,
                   String description,
                   Long startAt,
                   Long endAt,
                   List<ProductOptionCommand> selectedOptions,
                   Set<String> attributesKey,
                   Set<String> attributesProd,
                   Set<String> attributesGen,
                   List<CreateProductCommand.CreateProductSkuAdminCommand> skus,
                   List<ProductSalesImageCommand> attributeSaleImages
    ) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setImageUrlSmall(imageUrlSmall);
        setName(name);
        setProductId(productId);
        setDescription(description);
        setSelectedOptions(selectedOptions);
        setImageUrlLarge(imageUrlLarge);
        if (attributesProd != null)
            attributesProd.forEach(getStringConsumer(TagType.PROD));
        if (attributesKey != null)
            attributesKey.forEach(getStringConsumer(TagType.KEY));
        if (attributesGen != null)
            attributesGen.forEach(getStringConsumer(TagType.GEN));
        setStartAt(startAt);
        setEndAt(endAt);
        skus.forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(e.getAttributesSales());
        });
        skus.stream().map(CreateProductCommand.CreateProductSkuAdminCommand::getAttributesSales)
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .forEach(getStringConsumer(TagType.SALES));
        List<CreateSkuCommand> createSkuCommands = new ArrayList<>();
        for (CreateProductCommand.CreateProductSkuAdminCommand skuAdminCommand : skus) {
            CreateSkuCommand command1 = new CreateSkuCommand();
            command1.setPrice(skuAdminCommand.getPrice());
            command1.setReferenceId(productId);
            command1.setStorageOrder(skuAdminCommand.getStorageOrder());
            command1.setStorageActual(skuAdminCommand.getStorageActual());
            command1.setSales(skuAdminCommand.getSales());
            SkuId skuId = new SkuId();
            command1.setSkuId(skuId);
            createSkuCommands.add(command1);
            getAttrSalesMap().put(String.join(",", skuAdminCommand.getAttributesSales()), skuId.getDomainId());
        }
        setAttributeSaleImages(attributeSaleImages);
        setLowestPrice(findLowestPrice(skus));
        setTotalSales(calcTotalSales(skus));
        DomainEventPublisher.instance().publish(new ProductCreated(productId, createSkuCommands, UUID.randomUUID().toString()));
    }

    private void setAttributeSaleImages(List<ProductSalesImageCommand> attributeSaleImages) {
        if (attributeSaleImages != null) {
            ArrayList<ProductAttrSaleImages> collect = attributeSaleImages.stream().map(e -> new ProductAttrSaleImages(e.getAttributeSales(), (LinkedHashSet<String>) e.getImageUrls())
            ).collect(Collectors.toCollection(ArrayList::new));
            setAttributeSaleImages(collect);
        }
    }

    private Integer calcTotalSales(List<CreateProductCommand.CreateProductSkuAdminCommand> skus) {
        return skus.stream().map(CreateProductCommand.CreateProductSkuAdminCommand::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(List<CreateProductCommand.CreateProductSkuAdminCommand> skus) {
        CreateProductCommand.CreateProductSkuAdminCommand createProductSkuAdminCommand = skus.stream().min(Comparator.comparing(CreateProductCommand.CreateProductSkuAdminCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return createProductSkuAdminCommand.getPrice();
    }

    private BigDecimal findLowestPrice2(List<UpdateProductCommand.UpdateProductAdminSkuCommand> skus) {
        UpdateProductCommand.UpdateProductAdminSkuCommand updateProductAdminSkuCommand = skus.stream().min(Comparator.comparing(UpdateProductCommand.UpdateProductAdminSkuCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return updateProductAdminSkuCommand.getPrice();
    }

    public static List<PatchCommand> convertToSkuCommands(List<PatchCommand> hasNestedEntity) {
        Set<String> collect = hasNestedEntity.stream().map(e -> e.getPath().split("/")[1]).collect(Collectors.toSet());
        String join = "id:" + String.join(".", collect);
        SumPagedRep<Product> products = ApplicationServiceRegistry.getProductApplicationService().products(join, null, "sc:1");
        hasNestedEntity.forEach(e -> {
            String[] split = e.getPath().split("/");
            String id = split[1];
            String fieldName = split[split.length - 1];
            String attrSales = parseAttrSales(e);
            Optional<Product> first = products.getData().stream().filter(ee -> ee.getProductId().getDomainId().equals(id)).findFirst();
            if (first.isPresent()) {
                String domainId = first.get().getAttrSalesMap().get(attrSales);
                e.setPath("/" + domainId + "/" + fieldName);
            } else {
                throw new AggregateNotExistException();
            }
        });

        return hasNestedEntity;
    }

    /**
     * @param command [{"op":"add","path":"/837195323695104/skus?query=attributesSales:835604723556352-淡粉色,835604663263232-185~/100A~/XXL/storageActual","value":"1"}]
     * @return 835604723556352:淡粉色,835604663263232:185/100A/XXL
     */
    private static String parseAttrSales(PatchCommand command) {
        AtomicInteger index = new AtomicInteger();
        String[] split1 = command.getPath().split("/");
        String collect = Arrays.stream(split1).filter((e) -> index.getAndIncrement() > 1).collect(Collectors.joining("/"));
        String replace = collect.replace(ADMIN_REP_SKU_LITERAL + "?" + HTTP_PARAM_QUERY + "=" + ADMIN_REP_ATTR_SALES_LITERAL + ":", "");
        String replace1 = replace.replace("~/", "$");
        String[] split = replace1.split("/");
        if (split.length != 2)
            throw new NoUpdatableFieldException();
        String $ = split[0].replace("-", ":").replace("$", "/");
        return Arrays.stream($.split(",")).sorted((a, b) -> {
            long l = Long.parseLong(a.split(":")[0]);
            long l1 = Long.parseLong(b.split(":")[0]);
            return Long.compare(l, l1);
        }).collect(Collectors.joining(","));
    }

    public void replace(String name, Long startAt, Long endAt) {
        setName(name);
        setStartAt(startAt);
        setEndAt(endAt);
    }
}
