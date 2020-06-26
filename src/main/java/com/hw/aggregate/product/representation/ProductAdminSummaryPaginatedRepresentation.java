package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductSku;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAdminSummaryPaginatedRepresentation {
    private List<ProductAdminCardRepresentation> data;
    private Integer totalPageCount;
    private Long totalProductCount;

    public ProductAdminSummaryPaginatedRepresentation(List<ProductDetail> data, Integer totalPageCount, Long totalProductCount) {
        this.data = data.stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList());
        this.totalPageCount = totalPageCount;
        this.totalProductCount = totalProductCount;
    }

    @Data
    private static class ProductAdminCardRepresentation {
        private Long id;
        private String name;
        private Integer totalSales;
        private List<BigDecimal> priceList;

        public ProductAdminCardRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.totalSales = calcTotalSales(productDetail);
            this.priceList = productDetail.getProductSkuList().stream().map(ProductSku::getPrice).collect(Collectors.toList());
        }

        private Integer calcTotalSales(ProductDetail productDetail) {
            return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
        }
    }
}
