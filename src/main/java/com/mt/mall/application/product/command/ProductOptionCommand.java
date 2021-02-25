package com.mt.mall.application.product.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ProductOptionCommand {
    private String title;
    private List<OptionItemCommand> options;

    @Data
    @AllArgsConstructor
    public static class OptionItemCommand {
        private String optionValue;
        private String priceVar;
    }
}
