package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.shared.DefaultSumPagedRep;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductAdminSumPagedRep{
    private List<ProductAdminCardRepresentation> data = new ArrayList<>();
    private Long totalItemCount;

    public ProductAdminSumPagedRep(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public ProductAdminSumPagedRep(DefaultSumPagedRep<ProductDetail> select) {
        this.data.addAll(select.getData().stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = select.getTotalItemCount();
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
