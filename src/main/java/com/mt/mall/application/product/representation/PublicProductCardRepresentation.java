package com.mt.mall.application.product.representation;

import com.mt.mall.domain.model.product.Product;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class PublicProductCardRepresentation {
    private Long id;
    private String name;
    public static final String PUBLIC_REP_NAME_LITERAL = "name";
    private String imageUrlSmall;
    private String description;
    private BigDecimal lowestPrice;
    public static final String PUBLIC_REP_PRICE_LITERAL = "lowestPrice";
    private Integer totalSales;
    public static final String PUBLIC_REP_TOTAL_SALES_LITERAL = "totalSales";

    public PublicProductCardRepresentation(Product productDetail) {
        BeanUtils.copyProperties(productDetail, this);
    }


}
