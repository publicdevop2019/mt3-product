package com.mt.mall.domain.service;

import com.mt.common.CommonConstant;
import com.mt.common.query.QueryUtility;
import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.ProductAttrSaleImages;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagQuery;
import com.mt.mall.domain.model.tag.Type;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductValidationService {
    public void validate(List<ProductAttrSaleImages> saleImages, ValidationNotificationHandler handler) {
        Set<String> collect = saleImages.stream().map(e -> e.getAttributeSales().split(CommonConstant.QUERY_DELIMITER)[0]).collect(Collectors.toSet());
        Set<Tag> allByQuery = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.tagRepository().tagsOfQuery(query, page), new TagQuery(collect));
        if (allByQuery.size() != collect.size())
            handler.handleError("unable find all sales tags");
        if (allByQuery.stream().anyMatch(e -> !e.getType().equals(Type.SALES_ATTR)))
            handler.handleError("should not have non sales tags");
    }
}
