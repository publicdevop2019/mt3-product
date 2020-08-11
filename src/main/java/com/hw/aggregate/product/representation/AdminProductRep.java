package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductAttrSaleImages;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AdminProductRep {
    private Long id;

    private String name;
    public transient static final String ADMIN_REP_NAME_LITERAL = "name";

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Long startAt;
    public transient static final String ADMIN_REP_START_AT_LITERAL = "startAt";
    private Long endAt;
    public transient static final String ADMIN_REP_END_AT_LITERAL = "endAt";

    private Set<String> specification;

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSkuAdminRepresentation> skus;
    public transient static final String ADMIN_REP_SKU_LITERAL = "skus";

    private List<ProductAttrSaleImagesAdminRepresentation> attributeSaleImages;

    private Integer totalSales;
    public transient static final String ADMIN_REP_SALES_LITERAL = "totalSales";

    public AdminProductRep(Product productDetail) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.description = productDetail.getDescription();
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.selectedOptions = productDetail.getSelectedOptions();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.specification = productDetail.getSpecification();
        this.attributesKey = productDetail.getAttrKey();
        this.attributesProd = productDetail.getAttrProd();
        this.attributesGen = productDetail.getAttrGen();
        this.skus = productDetail.getProductSkuList().stream().map(ProductSkuAdminRepresentation::new).collect(Collectors.toList());
        this.totalSales = productDetail.getTotalSales();
        if (productDetail.getAttributeSaleImages() != null)
            this.attributeSaleImages = productDetail.getAttributeSaleImages().stream().map(ProductAttrSaleImagesAdminRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class ProductSkuAdminRepresentation {
        private Set<String> attributesSales;
        public transient static final String ADMIN_REP_ATTR_SALES_LITERAL = "attributesSales";
        private Integer storageOrder;
        public transient static final String ADMIN_REP_SKU_STORAGE_ORDER_LITERAL = "storageOrder";
        private Integer storageActual;
        public transient static final String ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL = "storageActual";
        private BigDecimal price;
        private Integer sales;
        public transient static final String ADMIN_REP_SKU_SALES_LITERAL = "sales";

        public ProductSkuAdminRepresentation(ProductSku sku) {
            this.attributesSales = sku.getAttributesSales();
            this.storageOrder = sku.getStorageOrder();
            this.storageActual = sku.getStorageActual();
            this.price = sku.getPrice();
            this.sales = sku.getSales();
        }
    }

    @Data
    public static class ProductAttrSaleImagesAdminRepresentation {
        private String attributeSales;
        private Set<String> imageUrls;

        public ProductAttrSaleImagesAdminRepresentation(ProductAttrSaleImages productAttrSaleImages) {
            this.attributeSales = productAttrSaleImages.getAttributeSales();
            this.imageUrls = productAttrSaleImages.getImageUrls();
        }
    }
}
