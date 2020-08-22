package com.hw.aggregate.product.model;

import com.hw.aggregate.product.AdminProductApplicationService;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.exception.SkuAlreadyExistException;
import com.hw.aggregate.product.exception.SkuNotExistException;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
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

    @OneToMany(targetEntity = ProductSku.class, mappedBy = "productId", cascade = {CascadeType.ALL})
    private List<ProductSku> productSkuList;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    private BigDecimal lowestPrice;
    public transient static final String PRODUCT_LOWEST_PRICE_LITERAL = "lowestPrice";

    @Column(updatable = false)
    private Integer totalSales;
    public transient static final String PRODUCT_TOTAL_SALES_LITERAL = "totalSales";

    public static Product create(Long id, AdminCreateProductCommand command) {
        return new Product(id, command);
    }

    public void replace(AdminUpdateProductCommand command, AdminProductApplicationService productApplicationService) {
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
        adjustSku(command.getSkus(), productApplicationService);
        this.attrSalesTotal = command.getSkus().stream().map(AdminUpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
        this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                {
                    ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                    productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                    productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                    return productAttrSaleImages;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        this.lowestPrice = findLowestPrice(this);
    }

    private void adjustSku(List<AdminUpdateProductCommand.UpdateProductAdminSkuCommand> commands, AdminProductApplicationService productApplicationService) {
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                boolean b = this.productSkuList.stream().anyMatch(e -> e.getAttributesSales().equals(command.getAttributesSales()));
                if (b)
                    throw new SkuAlreadyExistException();
                ProductSku productSku = new ProductSku();
                productSku.setSales(command.getSales() == null ? 0 : command.getSales());
                productSku.setStorageActual(command.getStorageActual());
                productSku.setStorageOrder(command.getStorageOrder());
                productSku.setAttributesSales(command.getAttributesSales());
                productSku.setPrice(command.getPrice());
                this.productSkuList.add(productSku);
            } else {
                //existing sku
                Optional<ProductSku> first = this.productSkuList.stream().filter(e -> e.getAttributesSales().equals(command.getAttributesSales())).findFirst();
                if (first.isEmpty())
                    throw new SkuNotExistException();
                //update price
                ProductSku productSku = first.get();
                productSku.setPrice(command.getPrice());
                updateStorage(productApplicationService, command);

            }
        });
        // find skus not in update command & remove
        List<ProductSku> collect = this.productSkuList.stream().filter(e -> commands.stream().noneMatch(command -> command.getAttributesSales().equals(e.getAttributesSales()))).collect(Collectors.toList());
        this.productSkuList.removeAll(collect);
    }

    private void updateStorage(AdminProductApplicationService productApplicationService, AdminUpdateProductCommand.UpdateProductAdminSkuCommand command) {
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        if (command.getDecreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getDecreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ORDER_LITERAL, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        String changeId = UUID.randomUUID().toString();
        productApplicationService.patchBatch(patchCommands, changeId);
    }

    private String toSkuQueryPath(AdminUpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType, Product productDetail) {
        Set<String> attributesSales1 = command.getAttributesSales();
        String join = String.join(",", attributesSales1);
        String replace = join.replace(":", "-").replace("/", "~/");
        String s = "/" + productDetail.getId() + "/" + ADMIN_REP_SKU_LITERAL + "?" + HTTP_PARAM_QUERY + "=" + ADMIN_REP_ATTR_SALES_LITERAL + ":" + replace;
        return s + "/" + storageType;
    }


    private Product(Long id, AdminCreateProductCommand command) {
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
        this.productSkuList = command.getSkus().stream().map(e -> {
            ProductSku productSku = new ProductSku();
            productSku.setPrice(e.getPrice());
            productSku.setProductId(this.id);
            productSku.setAttributesSales(e.getAttributesSales());
            productSku.setStorageOrder(e.getStorageOrder());
            productSku.setStorageActual(e.getStorageActual());
            productSku.setSales(e.getSales());
            return productSku;
        }).collect(Collectors.toList());
        this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e -> {
                    ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                    productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                    productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                    return productAttrSaleImages;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        this.lowestPrice = findLowestPrice(this);
        this.totalSales = calcTotalSales(this);
    }


    private Integer calcTotalSales(Product productDetail) {
        return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(Product productDetail) {
        ProductSku productSku = productDetail.getProductSkuList().stream().min(Comparator.comparing(ProductSku::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return productSku.getPrice();
    }

}
