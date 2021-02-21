package com.mt.mall.domain.model.product;

import com.mt.common.validate.Validator;
import com.mt.mall.domain.DomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Getter
@NoArgsConstructor
public class ProductAttrSaleImages implements Serializable {
    private static final long serialVersionUID = 1;
    private String attributeSales;
    private LinkedHashSet<String> imageUrls;

    private void setAttributeSales(String attributeSales) {
        this.attributeSales = attributeSales;
    }

    private void setImageUrls(LinkedHashSet<String> imageUrls) {
        Validator.notEmpty(imageUrls);
        imageUrls.forEach(Validator::isHttpUrl);
        this.imageUrls = imageUrls;
    }

    public ProductAttrSaleImages(String attributeSales, LinkedHashSet<String> imageUrls) {
        setAttributeSales(attributeSales);
        setImageUrls(imageUrls);
    }
}
