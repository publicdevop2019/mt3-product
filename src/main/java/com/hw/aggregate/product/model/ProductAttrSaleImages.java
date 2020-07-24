package com.hw.aggregate.product.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ProductAttrSaleImages implements Serializable {
    private static final long serialVersionUID = 1;
    private String attributeSales;
    private List<String> imageUrls;
}
