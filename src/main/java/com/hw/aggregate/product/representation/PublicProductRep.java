package com.hw.aggregate.product.representation;

import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.attribute.representation.AppBizAttributeCardRep;
import com.hw.aggregate.product.exception.AttributeNameNotFoundException;
import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductAttrSaleImages;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

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
    private Set<String> specification;
    private BigDecimal lowestPrice;
    private Integer totalSales;
    private List<ProductSkuCustomerRepresentation> skus;
    private List<ProductAttrSaleImagesCustomerRepresentation> attributeSaleImages;
    private List<ProductOption> selectedOptions;
    private Map<String, String> attrIdMap;

    public PublicProductRep(Product productDetail, AppBizAttributeApplicationService appBizAttributeApplicationService) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.description = productDetail.getDescription();
        this.specification = productDetail.getSpecification();
        this.lowestPrice = findLowestPrice(productDetail);
        this.totalSales = calcTotalSales(productDetail);
        this.skus = getCustomerSku(productDetail);
        this.attrIdMap = new HashMap<>();
        this.skus.stream().map(ProductSkuCustomerRepresentation::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toList())
                .stream().map(e -> e.split(":")[0]).forEach(el -> attrIdMap.put(el, null));
        String search = "id:" + String.join(".", this.attrIdMap.keySet());
        SumPagedRep<AppBizAttributeCardRep> bizAttributeSummaryRepresentation;
        if (this.attrIdMap.keySet().size() > 0) {
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

    @Data
    public static class ProductSkuCustomerRepresentation {
        private Set<String> attributesSales;
        private Integer storage;
        private BigDecimal price;

        public ProductSkuCustomerRepresentation(ProductSku productSku) {
            this.attributesSales = productSku.getAttributesSales();
            this.storage = productSku.getStorageOrder();
            this.price = productSku.getPrice();
        }

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

    private String findName(String id, SumPagedRep<AppBizAttributeCardRep> attributeSummaryRepresentation) {
        Optional<AppBizAttributeCardRep> first = attributeSummaryRepresentation.getData().stream().filter(e -> e.getId().toString().equals(id)).findFirst();
        if (first.isEmpty())
            throw new AttributeNameNotFoundException();
        return first.get().getName();
    }

    private List<ProductSkuCustomerRepresentation> getCustomerSku(Product productDetail) {
        List<ProductSku> productSkuList = productDetail.getProductSkuList();
        return productSkuList.stream().map(ProductSkuCustomerRepresentation::new).collect(Collectors.toList());
    }

    private Integer calcTotalSales(Product productDetail) {
        return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(Product productDetail) {
        ProductSku productSku = productDetail.getProductSkuList().stream().min(Comparator.comparing(ProductSku::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return productSku.getPrice();
    }
}
