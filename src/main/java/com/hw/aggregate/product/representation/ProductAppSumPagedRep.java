package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductAppSumPagedRep extends SumPagedRep<ProductAppSumPagedRep.ProductAdminCardRepresentation> {
    public ProductAppSumPagedRep(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public ProductAppSumPagedRep(SumPagedRep<Product> select) {
        this.data.addAll(select.getData().stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    protected static class ProductAdminCardRepresentation {
        private Long id;
        private String name;
        private Integer totalSales;
        private List<BigDecimal> priceList;
        private Set<String> attributesKey;
        private Long startAt;
        private Long endAt;

        public ProductAdminCardRepresentation(Product productDetail) {
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
