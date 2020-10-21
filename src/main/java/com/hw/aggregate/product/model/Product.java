package com.hw.aggregate.product.model;

import com.hw.aggregate.product.AppProductApplicationService;
import com.hw.aggregate.product.TagRepo;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.exception.SkuAlreadyExistException;
import com.hw.aggregate.product.exception.SkuNotExistException;
import com.hw.aggregate.product.representation.AppProductCardRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.sku.command.AppCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AppUpdateBizSkuCommand;
import com.hw.aggregate.sku.representation.AppBizSkuRep;
import com.hw.shared.Auditable;
import com.hw.shared.IdGenerator;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.rest.exception.EntityNotExistException;
import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ProductSkuAdminRepresentation.*;
import static com.hw.shared.AppConstant.*;


@Data
@Entity
@Table(name = "biz_product")
@NoArgsConstructor
@Slf4j
public class Product extends Auditable implements IdBasedEntity {
    @Id
    private Long id;

    private String imageUrlSmall;

    private String name;
    public transient static final String PRODUCT_NAME_LITERAL = "name";

    private String description;

    private Long endAt;
    public transient static final String PRODUCT_END_AT_LITERAL = "endAt";

    private Long startAt;
    public transient static final String PRODUCT_START_AT_LITERAL = "startAt";

    @Column(length = 10000)
    @Convert(converter = ProductOption.ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;
    public transient static final String PRODUCT_SELECTED_OPTIONS_LITERAL = "selectedOptions";

    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;
    public transient static final String PRODUCT_IMAGE_URL_LARGE_LITERAL = "imageUrlLarge";

    @Column(length = 10000)
    private HashMap<String, Long> attrSalesMap;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }


    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "biz_product_tag_map",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private BigDecimal lowestPrice;
    public transient static final String PRODUCT_LOWEST_PRICE_LITERAL = "lowestPrice";

    @Column(updatable = false)
    private Integer totalSales;
    public transient static final String PRODUCT_TOTAL_SALES_LITERAL = "totalSales";

    public static Product create(Long id, AdminCreateProductCommand command, AppBizSkuApplicationService appBizSkuApplicationService, IdGenerator idGenerator, TagRepo tagRepo) {
        return new Product(id, command, appBizSkuApplicationService, idGenerator, tagRepo);
    }

    public void replace(AdminUpdateProductCommand command, AppBizSkuApplicationService skuApplicationService, TagRepo tagRepo, IdGenerator idGenerator) {
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.startAt = command.getStartAt();
        this.endAt = command.getEndAt();
        if (command.getAttributesProd() != null)
            command.getAttributesProd().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.PROD));
        if (command.getAttributesKey() != null)
            command.getAttributesKey().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.KEY));
        if (command.getAttributesGen() != null)
            command.getAttributesGen().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.GEN));
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(new TreeSet<>(e.getAttributesSales()));
        });
        adjustSku(command.getSkus(), skuApplicationService, command.getChangeId());
        command.getSkus().stream().map(AdminUpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet()).forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.SALES));
        if (command.getAttributeSaleImages() != null)
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
        this.lowestPrice = findLowestPrice(command);
    }

    private Consumer<String> getStringConsumer(TagRepo tagRepo, IdGenerator idGenerator, TagTypeEnum key) {
        return e -> {
            Optional<Tag> byValue = tagRepo.findByValueAndType(e, key);
            if (byValue.isPresent()) {
                addTag(byValue.get());
            } else {
                Tag tag = new Tag(idGenerator.getId(), e, key);
                addTag(tag);
            }
        };
    }

    private void adjustSku(List<AdminUpdateProductCommand.UpdateProductAdminSkuCommand> commands, AppBizSkuApplicationService skuApplicationService, String changeId) {
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                if (this.attrSalesMap.containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuAlreadyExistException();
                }
                AppCreateBizSkuCommand command1 = new AppCreateBizSkuCommand();
                command1.setPrice(command.getPrice());
                command1.setReferenceId(this.id.toString());
                command1.setStorageOrder(command.getStorageOrder());
                command1.setStorageActual(command.getStorageActual());
                command1.setSales(command.getSales() == null ? 0 : command.getSales());
                CreatedEntityRep createdEntityRep = skuApplicationService.create(command1, UUID.randomUUID().toString());
                if (attrSalesMap == null)
                    attrSalesMap = new HashMap<>();
                attrSalesMap.put(getAttrSalesKey(command.getAttributesSales()), createdEntityRep.getId());
            } else {
                //existing sku
                if (!this.attrSalesMap.containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuNotExistException();
                }
                //update price
                Long aLong = this.attrSalesMap.get(getAttrSalesKey(command.getAttributesSales()));
                AppBizSkuRep appBizSkuRep = skuApplicationService.readById(aLong);
                if (appBizSkuRep.getPrice().compareTo(command.getPrice()) != 0) {
                    AppUpdateBizSkuCommand appUpdateBizSkuCommand = new AppUpdateBizSkuCommand();
                    appUpdateBizSkuCommand.setPrice(command.getPrice());
                    //price will be update in a different changeId
                    skuApplicationService.replaceById(aLong, appUpdateBizSkuCommand, UUID.randomUUID().toString());
                }
                updateStorage(skuApplicationService, command, changeId);

            }
        });
        // find skus not in update command & remove
        List<String> collect = attrSalesMap.keySet().stream().filter(e -> commands.stream().noneMatch(command -> getAttrSalesKey(command.getAttributesSales()).equals(e))).collect(Collectors.toList());
        Set<String> collect1 = collect.stream().map(e -> attrSalesMap.get(e).toString()).collect(Collectors.toSet());
        if (collect1.size() > 0)
            skuApplicationService.deleteByQuery(String.join(".", collect1), UUID.randomUUID().toString());
    }

    private String getAttrSalesKey(Set<String> attributesSales) {
        return String.join(",", attributesSales);
    }

    private void updateStorage(AppBizSkuApplicationService productApplicationService, AdminUpdateProductCommand.UpdateProductAdminSkuCommand command, String changeId) {
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
            productApplicationService.patchBatch(patchCommands, changeId);
    }

    private String toSkuQueryPath(AdminUpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType) {
        Long aLong = attrSalesMap.get(getAttrSalesKey(command.getAttributesSales()));
        return "/" + aLong + "/" + storageType;
    }


    private Product(Long id, AdminCreateProductCommand command, AppBizSkuApplicationService appBizSkuApplicationService, IdGenerator idGenerator, TagRepo tagRepo) {
        this.id = id;
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        if (command.getAttributesProd() != null)
            command.getAttributesProd().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.PROD));
        if (command.getAttributesKey() != null)
            command.getAttributesKey().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.KEY));
        if (command.getAttributesGen() != null)
            command.getAttributesGen().forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.GEN));
        this.startAt = (command.getStartAt());
        this.endAt = (command.getEndAt());
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(e.getAttributesSales());
        });
        command.getSkus().stream().map(AdminCreateProductCommand.CreateProductSkuAdminCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet()).forEach(getStringConsumer(tagRepo, idGenerator, TagTypeEnum.SALES));

        for (AdminCreateProductCommand.CreateProductSkuAdminCommand skuAdminCommand : command.getSkus()) {
            AppCreateBizSkuCommand command1 = new AppCreateBizSkuCommand();
            command1.setPrice(skuAdminCommand.getPrice());
            command1.setReferenceId(this.id.toString());
            command1.setStorageOrder(skuAdminCommand.getStorageOrder());
            command1.setStorageActual(skuAdminCommand.getStorageActual());
            command1.setSales(skuAdminCommand.getSales());
            CreatedEntityRep createdEntityRep = appBizSkuApplicationService.create(command1, UUID.randomUUID().toString());
            if (attrSalesMap == null)
                attrSalesMap = new HashMap<>();
            attrSalesMap.put(String.join(",", skuAdminCommand.getAttributesSales()), createdEntityRep.getId());
        }
        if (command.getAttributeSaleImages() != null)
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e -> {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
        this.lowestPrice = findLowestPrice(command);
        this.totalSales = calcTotalSales(command);
    }


    private Integer calcTotalSales(AdminCreateProductCommand command) {
        return command.getSkus().stream().map(AdminCreateProductCommand.CreateProductSkuAdminCommand::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(AdminCreateProductCommand command) {
        AdminCreateProductCommand.CreateProductSkuAdminCommand createProductSkuAdminCommand = command.getSkus().stream().min(Comparator.comparing(AdminCreateProductCommand.CreateProductSkuAdminCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return createProductSkuAdminCommand.getPrice();
    }

    private BigDecimal findLowestPrice(AdminUpdateProductCommand command) {
        AdminUpdateProductCommand.UpdateProductAdminSkuCommand updateProductAdminSkuCommand = command.getSkus().stream().min(Comparator.comparing(AdminUpdateProductCommand.UpdateProductAdminSkuCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return updateProductAdminSkuCommand.getPrice();
    }

    public static List<PatchCommand> convertToSkuCommands(List<PatchCommand> hasNestedEntity, AppProductApplicationService appProductApplicationService) {
        Set<String> collect = hasNestedEntity.stream().map(e -> e.getPath().split("/")[1]).collect(Collectors.toSet());
        String join = "id:" + String.join(".", collect);
        SumPagedRep<AppProductCardRep> appProductCardRepSumPagedRep = appProductApplicationService.readByQuery(join, null, "sc:1");
        hasNestedEntity.forEach(e -> {
            String[] split = e.getPath().split("/");
            String id = split[1];
            String fieldName = split[split.length - 1];
            String attrSales = parseAttrSales(e);
            Optional<AppProductCardRep> first = appProductCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().toString().equals(id)).findFirst();
            if (first.isPresent()) {
                Long aLong = first.get().getAttrSalesMap().get(attrSales);
                e.setPath("/" + aLong + "/" + fieldName);
            } else {
                throw new EntityNotExistException();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        return id != null && id.equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", imageUrlSmall='" + imageUrlSmall + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", endAt=" + endAt +
                ", startAt=" + startAt +
                ", selectedOptions=" + selectedOptions +
                ", imageUrlLarge=" + imageUrlLarge +
                ", tags=" + tags +
                ", attrSalesMap=" + attrSalesMap +
                ", attributeSaleImages=" + attributeSaleImages +
                ", lowestPrice=" + lowestPrice +
                ", totalSales=" + totalSales +
                '}';
    }
}
