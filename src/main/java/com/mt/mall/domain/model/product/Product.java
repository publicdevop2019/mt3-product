package com.mt.mall.domain.model.product;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
import com.mt.common.rest.exception.AggregateNotExistException;
import com.mt.common.rest.exception.AggregateOutdatedException;
import com.mt.common.rest.exception.NoUpdatableFieldException;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.application.product.exception.NoLowestPriceFoundException;
import com.mt.mall.application.product.exception.SkuAlreadyExistException;
import com.mt.mall.application.product.exception.SkuNotExistException;
import com.mt.mall.application.sku.command.CreateSkuCommand;
import com.mt.mall.application.sku.command.UpdateSkuCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.sku.Sku;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
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
    @Setter(AccessLevel.PRIVATE)
    private String imageUrlSmall;
    @Setter(AccessLevel.PRIVATE)
    private String name;
    @Setter(AccessLevel.PRIVATE)
    private String description;
    @Setter(AccessLevel.PRIVATE)
    private Long endAt;
    @Setter(AccessLevel.PRIVATE)
    private Long startAt;

    @Column(length = 10000)
    @Convert(converter = ProductOption.ProductOptionConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private List<ProductOption> selectedOptions;

    @Convert(converter = StringSetConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Set<String> imageUrlLarge;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "productId", updatable = false, nullable = false))
    })
    private ProductId productId;
    @Column(length = 10000)
    @Setter(AccessLevel.PRIVATE)
    private HashMap<String, String> attrSalesMap;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public void addTag(ProductTag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    public void removeTag(ProductTag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }


    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "product_tag_map",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ProductTag> tags = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    private BigDecimal lowestPrice;

    @Column(updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Integer totalSales;

    public void replace(String name,
                        String imageUrlSmall,
                        Set<String> imageUrlLarge,
                        String description,
                        Long startAt,
                        Long endAt,
                        List<ProductOption> selectedOptions,
                        Set<String> attributesKey,
                        Set<String> attributesProd,
                        Set<String> attributesGen,
                        List<UpdateProductCommand.UpdateProductAdminSkuCommand> skus,
                        List<UpdateProductCommand.UpdateProductAttrImageAdminCommand> attributeSaleImages,
                        String changeId,
                        Integer version
    ) {
        if (!getVersion().equals(version))
            throw new AggregateOutdatedException();
        setImageUrlSmall(imageUrlSmall);
        setName(name);
        setDescription(description);
        setSelectedOptions(selectedOptions);
        setImageUrlLarge(imageUrlLarge);
        setStartAt(startAt);
        setEndAt(endAt);

        skus.forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(new TreeSet<>(e.getAttributesSales()));
        });
        adjustSku(skus, changeId);
        setAttributeSaleImages2(attributeSaleImages);
        this.lowestPrice = findLowestPrice2(skus);
        Set<String> sales = skus.stream().map(UpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if(!getTags().equals(getProductTags(sales,attributesKey,attributesGen,attributesProd))){
            this.tags=new HashSet<>();
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
        if(sales!=null){
            sales.forEach(e->{
                Optional<ProductTag> byValue = DomainRegistry.productTagRepository().findByValueAndType(e, TagType.SALES);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.uniqueIdGeneratorService().id(), e, TagType.SALES);
                    productTags.add(tag);
                }
            });
        }
        if(attributesKey!=null){
            attributesKey.forEach(e->{
                Optional<ProductTag> byValue = DomainRegistry.productTagRepository().findByValueAndType(e, TagType.KEY);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.uniqueIdGeneratorService().id(), e, TagType.KEY);
                    productTags.add(tag);
                }
            });
        }
        if(attributesGen!=null){
            attributesGen.forEach(e->{
                Optional<ProductTag> byValue = DomainRegistry.productTagRepository().findByValueAndType(e, TagType.GEN);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.uniqueIdGeneratorService().id(), e, TagType.GEN);
                    productTags.add(tag);
                }
            });
        }
        if(attributesProd!=null){
            attributesProd.forEach(e->{
                Optional<ProductTag> byValue = DomainRegistry.productTagRepository().findByValueAndType(e, TagType.PROD);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.uniqueIdGeneratorService().id(), e, TagType.PROD);
                    productTags.add(tag);
                }
            });
        }
        return productTags;
    }

    private void setAttributeSaleImages2(List<UpdateProductCommand.UpdateProductAttrImageAdminCommand> attributeSaleImages) {
        if (attributeSaleImages != null)
            this.attributeSaleImages = attributeSaleImages.stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
    }

    private Consumer<String> getStringConsumer(TagType key) {
        return e -> {
            Optional<ProductTag> byValue = DomainRegistry.productTagRepository().findByValueAndType(e, key);
            if (byValue.isPresent()) {
                addTag(byValue.get());
            } else {
                ProductTag tag = new ProductTag(CommonDomainRegistry.uniqueIdGeneratorService().id(), e, key);
                addTag(tag);
            }
        };
    }


    private void adjustSku(List<UpdateProductCommand.UpdateProductAdminSkuCommand> commands, String changeId) {
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                if (this.attrSalesMap.containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuAlreadyExistException();
                }
                CreateSkuCommand command1 = new CreateSkuCommand();
                command1.setPrice(command.getPrice());
                command1.setReferenceId(this.id.toString());
                command1.setStorageOrder(command.getStorageOrder());
                command1.setStorageActual(command.getStorageActual());
                command1.setSales(command.getSales() == null ? 0 : command.getSales());
                String s = ApplicationServiceRegistry.skuApplicationService().create(command1, UUID.randomUUID().toString());
                if (attrSalesMap == null)
                    attrSalesMap = new HashMap<>();
                attrSalesMap.put(getAttrSalesKey(command.getAttributesSales()), s);
            } else {
                //existing sku
                if (!this.attrSalesMap.containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuNotExistException();
                }
                //update price
                String s = this.attrSalesMap.get(getAttrSalesKey(command.getAttributesSales()));
                Optional<Sku> sku = ApplicationServiceRegistry.skuApplicationService().sku(s);
                if (sku.isPresent() && sku.get().getPrice().compareTo(command.getPrice()) != 0) {
                    UpdateSkuCommand appUpdateBizSkuCommand = new UpdateSkuCommand();
                    appUpdateBizSkuCommand.setPrice(command.getPrice());
                    appUpdateBizSkuCommand.setVersion(command.getVersion());
                    //price will be update in a different changeId
                    ApplicationServiceRegistry.skuApplicationService().replace(s, appUpdateBizSkuCommand, UUID.randomUUID().toString());
                }
                updateStorage(command, changeId);

            }
        });
        // find skus not in update command & remove
        List<String> collect = attrSalesMap.keySet().stream().filter(e -> commands.stream().noneMatch(command -> getAttrSalesKey(command.getAttributesSales()).equals(e))).collect(Collectors.toList());
        Set<String> collect1 = collect.stream().map(e -> attrSalesMap.get(e)).collect(Collectors.toSet());
        if (collect1.size() > 0)
            ApplicationServiceRegistry.skuApplicationService().removeByQuery(String.join(".", collect1), UUID.randomUUID().toString());
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
            ApplicationServiceRegistry.productApplicationService().patchBatch(patchCommands, changeId);
    }

    private String toSkuQueryPath(UpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType) {
        String s = attrSalesMap.get(getAttrSalesKey(command.getAttributesSales()));
        return "/" + s + "/" + storageType;
    }

    public Product(ProductId productId,
                   String name,
                   String imageUrlSmall,
                   Set<String> imageUrlLarge,
                   String description,
                   Long startAt,
                   Long endAt,
                   List<ProductOption> selectedOptions,
                   Set<String> attributesKey,
                   Set<String> attributesProd,
                   Set<String> attributesGen,
                   List<CreateProductCommand.CreateProductSkuAdminCommand> skus,
                   List<CreateProductCommand.CreateProductAttrImageAdminCommand> attributeSaleImages
    ) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
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

        for (CreateProductCommand.CreateProductSkuAdminCommand skuAdminCommand : skus) {
            CreateSkuCommand command1 = new CreateSkuCommand();
            command1.setPrice(skuAdminCommand.getPrice());
            command1.setReferenceId(this.id.toString());
            command1.setStorageOrder(skuAdminCommand.getStorageOrder());
            command1.setStorageActual(skuAdminCommand.getStorageActual());
            command1.setSales(skuAdminCommand.getSales());
            String domainId = ApplicationServiceRegistry.skuApplicationService().create(command1, UUID.randomUUID().toString());
            if (attrSalesMap == null)
                attrSalesMap = new HashMap<>();
            attrSalesMap.put(String.join(",", skuAdminCommand.getAttributesSales()), domainId);
        }
        setAttributeSaleImages(attributeSaleImages);
        setLowestPrice(findLowestPrice(skus));
        setTotalSales(calcTotalSales(skus));
    }

    public void setAttributeSaleImages(List<CreateProductCommand.CreateProductAttrImageAdminCommand> attributeSaleImages) {
        if (attributeSaleImages != null)
            this.attributeSaleImages = attributeSaleImages.stream().map(e -> {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
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
        SumPagedRep<Product> products = ApplicationServiceRegistry.productApplicationService().products(join, null, "sc:1");
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
