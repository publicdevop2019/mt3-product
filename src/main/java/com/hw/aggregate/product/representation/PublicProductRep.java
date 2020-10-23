package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.exception.AttributeNameNotFoundException;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductAttrSaleImages;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.sku.representation.AppBizSkuCardRep;
import com.hw.aggregate.tag.AppBizTagApplicationService;
import com.hw.aggregate.tag.representation.AppBizTagCardRep;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class PublicProductRep {
    private Long id;
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private BigDecimal lowestPrice;
    private Integer totalSales;
    private List<ProductSkuCustomerRepresentation> skus;
    private List<ProductAttrSaleImagesCustomerRepresentation> attributeSaleImages;
    private List<ProductOption> selectedOptions;
    private Map<String, String> attrIdMap;

    public PublicProductRep(Product productDetail, AppBizTagApplicationService appBizAttributeApplicationService, AppBizSkuApplicationService skuApplicationService) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.description = productDetail.getDescription();

        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<AppBizSkuCardRep> appBizSkuCardRepSumPagedRep = skuApplicationService.readByQuery("id:" + String.join(".", collect), null, null);
        this.skus = attrSalesMap.keySet().stream().map(e -> {
            ProductSkuCustomerRepresentation appProductSkuRep = new ProductSkuCustomerRepresentation();
            Long aLong = attrSalesMap.get(e);
            Optional<AppBizSkuCardRep> first = appBizSkuCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
            if (first.isPresent()) {
                HashSet<String> strings = new HashSet<>(Arrays.asList(e.split(",")));
                appProductSkuRep.setAttributesSales(strings);
                appProductSkuRep.setPrice(first.get().getPrice());
                appProductSkuRep.setStorage(first.get().getStorageOrder());
            }
            return appProductSkuRep;
        }).collect(Collectors.toList());

        this.lowestPrice = productDetail.getLowestPrice();
        this.totalSales = productDetail.getTotalSales();
        this.attrIdMap = new HashMap<>();

        this.skus.stream().map(ProductSkuCustomerRepresentation::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toList())
                .stream().map(e -> e.split(":")[0]).forEach(el -> attrIdMap.put(el, null));
        String search = "id:" + String.join(".", this.attrIdMap.keySet());
        SumPagedRep<AppBizTagCardRep> bizAttributeSummaryRepresentation;
        if (this.attrIdMap.keySet().size() > 0 && !onlyEmptyKeyExist(this.attrIdMap.keySet())) {
            String page = "size:" + this.attrIdMap.keySet().size();
            bizAttributeSummaryRepresentation = appBizAttributeApplicationService.readByQuery(search, page, "0");
            this.attrIdMap.keySet().forEach(e -> {
                attrIdMap.put(e, findName(e, bizAttributeSummaryRepresentation));
            });
        }
        if (productDetail.getAttributeSaleImages() != null)
            this.attributeSaleImages = productDetail.getAttributeSaleImages().stream().map(ProductAttrSaleImagesCustomerRepresentation::new).collect(Collectors.toList());
        this.selectedOptions = productDetail.getSelectedOptions();
    }

    private boolean onlyEmptyKeyExist(Set<String> strings) {
        return (strings.size() == 1 && strings.contains(""));
    }

    @Data
    @NoArgsConstructor
    public static class ProductSkuCustomerRepresentation {
        private Set<String> attributesSales;
        private Integer storage;
        private BigDecimal price;
    }

    @Data
    public static class ProductAttrSaleImagesCustomerRepresentation {
        private String attributeSales;
        private Set<String> imageUrls;

        public ProductAttrSaleImagesCustomerRepresentation(ProductAttrSaleImages productAttrSaleImages) {
            this.attributeSales = productAttrSaleImages.getAttributeSales();
            this.imageUrls = productAttrSaleImages.getImageUrls();
        }
    }

    private String findName(String id, SumPagedRep<AppBizTagCardRep> attributeSummaryRepresentation) {
        Optional<AppBizTagCardRep> first = attributeSummaryRepresentation.getData().stream().filter(e -> e.getId().toString().equals(id)).findFirst();
        if (first.isEmpty())
            throw new AttributeNameNotFoundException();
        return first.get().getName();
    }

}
