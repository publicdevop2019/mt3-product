package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * copied from user-profile service
 */
@Data
public class ProductValidationRepresentation {
    private String name;
    private List<ProductOption> selectedOptions;
    private String finalPrice;
    private String imageUrlSmall;
    private String productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductValidationRepresentation product = (ProductValidationRepresentation) o;
        return Objects.equals(name, product.name) &&
                /**
                 * use deepEquals for JPA persistentBag workaround, otherwise equals will return incorrect result
                 */
                Objects.deepEquals(selectedOptions != null ? selectedOptions.toArray() : new Object[0], product.selectedOptions != null ? product.selectedOptions.toArray() : new Object[0]) &&
                Objects.equals(finalPrice, product.finalPrice) &&
                Objects.equals(imageUrlSmall, product.imageUrlSmall) &&
                Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, selectedOptions, finalPrice, imageUrlSmall, productId);
    }
}
