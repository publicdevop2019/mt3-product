package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

    public ProductDetailCustomRepresentation(ProductDetail productDetail) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.description = productDetail.getDescription();
        this.specification = productDetail.getSpecification();
        this.lowestPrice = findLowestPrice(productDetail);
        this.totalSales = calcTotalSales(productDetail);
        this.skus = getCustomerSku(productDetail);
        this.selectedOptions = productDetail.getSelectedOptions();

    }

    private List<ProductSkuCustomerRepresentation> getCustomerSku(ProductDetail productDetail) {
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
}
