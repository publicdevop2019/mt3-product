package com.hw.aggregate.product.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductOption {
    public String title;
    public List<OptionItem> options;
}
