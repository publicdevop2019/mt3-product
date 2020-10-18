package com.hw.aggregate.product.model;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
@Data
public class ProductAttrSaleImages implements Serializable {
    private static final long serialVersionUID = 1;
    private String attributeSales;
    private LinkedHashSet<String> imageUrls;
}
