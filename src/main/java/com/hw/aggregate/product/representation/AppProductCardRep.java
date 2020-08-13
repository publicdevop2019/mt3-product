package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.util.List;

@Data
public
class AppProductCardRep {
    private Long id;
    private List<ProductOption> selectedOptions;
    private List<ProductSku> productSkuList;

    public AppProductCardRep(Product productDetail) {
        this.id = productDetail.getId();
        this.selectedOptions = productDetail.getSelectedOptions();
        this.productSkuList = productDetail.getProductSkuList();
    }

}
