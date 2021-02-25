package com.mt.mall.application.product.representation;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductOption;
import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class InternalProductRepresentation {
    private String id;
    private List<ProductOption> selectedOptions;
    private List<AppProductSkuRep> productSkuList;
    private HashMap<String, String> attrSalesMap;

    public InternalProductRepresentation(Object obj) {
        Product product = (Product) obj;
        setId(product.getProductId().getDomainId());
        setSelectedOptions(product.getSelectedOptions());
        setAttrSalesMap(product.getAttrSalesMap());
        HashMap<String, String> attrSalesMap = product.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<Sku> skus = ApplicationServiceRegistry.skuApplicationService().skus("id:" + String.join(".", collect), null, null);
        this.productSkuList = attrSalesMap.keySet().stream().map(e -> {
            AppProductSkuRep appProductSkuRep = new AppProductSkuRep();
            String aLong = attrSalesMap.get(e);
            Optional<Sku> first = skus.getData().stream().filter(ee -> ee.getSkuId().getDomainId().equals(aLong)).findFirst();
            if (first.isPresent()) {
                HashSet<String> strings = new HashSet<>(Arrays.asList(e.split(",")));
                appProductSkuRep.setAttributesSales(strings);
                appProductSkuRep.setPrice(first.get().getPrice());
            }
            return appProductSkuRep;
        }).collect(Collectors.toList());
    }

    @Data
    private static class AppProductSkuRep {
        private Set<String> attributesSales;
        private BigDecimal price;
    }
}
