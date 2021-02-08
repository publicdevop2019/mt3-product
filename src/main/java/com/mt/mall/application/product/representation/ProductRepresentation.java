package com.mt.mall.application.product.representation;

import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.domain.model.product.*;
import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ProductRepresentation {
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
    private Integer version;

    public ProductRepresentation(Product productDetail) {
        BeanUtils.copyProperties(productDetail, this);
        this.attributesKey = productDetail.getTags().stream().filter(e -> e.getType().equals(TagTypeEnum.KEY)).map(Tag::getValue).collect(Collectors.toSet());
        this.attributesProd = productDetail.getTags().stream().filter(e -> e.getType().equals(TagTypeEnum.PROD)).map(Tag::getValue).collect(Collectors.toSet());
        this.attributesGen = productDetail.getTags().stream().filter(e -> e.getType().equals(TagTypeEnum.GEN)).map(Tag::getValue).collect(Collectors.toSet());

        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<Sku> skus = ApplicationServiceRegistry.skuApplicationService().skus("id:" + String.join(".", collect), null, null);
        this.skus = attrSalesMap.keySet().stream().map(e -> {
            ProductSkuAdminRepresentation appProductSkuRep = new ProductSkuAdminRepresentation();
            Long aLong = attrSalesMap.get(e);
            Optional<Sku> first = skus.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
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
            BeanUtils.copyProperties(productAttrSaleImages, this);
        }
    }
}
