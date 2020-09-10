package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.sku.representation.AppBizSkuCardRep;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public
class AppProductCardRep {
    private Long id;
    private List<ProductOption> selectedOptions;
    private List<AppProductSkuRep> productSkuList;

    public AppProductCardRep(Product productDetail, AppBizSkuApplicationService skuApplicationService) {
        this.id = productDetail.getId();
        this.selectedOptions = productDetail.getSelectedOptions();
        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<AppBizSkuCardRep> appBizSkuCardRepSumPagedRep = skuApplicationService.readByQuery("id:"+String.join(".", collect), null, null);
        this.productSkuList = attrSalesMap.keySet().stream().map(e -> {
            AppProductSkuRep appProductSkuRep = new AppProductSkuRep();
            Long aLong = attrSalesMap.get(e);
            Optional<AppBizSkuCardRep> first = appBizSkuCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
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
