package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.*;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.sku.representation.AppBizSkuCardRep;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
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

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSkuAdminRepresentation> skus;
    public transient static final String ADMIN_REP_SKU_LITERAL = "skus";

    private List<ProductAttrSaleImagesAdminRepresentation> attributeSaleImages;

    private Integer totalSales;
    public transient static final String ADMIN_REP_SALES_LITERAL = "totalSales";
    private BigDecimal lowestPrice;
    public static final String ADMIN_REP_PRICE_LITERAL = "lowestPrice";

    public AdminProductRep(Product productDetail, AppBizSkuApplicationService skuApplicationService) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.description = productDetail.getDescription();
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.selectedOptions = productDetail.getSelectedOptions();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.attributesKey = productDetail.getTags().stream().map(Tag::getValue).collect(Collectors.toSet());
        this.attributesProd = productDetail.getTags().stream().filter(e->e.getType().equals(TagTypeEnum.PROD)).map(Tag::getValue).collect(Collectors.toSet());
        this.attributesGen = productDetail.getTags().stream().filter(e->e.getType().equals(TagTypeEnum.GEN)).map(Tag::getValue).collect(Collectors.toSet());

        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<AppBizSkuCardRep> appBizSkuCardRepSumPagedRep = skuApplicationService.readByQuery("id:"+String.join(".", collect), null, null);
        this.skus = attrSalesMap.keySet().stream().map(e -> {
            ProductSkuAdminRepresentation appProductSkuRep = new ProductSkuAdminRepresentation();
            Long aLong = attrSalesMap.get(e);
            Optional<AppBizSkuCardRep> first = appBizSkuCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
            if (first.isPresent()) {
                HashSet<String> strings = new HashSet<>(Arrays.asList(e.split(",")));
                appProductSkuRep.setSales(first.get().getSales());
                appProductSkuRep.setPrice(first.get().getPrice());
                appProductSkuRep.setAttributesSales(strings);
                appProductSkuRep.setStorageActual(first.get().getStorageActual());
                appProductSkuRep.setStorageOrder(first.get().getStorageOrder());
            }
            return appProductSkuRep;
        }).collect(Collectors.toList());

        this.totalSales = productDetail.getTotalSales();
        this.lowestPrice = productDetail.getLowestPrice();
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
