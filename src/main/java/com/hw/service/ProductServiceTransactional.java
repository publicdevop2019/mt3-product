package com.hw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProductServiceTransactional {
    @Autowired
    private ProductService productService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void decreaseOrderStorageForMappedProducts(Map<String, String> map, String optToken) {
        productService.decreaseOrderStorageForMappedProducts.accept(map, optToken);
    }
}
