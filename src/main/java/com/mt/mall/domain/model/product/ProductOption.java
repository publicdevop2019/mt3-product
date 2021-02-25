package com.mt.mall.domain.model.product;

import com.mt.common.domain.model.validate.Validator;
import com.mt.mall.application.product.command.ProductOptionCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ProductOption {
    public String title;
    public List<OptionItem> options;

    private void setTitle(String title) {
        Validator.notBlank(title);
        Validator.lengthLessThanOrEqualTo(title, 25);
        this.title = title;
    }

    private void setOptions(List<OptionItem> options) {
        Validator.notEmpty(options);
        this.options = options;
    }

    public ProductOption(ProductOptionCommand e) {
        setTitle(e.getTitle());
        setOptions(e.getOptions().stream().map(OptionItem::new).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor
    public static class OptionItem {
        public String optionValue;
        public String priceVar;

        private void setOptionValue(String optionValue) {
            Validator.notBlank(optionValue);
            this.optionValue = optionValue;
        }

        private void setPriceVar(String priceVar) {
            Validator.notBlank(priceVar);
            this.priceVar = priceVar;
        }

        public OptionItem(ProductOptionCommand.OptionItemCommand ee) {
            setOptionValue(ee.getOptionValue());
            setPriceVar(ee.getPriceVar());
        }

        private OptionItem(String optionValue, String priceVar) {
            setOptionValue(optionValue);
            setPriceVar(priceVar);
        }
    }

    public static class ProductOptionConverter implements AttributeConverter<List<ProductOption>, String> {
        @Override
        public String convertToDatabaseColumn(List<ProductOption> productOptions) {
            /**
             *  e.g.
             *  qty:1&1*2&2*3&3,color:white&0.35*black&0.37
             */
            if (productOptions == null)
                return null;
            return productOptions.stream().map(e -> e.title + ":" + e.options.stream().map(el -> el.optionValue + "&" + el.priceVar).collect(Collectors.joining("="))).collect(Collectors.joining(","));
        }

        @Override
        public List<ProductOption> convertToEntityAttribute(String s) {
            if (s == null || s.equals(""))
                return null;
            List<ProductOption> optionList = new ArrayList<>();
            Arrays.stream(s.split(",")).forEach(e -> {
                ProductOption option1 = new ProductOption();
                option1.title = e.split(":")[0];
                String detail = e.split(":")[1];
                String[] split = detail.split("=");
                Arrays.stream(split).forEach(el -> {
                    String[] split1 = el.split("&");
                    OptionItem option = new OptionItem(split1[0], split1[1]);
                    if (option1.options == null)
                        option1.options = new ArrayList<>();
                    option1.options.add(option);
                });
                optionList.add(option1);
            });
            return optionList;
        }
    }
}
