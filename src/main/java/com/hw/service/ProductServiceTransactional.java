package com.hw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
public class ProductServiceTransactional {
    @Autowired
    private ProductService productService;

    @Transactional
    public void decreaseOrderStorageForMappedProducts(Map<String, String> map) {
        productService.decreaseOrderStorageForMappedProducts.accept(map);
    }
}
