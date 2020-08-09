package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AppProductSumPagedRep extends SumPagedRep<AppProductSumPagedRep.ProductAdminCardRepresentation> {
    public AppProductSumPagedRep(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public AppProductSumPagedRep(SumPagedRep<Product> select) {
        this.data.addAll(select.getData().stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    protected static class ProductAdminCardRepresentation {
        private Long id;
        private List<ProductOption> selectedOptions;
        private List<ProductSku> productSkuList;
        private Integer storageOrder;
        private Integer storageActual;
        private BigDecimal lowestPrice;

        public ProductAdminCardRepresentation(Product productDetail) {
            this.id = productDetail.getId();
            this.selectedOptions = productDetail.getSelectedOptions();
            this.productSkuList = productDetail.getProductSkuList();
            this.storageOrder = productDetail.getStorageOrder();
            this.storageActual = productDetail.getStorageActual();
            this.lowestPrice = productDetail.getLowestPrice();
        }

    }
}
