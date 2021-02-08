package com.mt.mall.application.product.representation;

import com.mt.mall.application.product.exception.AttributeNameNotFoundException;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductAttrSaleImages;
import com.mt.mall.domain.model.product.ProductOption;
import com.mt.mall.application.sku.AppBizSkuApplicationService;
import com.mt.mall.application.sku.representation.InternalSkuCardRepresentation;
import com.mt.mall.application.tag.AppBizTagApplicationService;
import com.mt.mall.application.tag.representation.InternalTagCardRepresentation;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class PublicProductRepresentation {
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

    public PublicProductRepresentation(Product productDetail, AppBizTagApplicationService appBizAttributeApplicationService, AppBizSkuApplicationService skuApplicationService) {
        BeanUtils.copyProperties(productDetail, this);

        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<InternalSkuCardRepresentation> appBizSkuCardRepSumPagedRep = skuApplicationService.readByQuery("id:" + String.join(".", collect), null, null);
        this.skus = attrSalesMap.keySet().stream().map(e -> {
            ProductSkuCustomerRepresentation appProductSkuRep = new ProductSkuCustomerRepresentation();
            Long aLong = attrSalesMap.get(e);
            Optional<InternalSkuCardRepresentation> first = appBizSkuCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
            if (first.isPresent()) {
                HashSet<String> strings = new HashSet<>(Arrays.asList(e.split(",")));
                appProductSkuRep.setAttributesSales(strings);
                appProductSkuRep.setPrice(first.get().getPrice());
                appProductSkuRep.setStorage(first.get().getStorageOrder());
            }
            return appProductSkuRep;
        }).collect(Collectors.toList());

        this.attrIdMap = new HashMap<>();

        this.skus.stream().map(ProductSkuCustomerRepresentation::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toList())
                .stream().map(e -> e.split(":")[0]).forEach(el -> attrIdMap.put(el, null));
        String search = "id:" + String.join(".", this.attrIdMap.keySet());
        SumPagedRep<InternalTagCardRepresentation> bizAttributeSummaryRepresentation;
        if (this.attrIdMap.keySet().size() > 0 && !onlyEmptyKeyExist(this.attrIdMap.keySet())) {
            String page = "size:" + this.attrIdMap.keySet().size();
            bizAttributeSummaryRepresentation = appBizAttributeApplicationService.readByQuery(search, page, "0");
            this.attrIdMap.keySet().forEach(e -> {
                attrIdMap.put(e, findName(e, bizAttributeSummaryRepresentation));
            });
        }
        if (productDetail.getAttributeSaleImages() != null)
            this.attributeSaleImages = productDetail.getAttributeSaleImages().stream().map(ProductAttrSaleImagesCustomerRepresentation::new).collect(Collectors.toList());
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
            BeanUtils.copyProperties(productAttrSaleImages, this);
        }
    }

    private String findName(String id, SumPagedRep<InternalTagCardRepresentation> attributeSummaryRepresentation) {
        Optional<InternalTagCardRepresentation> first = attributeSummaryRepresentation.getData().stream().filter(e -> e.getId().toString().equals(id)).findFirst();
        if (first.isEmpty())
            throw new AttributeNameNotFoundException();
        return first.get().getName();
    }

}