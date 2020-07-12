package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * copied from user-profile service
 */
@Data
public class ProductValidationCommand {
    private List<ProductOption> selectedOptions;
    private BigDecimal finalPrice;
    private String productId;
    private Set<String> attributesSales;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductValidationCommand product = (ProductValidationCommand) o;
        return
                //use deepEquals for JPA persistentBag workaround, otherwise equals will return incorrect result
                Objects.deepEquals(selectedOptions != null ? selectedOptions.toArray() : new Object[0], product.selectedOptions != null ? product.selectedOptions.toArray() : new Object[0]) &&
                        Objects.equals(finalPrice, product.finalPrice) &&
                        Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedOptions, finalPrice, productId);
    }
}
