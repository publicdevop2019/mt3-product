package com.hw.aggregate.product.model;

import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.exception.SkuAlreadyExistException;
import com.hw.aggregate.product.exception.SkuNotExistException;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.sku.command.AppCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AppUpdateBizSkuCommand;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ORDER_LITERAL;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_DIFF;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_SUM;


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

    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;
    public transient static final String PRODUCT_SPEC_LITERAL = "specification";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrKey;
    public transient static final String PRODUCT_ATTR_KEY_LITERAL = "attrKey";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrProd;
    public transient static final String PRODUCT_ATTR_PROD_LITERAL = "attrProd";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrGen;
    public transient static final String PRODUCT_ATTR_GEN_LITERAL = "attrGen";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrSalesTotal;
    public transient static final String PRODUCT_ATTR_SALES_TOTAL_LITERAL = "attrSalesTotal";

    @Column(length = 10000)
    private HashMap<String, Long> attrSalesMap;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    private BigDecimal lowestPrice;
    public transient static final String PRODUCT_LOWEST_PRICE_LITERAL = "lowestPrice";

    @Column(updatable = false)
    private Integer totalSales;
    public transient static final String PRODUCT_TOTAL_SALES_LITERAL = "totalSales";

    public static Product create(Long id, AdminCreateProductCommand command, AppBizSkuApplicationService appBizSkuApplicationService) {
        return new Product(id, command, appBizSkuApplicationService);
    }

    public void replace(AdminUpdateProductCommand command, AppBizSkuApplicationService skuApplicationService) {
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.startAt = command.getStartAt();
        this.endAt = command.getEndAt();
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(new TreeSet<>(e.getAttributesSales()));
        });
        adjustSku(command.getSkus(), skuApplicationService);
        this.attrSalesTotal = command.getSkus().stream().map(AdminUpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
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

    private void adjustSku(List<AdminUpdateProductCommand.UpdateProductAdminSkuCommand> commands, AppBizSkuApplicationService skuApplicationService) {
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
                AppUpdateBizSkuCommand appUpdateBizSkuCommand = new AppUpdateBizSkuCommand();
                appUpdateBizSkuCommand.setPrice(command.getPrice());
                skuApplicationService.replaceById(aLong, appUpdateBizSkuCommand, UUID.randomUUID().toString());
                updateStorage(skuApplicationService, command);

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

    private void updateStorage(AppBizSkuApplicationService productApplicationService, AdminUpdateProductCommand.UpdateProductAdminSkuCommand command) {
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        if (command.getDecreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getDecreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        String changeId = UUID.randomUUID().toString();
        productApplicationService.patchBatch(patchCommands, changeId);
    }

    private String toSkuQueryPath(AdminUpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType) {
        Long aLong = attrSalesMap.get(getAttrSalesKey(command.getAttributesSales()));
        return "/" + aLong + "/" + storageType;
    }


    private Product(Long id, AdminCreateProductCommand command, AppBizSkuApplicationService appBizSkuApplicationService) {
        this.id = id;
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.startAt = (command.getStartAt());
        this.endAt = (command.getEndAt());
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(e.getAttributesSales());
        });
        this.attrSalesTotal = command.getSkus().stream().map(AdminCreateProductCommand.CreateProductSkuAdminCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());

        for (AdminCreateProductCommand.CreateProductSkuAdminCommand skuAdminCommand : command.getSkus()) {
            AppCreateBizSkuCommand command1 = new AppCreateBizSkuCommand();
            command1.setPrice(skuAdminCommand.getPrice());
            command1.setReferenceId(this.id.toString());
            command1.setStorageOrder(skuAdminCommand.getStorageOrder());
            command1.setStorageActual(skuAdminCommand.getStorageActual());
            command1.setSales(skuAdminCommand.getSales());
            CreatedEntityRep createdEntityRep = appBizSkuApplicationService.create(command1, UUID.randomUUID().toString());
            attrSalesMap.put(String.join(",", skuAdminCommand.getAttributesSales()), createdEntityRep.getId());
        }
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

}
