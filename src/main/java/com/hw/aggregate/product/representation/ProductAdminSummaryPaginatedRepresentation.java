package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductAdminSummaryPaginatedRepresentation {
    private List<ProductAdminCardRepresentation> data = new ArrayList<>();
    private Integer totalPageCount;
    private Long totalItemCount;

    public ProductAdminSummaryPaginatedRepresentation(List<ProductDetail> data, Integer pageSize, Long totalItemCount) {
        this.data.addAll(data.stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList()));
        long l = Math.floorDiv(totalItemCount, pageSize.longValue());
        this.totalPageCount = (int) l + 1;
        this.totalItemCount = totalItemCount;
    }

    @Data
    private static class ProductAdminCardRepresentation {
        private Long id;
        private String name;
        private Integer totalSales;
        private List<BigDecimal> priceList;
        private Set<String> attributesKey;
        private Long startAt;
        private Long endAt;

        public ProductAdminCardRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.totalSales = productDetail.getTotalSales();
            this.priceList = productDetail.getProductSkuList().stream().map(ProductSku::getPrice).collect(Collectors.toList());
            this.attributesKey = productDetail.getAttrKey();
            this.startAt = productDetail.getStartAt();
            this.endAt = productDetail.getEndAt();
        }

    }
}
