package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductAttrSaleImages;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductDetailAdminRepresentation {
    private Long id;

    private String name;

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Long startAt;
    private Long endAt;

    private Set<String> specification;

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSkuAdminRepresentation> skus;

    private List<ProductAttrSaleImagesAdminRepresentation> attributeSaleImages;
    private Integer storageOrder;

    private Integer storageActual;

    private BigDecimal price;

    private Integer sales;

    public ProductDetailAdminRepresentation(ProductDetail productDetail) {
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

        if (productDetail.getProductSkuList() != null && productDetail.getProductSkuList().size() != 0) {
            this.skus = productDetail.getProductSkuList().stream().map(ProductSkuAdminRepresentation::new).collect(Collectors.toList());
        } else {
            this.price = productDetail.getLowestPrice();
            this.sales = productDetail.getTotalSales();
            this.storageActual = productDetail.getStorageActual();
            this.storageOrder = productDetail.getStorageOrder();
        }
        if (productDetail.getAttributeSaleImages() != null)
            this.attributeSaleImages = productDetail.getAttributeSaleImages().stream().map(ProductAttrSaleImagesAdminRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class ProductSkuAdminRepresentation {
        private Set<String> attributesSales;
        private Integer storageOrder;
        private Integer storageActual;
        private BigDecimal price;
        private Integer sales;

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
        private List<String> imageUrls;

        public ProductAttrSaleImagesAdminRepresentation(ProductAttrSaleImages productAttrSaleImages) {
            this.attributeSales = productAttrSaleImages.getAttributeSales();
            this.imageUrls = productAttrSaleImages.getImageUrls();
        }
    }
}
