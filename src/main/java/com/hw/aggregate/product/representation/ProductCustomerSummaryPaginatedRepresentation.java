package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.exception.NoLowestPriceFoundException;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductCustomerSummaryPaginatedRepresentation {
    private List<ProductSearchRepresentation> data = new ArrayList<>();
    private Integer totalPageCount;
    private Long totalProductCount;

    public ProductCustomerSummaryPaginatedRepresentation(List<ProductDetail> productSimpleList, Integer totalPageCount, Long totalProductCount) {
        this.data.addAll(productSimpleList.stream().map(ProductSearchRepresentation::new).collect(Collectors.toList()));
        this.totalPageCount = totalPageCount;
        this.totalProductCount = totalProductCount;
    }

    @Data
    private static class ProductSearchRepresentation {
        private Long id;
        private String name;
        private String imageUrlSmall;
        private String description;
        private BigDecimal lowestPrice;
        private Integer totalSales;

        public ProductSearchRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.description = productDetail.getDescription();
            if (productDetail.getProductSkuList() != null&& productDetail.getProductSkuList().size() != 0) {
                this.lowestPrice = findLowestPrice(productDetail);
                this.totalSales = calcTotalSales(productDetail);
            } else {
                this.lowestPrice = productDetail.getPrice();
                this.totalSales = productDetail.getSales();
            }
        }

        private Integer calcTotalSales(ProductDetail productDetail) {
            return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
        }

        private BigDecimal findLowestPrice(ProductDetail productDetail) {
            ProductSku productSku = productDetail.getProductSkuList().stream().min(Comparator.comparing(ProductSku::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
            return productSku.getPrice();
        }

    }
}
