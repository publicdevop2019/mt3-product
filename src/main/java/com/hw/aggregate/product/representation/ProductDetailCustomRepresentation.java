package com.hw.aggregate.product.representation;

import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.aggregate.product.exception.AttributeNameNotFoundException;
import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ProductDetailCustomRepresentation {
    private Long id;
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private Set<String> specification;
    private BigDecimal lowestPrice;
    private Integer totalSales;
    private List<ProductSkuCustomerRepresentation> skus;
    private List<ProductOption> selectedOptions;
    private Map<String, String> attrIdMap;

    public ProductDetailCustomRepresentation(ProductDetail productDetail, BizAttributeSummaryRepresentation attributeSummaryRepresentation) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.description = productDetail.getDescription();
        this.specification = productDetail.getSpecification();
        this.lowestPrice = findLowestPrice(productDetail);
        this.totalSales = calcTotalSales(productDetail);
        this.skus = getCustomerSku(productDetail, attributeSummaryRepresentation);
        this.selectedOptions = productDetail.getSelectedOptions();
        this.attrIdMap = new HashMap<>();
        this.skus.stream().map(ProductSkuCustomerRepresentation::getAttributeSales).flatMap(Collection::stream).collect(Collectors.toList())
                .stream().map(e -> e.split(":")[0]).forEach(el -> attrIdMap.put(el, findName(el, attributeSummaryRepresentation)));
    }

    private String findName(String id, BizAttributeSummaryRepresentation attributeSummaryRepresentation) {
        Optional<BizAttributeSummaryRepresentation.BizAttributeCardRepresentation> first = attributeSummaryRepresentation.getData().stream().filter(e -> e.getId().toString().equals(id)).findFirst();
        if (first.isEmpty())
            throw new AttributeNameNotFoundException();
        return first.get().getName();
    }

    private List<ProductSkuCustomerRepresentation> getCustomerSku(ProductDetail productDetail, BizAttributeSummaryRepresentation attributeSummaryRepresentation) {
        List<ProductSku> productSkuList = productDetail.getProductSkuList();
        return productSkuList.stream().map(ProductSkuCustomerRepresentation::new).collect(Collectors.toList());
    }

    private Integer calcTotalSales(ProductDetail productDetail) {
        return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(ProductDetail productDetail) {
        ProductSku productSku = productDetail.getProductSkuList().stream().min(Comparator.comparing(ProductSku::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return productSku.getPrice();
    }

    @Data
    public static class ProductSkuCustomerRepresentation {
        private Set<String> attributeSales;
        private Integer storageOrder;
        private BigDecimal price;

        public ProductSkuCustomerRepresentation(ProductSku productSku) {
            this.attributeSales = productSku.getAttributesSales();
            this.storageOrder = productSku.getStorageOrder();
            this.price = productSku.getPrice();
        }

    }
}
