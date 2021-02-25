package com.mt.mall.application.product.representation;

import com.mt.common.domain.model.restful.SumPagedRep;
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
    public transient static final String ADMIN_REP_NAME_LITERAL = "name";
    public transient static final String ADMIN_REP_START_AT_LITERAL = "startAt";
    public transient static final String ADMIN_REP_END_AT_LITERAL = "endAt";
    public transient static final String ADMIN_REP_SKU_LITERAL = "skus";
    public transient static final String ADMIN_REP_SALES_LITERAL = "totalSales";
    public static final String ADMIN_REP_PRICE_LITERAL = "lowestPrice";

    private String id;

    private String name;

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Long startAt;
    private Long endAt;

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSkuAdminRepresentation> skus;

    private List<ProductAttrSaleImagesAdminRepresentation> attributeSaleImages;

    private Integer totalSales;
    private BigDecimal lowestPrice;
    private Integer version;

    public ProductRepresentation(Product product) {
        setId(product.getProductId().getDomainId());
        setName(product.getName());
        setImageUrlSmall(product.getImageUrlSmall());
        setImageUrlLarge(product.getImageUrlLarge());
        setDescription(product.getDescription());
        setStartAt(product.getStartAt());
        setEndAt(product.getEndAt());
        setSelectedOptions(product.getSelectedOptions());
        setTotalSales(product.getTotalSales());
        setLowestPrice(product.getLowestPrice());
        setVersion(product.getVersion());
        this.attributesKey = product.getTags().stream().filter(e -> e.getType().equals(TagType.KEY)).map(ProductTag::getValue).collect(Collectors.toSet());
        this.attributesProd = product.getTags().stream().filter(e -> e.getType().equals(TagType.PROD)).map(ProductTag::getValue).collect(Collectors.toSet());
        this.attributesGen = product.getTags().stream().filter(e -> e.getType().equals(TagType.GEN)).map(ProductTag::getValue).collect(Collectors.toSet());

        HashMap<String, String> attrSalesMap = product.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<Sku> skus = ApplicationServiceRegistry.skuApplicationService().skus("id:" + String.join(".", collect), null, null);
        this.skus = attrSalesMap.keySet().stream().map(e -> {
            ProductSkuAdminRepresentation appProductSkuRep = new ProductSkuAdminRepresentation();
            String aLong = attrSalesMap.get(e);
            Optional<Sku> first = skus.getData().stream().filter(ee -> ee.getSkuId().getDomainId().equals(aLong)).findFirst();
            if (first.isPresent()) {
                HashSet<String> strings = new HashSet<>(Arrays.asList(e.split(",")));
                appProductSkuRep.setSales(first.get().getSales());
                appProductSkuRep.setPrice(first.get().getPrice());
                appProductSkuRep.setAttributesSales(strings);
                appProductSkuRep.setStorageActual(first.get().getStorageActual());
                appProductSkuRep.setStorageOrder(first.get().getStorageOrder());
                appProductSkuRep.setVersion(first.get().getVersion());

            }
            return appProductSkuRep;
        }).collect(Collectors.toList());

        if (product.getAttributeSaleImages() != null)
            this.attributeSaleImages = product.getAttributeSaleImages().stream().map(ProductAttrSaleImagesAdminRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class ProductSkuAdminRepresentation {
        public transient static final String ADMIN_REP_ATTR_SALES_LITERAL = "attributesSales";
        public transient static final String ADMIN_REP_SKU_STORAGE_ORDER_LITERAL = "storageOrder";
        public transient static final String ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL = "storageActual";
        public transient static final String ADMIN_REP_SKU_SALES_LITERAL = "sales";
        private Set<String> attributesSales;
        private Integer storageOrder;
        private Integer storageActual;
        private BigDecimal price;
        private Integer sales;
        private Integer version;

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
