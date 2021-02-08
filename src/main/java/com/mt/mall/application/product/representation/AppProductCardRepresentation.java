package com.mt.mall.application.product.representation;

import com.mt.mall.domain.model.product.Product;
import com.mt.mall.domain.model.product.ProductOption;
import com.mt.mall.application.sku.AppBizSkuApplicationService;
import com.mt.mall.application.sku.representation.InternalSkuCardRepresentation;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public
class AppProductCardRepresentation {
    private Long id;
    private List<ProductOption> selectedOptions;
    private List<AppProductSkuRep> productSkuList;
    private HashMap<String, Long> attrSalesMap;

    public AppProductCardRepresentation(Product productDetail, AppBizSkuApplicationService skuApplicationService) {
        BeanUtils.copyProperties(productDetail, this);
        HashMap<String, Long> attrSalesMap = productDetail.getAttrSalesMap();
        Set<String> collect = attrSalesMap.values().stream().map(Object::toString).collect(Collectors.toSet());
        SumPagedRep<InternalSkuCardRepresentation> appBizSkuCardRepSumPagedRep = skuApplicationService.readByQuery("id:" + String.join(".", collect), null, null);
        this.productSkuList = attrSalesMap.keySet().stream().map(e -> {
            AppProductSkuRep appProductSkuRep = new AppProductSkuRep();
            Long aLong = attrSalesMap.get(e);
            Optional<InternalSkuCardRepresentation> first = appBizSkuCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().equals(aLong)).findFirst();
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
