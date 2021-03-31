package com.mt.mall.application.product.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductOptionCommand implements Serializable {
    private String title;
    private List<OptionItemCommand> options;

    @Data
    @AllArgsConstructor
    public static class OptionItemCommand implements Serializable{
        private String optionValue;
        private String priceVar;
    }
}
