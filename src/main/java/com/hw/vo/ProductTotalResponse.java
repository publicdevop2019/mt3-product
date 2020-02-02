package com.hw.vo;

import com.hw.entity.ProductSimple;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ProductTotalResponse {
    public List<ProductSimple> productSimpleList;
    public Integer totalPageCount;
    public Long totalProductCount;
}
